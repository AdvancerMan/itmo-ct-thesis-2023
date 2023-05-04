package ru.itmo.kazakov.autoschedule.nsp.generator.individual

import org.slf4j.LoggerFactory
import ru.itmo.kazakov.autoschedule.nsp.aggregator.NspFitnessAggregator
import ru.itmo.kazakov.autoschedule.nsp.constraints.checker.AssignmentConstraintsChecker
import ru.itmo.kazakov.autoschedule.nsp.fitness.NspFitnessCalculator
import ru.itmo.kazakov.autoschedule.nsp.generator.breaks.BreaksGenerator
import ru.itmo.kazakov.autoschedule.nsp.model.FitnessType
import ru.itmo.kazakov.autoschedule.nsp.model.ScheduleIndividual
import ru.itmo.kazakov.autoschedule.nsp.model.forecast.Forecast
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.AssigneeSchedule
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.Schedule
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.Shift
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.ShiftAssignment
import ru.itmo.kazakov.autoschedule.nsp.model.team.Team
import java.util.Comparator
import kotlin.math.roundToInt

class GreedyScheduleIndividualGenerator<ID>(

    private val forecast: Forecast,

    private val team: Team<ID>,

    private val fitnessCalculator: NspFitnessCalculator<ID>,

    private val assignmentConstraintsChecker: AssignmentConstraintsChecker,

    private val breaksGenerator: BreaksGenerator,

    private val nspFitnessAggregator: NspFitnessAggregator,
) : IndividualGenerator<ScheduleIndividual<ID>> {

    companion object {
        private val LOG = LoggerFactory.getLogger(GreedyScheduleIndividualGenerator::class.java)
    }

    override fun generateIndividual(): ScheduleIndividual<ID> {
        val shiftAssignments = team.members
            .flatMap { assignee ->
                forecast.stepsForecast.indices
                    .filter { assignee.isShiftStartStepIndex(it) }
                    .flatMap { startStep ->
                        (assignee.minStepsShift..assignee.maxStepsShift).map { shiftSize ->
                            val shift = Shift.ofStepsCount(startStep, shiftSize)
                            ShiftAssignment(shift, assignee, breaksGenerator.generateSortedBreaks(shift))
                        }
                    }
            }
            .toMutableList()

        val emptySchedule = Schedule<ID>(emptyMap())
        var resultSchedule = ScheduleIndividual(emptySchedule, fitnessCalculator.calculateFitness(emptySchedule))
        val initialAssignmentsSize = shiftAssignments.size
        while (shiftAssignments.isNotEmpty()) {
            LOG.debug("Processing {} assignments... ({}% done)", shiftAssignments.size, (1 - shiftAssignments.size.toDouble() / initialAssignmentsSize.toDouble()).times(10000).roundToInt().toDouble().div(100))

            shiftAssignments.removeIf {
                val existingAssignments = resultSchedule.schedule.assignments[it.assignee]?.sortedAssignments
                    ?: emptyList()
                !assignmentConstraintsChecker.canAddAssignment(it, existingAssignments)
            }

            shiftAssignments
                .map { withAddedAssignment(resultSchedule, it) }
                .minWithOrNull(
                    Comparator.comparingDouble<ScheduleIndividual<ID>> { it.getFitness(FitnessType.HARD_CONSTRAINTS_PENALTY) }
                        .thenComparingDouble { nspFitnessAggregator.aggregateFitness(it) },
                )
                ?.also { resultSchedule = it }
        }

        return resultSchedule
    }

    private fun withAddedAssignment(
        originalIndividual: ScheduleIndividual<ID>,
        assignment: ShiftAssignment<ID>,
    ): ScheduleIndividual<ID> {
        val oldAssigneeSchedule = originalIndividual.schedule.assignments[assignment.assignee]?.sortedAssignments
            ?: emptyList()
        val newAssigneeSchedule = AssigneeSchedule(assignment.assignee, oldAssigneeSchedule + assignment)

        val newSchedule = originalIndividual.schedule.assignments
            .toMutableMap()
            .apply { put(assignment.assignee, newAssigneeSchedule) }
            .let { Schedule(it) }

        val fitness = fitnessCalculator.recalculateFitness(
            originalIndividual,
            newSchedule,
            emptyList(),
            listOf(assignment),
        )
        return ScheduleIndividual(newSchedule, fitness)
    }
}
