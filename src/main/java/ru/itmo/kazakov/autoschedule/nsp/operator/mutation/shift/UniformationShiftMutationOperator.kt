package ru.itmo.kazakov.autoschedule.nsp.operator.mutation.shift

import ru.itmo.kazakov.autoschedule.nsp.fitness.NspFitnessCalculator
import ru.itmo.kazakov.autoschedule.nsp.generator.breaks.BreaksGenerator
import ru.itmo.kazakov.autoschedule.nsp.model.ScheduleIndividual
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.AssigneeSchedule
import ru.itmo.kazakov.autoschedule.nsp.model.team.ShiftAssignee
import ru.itmo.kazakov.autoschedule.nsp.operator.mutation.RandomUserScheduleMutationOperator
import kotlin.random.Random

class UniformationShiftMutationOperator<ID>(

    fitnessCalculator: NspFitnessCalculator<ID>,

    random: Random,

    private val breaksGenerator: BreaksGenerator,
) : RandomUserScheduleMutationOperator<ID>(
    fitnessCalculator,
    random,
) {

    override fun selectUsersForMutation(individual: ScheduleIndividual<ID>): Collection<ShiftAssignee<ID>> {
        return individual.schedule.assignments
            .filter { (_, assignments) -> assignments.sortedAssignments.map { it.shift.stepsCount }.toSet().size > 1 }
            .keys
    }

    override fun mutateAssigneeSchedule(assigneeSchedule: AssigneeSchedule<ID>): AssigneeSchedule<ID> {
        if (assigneeSchedule.sortedAssignments.size < 2) {
            return assigneeSchedule
        }

        var (moveStepFromIndex, moveStepToIndex) = if (random.nextBoolean()) {
            chooseRandomMinMaxSwapIndices(assigneeSchedule)
        } else {
            chooseRandomSwapIndices(assigneeSchedule)
        }

        if (moveStepFromIndex == moveStepToIndex) {
            return assigneeSchedule
        }

        if (assigneeSchedule.sortedAssignments[moveStepFromIndex].shift.stepsCount < assigneeSchedule.sortedAssignments[moveStepToIndex].shift.stepsCount) {
            val tmp = moveStepFromIndex
            moveStepFromIndex = moveStepToIndex
            moveStepToIndex = tmp
        }

        val scheduleCopy = assigneeSchedule.sortedAssignments.toMutableList()
        scheduleCopy[moveStepFromIndex].let {
            val newShift = it.shift.copy(
                endStepIndex = it.shift.endStepIndex - 1,
            )
            scheduleCopy[moveStepFromIndex] = it.copy(
                shift = newShift,
                sortedBreaks = breaksGenerator.generateSortedBreaks(newShift),
            )
        }
        scheduleCopy[moveStepToIndex].let {
            val newShift = it.shift.copy(
                endStepIndex = it.shift.endStepIndex + 1,
            )
            scheduleCopy[moveStepToIndex] = it.copy(
                shift = newShift,
                sortedBreaks = breaksGenerator.generateSortedBreaks(newShift),
            )
        }

        return AssigneeSchedule(assigneeSchedule.assignee, scheduleCopy)
    }

    private fun chooseRandomMinMaxSwapIndices(assigneeSchedule: AssigneeSchedule<ID>): SwapIndices {
        val maxShiftSize = assigneeSchedule.sortedAssignments.maxOf { it.shift.stepsCount }
        val minShiftSize = assigneeSchedule.sortedAssignments.minOf { it.shift.stepsCount }

        val moveStepFromIndex = assigneeSchedule.sortedAssignments.indices
            .filter { assigneeSchedule.sortedAssignments[it].shift.stepsCount == maxShiftSize }
            .random(random)

        val moveStepToIndex = assigneeSchedule.sortedAssignments.indices
            .filter { assigneeSchedule.sortedAssignments[it].shift.stepsCount == minShiftSize }
            .random(random)

        return SwapIndices(moveStepFromIndex, moveStepToIndex)
    }

    private fun chooseRandomSwapIndices(assigneeSchedule: AssigneeSchedule<ID>): SwapIndices {
        val from = random.nextInt(assigneeSchedule.sortedAssignments.size)
        val rawTo = random.nextInt(assigneeSchedule.sortedAssignments.size - 1)
        val to = if (from <= rawTo) rawTo + 1 else rawTo
        return SwapIndices(from, to)
    }

    private data class SwapIndices(
        val from: Int,
        val to: Int,
    )
}
