package ru.itmo.kazakov.autoschedule.nsp.operator.mutation.breaks

import ru.itmo.kazakov.autoschedule.helper.NspHelper
import ru.itmo.kazakov.autoschedule.nsp.fitness.NspFitnessCalculator
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.AssigneeSchedule
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.ShiftAssignment
import ru.itmo.kazakov.autoschedule.nsp.operator.mutation.RandomShiftAssignmentMutationOperator
import kotlin.math.sign
import kotlin.random.Random

class MoveShiftBreakMutationOperator<ID>(

    fitnessCalculator: NspFitnessCalculator<ID>,

    random: Random,
) : RandomShiftAssignmentMutationOperator<ID>(
    fitnessCalculator,
    random,
) {

    override fun mutateShiftAssignment(
        assignmentToMutate: ShiftAssignment<ID>,
        assigneeSchedule: AssigneeSchedule<ID>
    ): ShiftAssignment<ID> {
        if (assignmentToMutate.sortedBreaks.isEmpty()) {
            return assignmentToMutate
        }

        val breakToMove = random.nextInt(assignmentToMutate.sortedBreaks.size)
        val movedBreakStart = assignmentToMutate.sortedBreaks[breakToMove].breakStartInShift
        val moveDelta = if (random.nextBoolean() || movedBreakStart == 0) 1 else -1
        val resultBreaks = MutableList(assignmentToMutate.sortedBreaks.size) {
            if (it != breakToMove) {
                return@MutableList assignmentToMutate.sortedBreaks[it]
            }

            assignmentToMutate.sortedBreaks[it].copy(
                breakStartInShift = assignmentToMutate.sortedBreaks[it].breakStartInShift + moveDelta
            )
        }

        NspHelper.sizedSubList(resultBreaks, breakToMove, moveDelta + 1 * moveDelta.sign)
            .sortBy { it.breakStartInShift }

        return assignmentToMutate.copy(sortedBreaks = resultBreaks)
    }
}
