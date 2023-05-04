package ru.itmo.kazakov.autoschedule.nsp.generator.individual

import ru.itmo.kazakov.autoschedule.nsp.constraints.checker.AssignmentConstraintsChecker
import ru.itmo.kazakov.autoschedule.nsp.fitness.NspFitnessCalculator
import ru.itmo.kazakov.autoschedule.nsp.generator.breaks.BreaksGenerator
import ru.itmo.kazakov.autoschedule.nsp.model.ScheduleIndividual
import ru.itmo.kazakov.autoschedule.nsp.model.forecast.Forecast
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.AssigneeSchedule
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.Schedule
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.Shift
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.ShiftAssignment
import ru.itmo.kazakov.autoschedule.nsp.model.team.ShiftAssignee
import ru.itmo.kazakov.autoschedule.nsp.model.team.Team
import kotlin.math.min
import kotlin.random.Random

class RandomConstraintsCheckingScheduleIndividualGenerator<ID>(

    private val forecast: Forecast,

    private val team: Team<ID>,

    private val fitnessCalculator: NspFitnessCalculator<ID>,

    private val assignmentConstraintsChecker: AssignmentConstraintsChecker,

    private val breaksGenerator: BreaksGenerator,

    private val random: Random,
) : IndividualGenerator<ScheduleIndividual<ID>> {

    override fun generateIndividual(): ScheduleIndividual<ID> {
        val assignments = team.members.associateWith { assignee ->
            val currentAssignments = mutableListOf<ShiftAssignment<ID>>()
            forecast
                .stepsForecast
                .indices
                .filter { assignee.isShiftStartStepIndex(it) }
                .shuffled(random)
                .forEach { firstStepIndex -> tryAddShift(assignee, firstStepIndex, currentAssignments) }
            AssigneeSchedule(assignee, currentAssignments)
        }

        val schedule = Schedule(assignments)
        return ScheduleIndividual(
            schedule,
            fitnessCalculator.calculateFitness(schedule),
        )
    }

    private fun tryAddShift(
        assignee: ShiftAssignee<ID>,
        firstStepIndex: Int,
        assignments: MutableList<ShiftAssignment<ID>>,
    ) {
        val stepsCountFrom = assignee.minStepsShift
        val stepsCountTo = min(forecast.size - firstStepIndex, assignee.maxStepsShift)
        if (stepsCountFrom > stepsCountTo) {
            return
        }

        val stepsCountCandidatesSize = (stepsCountFrom..stepsCountTo)
            .indexOfFirst {
                assignee.countForbiddenStepIndices(firstStepIndex until firstStepIndex + it) > 0
            }
            .takeIf { it != -1 }
            ?: (stepsCountTo - stepsCountFrom + 1)
        if (stepsCountCandidatesSize == 0) {
            return
        }

        val stepsCountCandidates = IntArray(stepsCountCandidatesSize) { stepsCountFrom + it }
        stepsCountCandidates.shuffle(random)

        stepsCountCandidates.forEach { stepsCount ->
            val shift = Shift.ofStepsCount(firstStepIndex, stepsCount)
            val shiftAssignment = ShiftAssignment(shift, assignee, breaksGenerator.generateSortedBreaks(shift))

            val canCreateShift = assignmentConstraintsChecker.canAddAssignment(shiftAssignment, assignments)

            if (!canCreateShift) {
                return@forEach
            }

            assignments.add(shiftAssignment)
            return
        }
    }
}
