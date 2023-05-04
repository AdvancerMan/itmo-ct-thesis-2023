package ru.itmo.kazakov.autoschedule.nsp.constraints.breaks

import ru.itmo.kazakov.autoschedule.nsp.model.schedule.AssigneeSchedule
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.Schedule
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.ShiftAssignment
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.cache.AssigneeCacheKey
import ru.itmo.kazakov.autoschedule.nsp.model.team.ShiftAssignee

abstract class ShiftLocalBreaksNspConstraint : BreaksNspConstraint {

    protected abstract fun checkConstraint(shiftAssignment: ShiftAssignment<*>): Boolean

    protected abstract fun calculatePenalty(shiftAssignment: ShiftAssignment<*>): Double

    final override fun canAddAssignment(
        newAssignment: ShiftAssignment<*>,
        existingAssignments: List<ShiftAssignment<*>>,
    ): Boolean {
        return checkConstraint(newAssignment)
    }

    final override fun calculatePenalty(assigneeSchedule: AssigneeSchedule<*>): Double {
        if (assigneeSchedule.sortedAssignments.isEmpty()) {
            return 0.0
        }

        return assigneeSchedule.sortedAssignments
            .sumOf { calculatePenalty(it) }
            .div(assigneeSchedule.sortedAssignments.size)
    }

    final override fun <ID> calculatePenaltyDelta(
        assignee: ShiftAssignee<ID>,
        originalSchedule: Schedule<ID>,
        changedSchedule: Schedule<ID>,
        removedAssignments: List<ShiftAssignment<ID>>,
        addedAssignments: List<ShiftAssignment<ID>>,
    ): Double {
        val oldSchedule = originalSchedule.assignments.getOrDefault(assignee, AssigneeSchedule(assignee, emptyList()))
        val newSchedule = changedSchedule.assignments.getOrDefault(assignee, AssigneeSchedule(assignee, emptyList()))

        val cacheKey = AssigneeCacheKey.NspConstraintPenaltyAssigneeCacheKey(this)
        val oldPenalty = oldSchedule.computeIfAbsentCacheValue(cacheKey) { this.calculatePenalty(oldSchedule) }

        val removedAssignmentsPenaltyDelta = removedAssignments.sumOf { calculatePenalty(it) }
        val addedAssignmentsPenaltyDelta = addedAssignments.sumOf { calculatePenalty(it) }

        val nonNormalizedOldPenalty = oldPenalty * oldSchedule.sortedAssignments.size
        val nonNormalizedNewPenalty = nonNormalizedOldPenalty - removedAssignmentsPenaltyDelta + addedAssignmentsPenaltyDelta
        val newPenalty = nonNormalizedNewPenalty / newSchedule.sortedAssignments.size

        newSchedule.computeIfAbsentCacheValue(cacheKey) { newPenalty }

        return newPenalty - oldPenalty
    }
}
