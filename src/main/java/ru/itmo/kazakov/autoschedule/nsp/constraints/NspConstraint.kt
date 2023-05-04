package ru.itmo.kazakov.autoschedule.nsp.constraints

import ru.itmo.kazakov.autoschedule.nsp.model.schedule.AssigneeSchedule
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.Schedule
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.ShiftAssignment
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.cache.AssigneeCacheKey
import ru.itmo.kazakov.autoschedule.nsp.model.team.ShiftAssignee

interface NspConstraint {

    fun canAddAssignment(
        newAssignment: ShiftAssignment<*>,
        existingAssignments: List<ShiftAssignment<*>>,
    ): Boolean

    fun calculatePenalty(assigneeSchedule: AssigneeSchedule<*>): Double

    fun <ID> calculatePenaltyDelta(
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
        val newPenalty = newSchedule.computeIfAbsentCacheValue(cacheKey) { this.calculatePenalty(newSchedule) }

        return newPenalty - oldPenalty
    }

    fun calculateFitnessRepresentation(assigneeSchedule: AssigneeSchedule<*>): Map<String, Double> {
        return mapOf(this::class.java.simpleName to calculatePenalty(assigneeSchedule))
    }
}
