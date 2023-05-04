package ru.itmo.kazakov.autoschedule.nsp.operator.mutation.shift

import ru.itmo.kazakov.autoschedule.nsp.fitness.NspFitnessCalculator
import ru.itmo.kazakov.autoschedule.nsp.generator.breaks.BreaksGenerator
import ru.itmo.kazakov.autoschedule.nsp.model.ScheduleIndividual
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.AssigneeSchedule
import ru.itmo.kazakov.autoschedule.nsp.model.team.ShiftAssignee
import ru.itmo.kazakov.autoschedule.nsp.operator.mutation.RandomUserScheduleMutationOperator
import kotlin.random.Random

class FixNormShiftMutationOperator<ID>(

    fitnessCalculator: NspFitnessCalculator<ID>,

    random: Random,

    private val breaksGenerator: BreaksGenerator,
) : RandomUserScheduleMutationOperator<ID>(
    fitnessCalculator,
    random,
) {

    override fun selectUsersForMutation(individual: ScheduleIndividual<ID>): Collection<ShiftAssignee<ID>> {
        return individual.schedule.assignments
            .filter { (assignee, assignments) ->
                assignments.sortedAssignments.isNotEmpty() && assignments.sortedAssignments.sumOf { it.shift.stepsCount } != assignee.stepsNorm
            }
            .keys
    }

    override fun mutateAssigneeSchedule(assigneeSchedule: AssigneeSchedule<ID>): AssigneeSchedule<ID> {
        val scheduleSteps = assigneeSchedule.sortedAssignments.sumOf { it.shift.stepsCount }
        val shouldExtendShift = scheduleSteps <= assigneeSchedule.assignee.stepsNorm

        val indexToMutate = if (random.nextBoolean()) {
            if (shouldExtendShift) {
                chooseRandomMinShiftIndex(assigneeSchedule)
            } else {
                chooseRandomMaxShiftIndex(assigneeSchedule)
            }
        } else {
            chooseRandomIndex(assigneeSchedule)
        }

        return if (shouldExtendShift) {
            changeShiftSize(assigneeSchedule, indexToMutate, 1)
        } else {
            changeShiftSize(assigneeSchedule, indexToMutate, -1)
        }
    }

    private fun changeShiftSize(
        assigneeSchedule: AssigneeSchedule<ID>,
        indexToMutate: Int,
        delta: Int,
    ): AssigneeSchedule<ID> {
        val scheduleCopy = assigneeSchedule.sortedAssignments.toMutableList()
        scheduleCopy[indexToMutate].let {
            val newStartStepIndex = it.shift.startStepIndex - delta
            val newEndStepIndex = it.shift.endStepIndex + delta

            val newShift = if (random.nextBoolean() && it.assignee.isShiftStartStepIndex(newStartStepIndex)) {
                it.shift.copy(
                    startStepIndex = newStartStepIndex,
                )
            } else {
                it.shift.copy(
                    endStepIndex = newEndStepIndex,
                )
            }

            scheduleCopy[indexToMutate] = it.copy(
                shift = newShift,
                sortedBreaks = breaksGenerator.generateSortedBreaks(newShift),
            )
        }

        return AssigneeSchedule(assigneeSchedule.assignee, scheduleCopy)
    }

    private fun chooseRandomMinShiftIndex(assigneeSchedule: AssigneeSchedule<ID>): Int {
        val minShiftSize = assigneeSchedule.sortedAssignments.minOf { it.shift.stepsCount }

        return assigneeSchedule.sortedAssignments.indices
            .filter { assigneeSchedule.sortedAssignments[it].shift.stepsCount == minShiftSize }
            .random(random)
    }

    private fun chooseRandomMaxShiftIndex(assigneeSchedule: AssigneeSchedule<ID>): Int {
        val maxShiftSize = assigneeSchedule.sortedAssignments.maxOf { it.shift.stepsCount }

        return assigneeSchedule.sortedAssignments.indices
            .filter { assigneeSchedule.sortedAssignments[it].shift.stepsCount == maxShiftSize }
            .random(random)
    }

    private fun chooseRandomIndex(assigneeSchedule: AssigneeSchedule<ID>): Int {
        return random.nextInt(assigneeSchedule.sortedAssignments.size)
    }
}
