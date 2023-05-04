package ru.itmo.kazakov.autoschedule.nsp.operator.mutation.shift

import ru.itmo.kazakov.autoschedule.nsp.constraints.checker.AssignmentConstraintsChecker
import ru.itmo.kazakov.autoschedule.nsp.fitness.NspFitnessCalculator
import ru.itmo.kazakov.autoschedule.nsp.generator.breaks.BreaksGenerator
import ru.itmo.kazakov.autoschedule.nsp.model.forecast.Forecast
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.AssigneeSchedule
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.Shift
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.ShiftAssignment
import ru.itmo.kazakov.autoschedule.nsp.operator.mutation.RandomUserScheduleMutationOperator
import kotlin.random.Random

class AddScheduleShiftMutationOperator<ID>(

    private val forecast: Forecast,

    fitnessCalculator: NspFitnessCalculator<ID>,

    private val assignmentConstraintsChecker: AssignmentConstraintsChecker,

    private val breaksGenerator: BreaksGenerator,

    random: Random,
) : RandomUserScheduleMutationOperator<ID>(
    fitnessCalculator,
    random,
) {

    override fun mutateAssigneeSchedule(assigneeSchedule: AssigneeSchedule<ID>): AssigneeSchedule<ID> {
        return (0..forecast.size - assigneeSchedule.assignee.minStepsShift)
            .asSequence()
            .filter { assigneeSchedule.assignee.isShiftStartStepIndex(it) }
            .shuffled(random)
            .map {
                val shift = Shift.ofStepsCount(it, assigneeSchedule.assignee.minStepsShift)
                ShiftAssignment(shift, assigneeSchedule.assignee, breaksGenerator.generateSortedBreaks(shift))
            }
            .firstOrNull { assignmentConstraintsChecker.canAddAssignment(it, assigneeSchedule.sortedAssignments) }
            ?.let { AssigneeSchedule(assigneeSchedule.assignee, assigneeSchedule.sortedAssignments + it) }
            ?: assigneeSchedule
    }
}
