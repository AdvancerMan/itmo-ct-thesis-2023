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
import ru.itmo.kazakov.autoschedule.nsp.model.team.Team
import kotlin.random.Random

class UniformScheduleIndividualGenerator<ID>(

    private val forecast: Forecast,

    private val team: Team<ID>,

    private val fitnessCalculator: NspFitnessCalculator<ID>,

    private val assignmentConstraintsChecker: AssignmentConstraintsChecker,

    private val breaksGenerator: BreaksGenerator,

    private val random: Random,
) : IndividualGenerator<ScheduleIndividual<ID>> {

    override fun generateIndividual(): ScheduleIndividual<ID> {
        val assignments = team.members.associateWith { assignee ->
            val initialCapacity = (team.teamConstraintsInfo.forecastSteps + team.teamConstraintsInfo.stepsInDay - 1)
                .div(team.teamConstraintsInfo.stepsInDay)
            val currentAssignments: MutableList<ShiftAssignment<ID>> = ArrayList(initialCapacity)

            forecast
                .stepsForecast
                .indices
                .filter { assignee.isShiftStartStepIndex(it) }
                .shuffled(random)
                .forEach { firstStepIndex ->
                    val shift = Shift.ofStepsCount(firstStepIndex, assignee.minStepsShift)
                    val assignment = ShiftAssignment(shift, assignee, breaksGenerator.generateSortedBreaks(shift))
                    if (assignmentConstraintsChecker.canAddAssignment(assignment, currentAssignments.toList())) {
                        currentAssignments.add(assignment)
                    }
                }

            for (addedSteps in currentAssignments.size * assignee.minStepsShift until assignee.stepsNorm) {
                var hasAddedStep = false
                for (triedAssignments in 0 until currentAssignments.size) {
                    val randomAssignment = currentAssignments
                        .subList(0, currentAssignments.size - triedAssignments)
                        .random(random)
                    currentAssignments.remove(randomAssignment)

                    val shift = randomAssignment.shift.copy(
                        endStepIndex = randomAssignment.shift.endStepIndex + 1,
                    )
                    val extendedAssignment = randomAssignment.copy(
                        shift = shift,
                        sortedBreaks = breaksGenerator.generateSortedBreaks(shift),
                    )
                    if (assignmentConstraintsChecker.canAddAssignment(extendedAssignment, currentAssignments)) {
                        currentAssignments.add(extendedAssignment)
                        hasAddedStep = true
                        break
                    } else {
                        currentAssignments.add(randomAssignment)
                    }
                }

                if (!hasAddedStep) {
                    break
                }
            }
            AssigneeSchedule(assignee, currentAssignments)
        }

        val schedule = Schedule(assignments)
        return ScheduleIndividual(
            schedule,
            fitnessCalculator.calculateFitness(schedule),
        )
    }
}
