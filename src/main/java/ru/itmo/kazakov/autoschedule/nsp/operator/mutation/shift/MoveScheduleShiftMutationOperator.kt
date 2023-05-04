package ru.itmo.kazakov.autoschedule.nsp.operator.mutation.shift

import ru.itmo.kazakov.autoschedule.nsp.fitness.NspFitnessCalculator
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.AssigneeSchedule
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.ShiftAssignment
import ru.itmo.kazakov.autoschedule.nsp.operator.mutation.RandomShiftAssignmentMutationOperator
import kotlin.random.Random

class MoveScheduleShiftMutationOperator<ID>(

    fitnessCalculator: NspFitnessCalculator<ID>,

    random: Random,

    private val forecastStepsCount: Int,
) : RandomShiftAssignmentMutationOperator<ID>(
    fitnessCalculator,
    random,
) {

    override fun mutateShiftAssignment(
        assignmentToMutate: ShiftAssignment<ID>,
        assigneeSchedule: AssigneeSchedule<ID>
    ): ShiftAssignment<ID> {
        val moveDelta = if (random.nextBoolean()) 1 else -1
        val shiftToMutate = assignmentToMutate.shift

        var allowedStartMoveDelta = 0
        for (i in 1 until forecastStepsCount) {
            if (assignmentToMutate.assignee.isShiftStartStepIndex(shiftToMutate.startStepIndex + moveDelta * i)) {
                allowedStartMoveDelta = moveDelta * i
                break
            }
        }

        return assignmentToMutate.copy(
            shiftToMutate.copy(
                startStepIndex = shiftToMutate.startStepIndex + allowedStartMoveDelta,
                endStepIndex = shiftToMutate.endStepIndex + allowedStartMoveDelta,
            )
        )
    }
}
