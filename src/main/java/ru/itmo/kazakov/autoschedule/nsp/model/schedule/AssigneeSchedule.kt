package ru.itmo.kazakov.autoschedule.nsp.model.schedule

import ru.itmo.kazakov.autoschedule.nsp.model.schedule.cache.CachedEntity
import ru.itmo.kazakov.autoschedule.nsp.model.team.ShiftAssignee

class AssigneeSchedule<ID>(
    val assignee: ShiftAssignee<ID>,
    assignments: List<ShiftAssignment<ID>>,
) : CachedEntity() {

    val sortedAssignments: List<ShiftAssignment<ID>> = assignments.sortedBy { it.shift.startStepIndex }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AssigneeSchedule<*>

        if (sortedAssignments != other.sortedAssignments) return false

        return true
    }

    override fun hashCode(): Int {
        return sortedAssignments.hashCode()
    }

    override fun toString(): String {
        return "AssigneeSchedule(assignee=$assignee, assignments=$sortedAssignments)"
    }
}
