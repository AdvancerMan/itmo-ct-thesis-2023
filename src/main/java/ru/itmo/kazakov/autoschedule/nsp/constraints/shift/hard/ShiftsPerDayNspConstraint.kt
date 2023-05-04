package ru.itmo.kazakov.autoschedule.nsp.constraints.shift.hard

import ru.itmo.kazakov.autoschedule.nsp.constraints.NspConstraint
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.AssigneeSchedule
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.ShiftAssignment

class ShiftsPerDayNspConstraint : NspConstraint {

    override fun canAddAssignment(
        newAssignment: ShiftAssignment<*>,
        existingAssignments: List<ShiftAssignment<*>>,
    ): Boolean {
        val newDayId = newAssignment.assignee.getDayIdByStepIndex(newAssignment.shift.startStepIndex)
        return existingAssignments
            .all { newAssignment.assignee.getDayIdByStepIndex(it.shift.startStepIndex) != newDayId }
    }

    override fun calculatePenalty(assigneeSchedule: AssigneeSchedule<*>): Double {
        if (assigneeSchedule.sortedAssignments.isEmpty()) {
            return 0.0
        }

        return assigneeSchedule.sortedAssignments
            .distinctBy { assigneeSchedule.assignee.getDayIdByStepIndex(it.shift.startStepIndex) }
            .size
            .toDouble()
            .div(assigneeSchedule.sortedAssignments.size)
            .let { 1 - it }
    }
}
