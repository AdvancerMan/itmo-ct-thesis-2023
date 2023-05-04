package ru.itmo.kazakov.autoschedule.nsp.operator.mutation

import ru.itmo.kazakov.autoschedule.nsp.fitness.NspFitnessCalculator
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.AssigneeSchedule
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.ShiftAssignment
import kotlin.random.Random

abstract class RandomShiftAssignmentMutationOperator<ID>(

    fitnessCalculator: NspFitnessCalculator<ID>,

    random: Random,
) : RandomUserScheduleMutationOperator<ID>(
    fitnessCalculator,
    random,
) {

    abstract fun mutateShiftAssignment(
        assignmentToMutate: ShiftAssignment<ID>,
        assigneeSchedule: AssigneeSchedule<ID>,
    ): ShiftAssignment<ID>

    override fun mutateAssigneeSchedule(assigneeSchedule: AssigneeSchedule<ID>): AssigneeSchedule<ID> {
        return if (assigneeSchedule.sortedAssignments.isEmpty()) {
            assigneeSchedule
        } else {
            val randomAssignment = assigneeSchedule.sortedAssignments.random(random)
            val mutatedAssignment = mutateShiftAssignment(randomAssignment, assigneeSchedule)
            AssigneeSchedule(
                assigneeSchedule.assignee,
                assigneeSchedule.sortedAssignments - randomAssignment + mutatedAssignment,
            )
        }
    }
}
