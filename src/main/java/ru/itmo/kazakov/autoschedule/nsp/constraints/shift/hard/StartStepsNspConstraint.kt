package ru.itmo.kazakov.autoschedule.nsp.constraints.shift.hard

import ru.itmo.kazakov.autoschedule.nsp.constraints.NspConstraint
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.AssigneeSchedule
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.ShiftAssignment

class StartStepsNspConstraint : NspConstraint {

    override fun canAddAssignment(
        newAssignment: ShiftAssignment<*>,
        existingAssignments: List<ShiftAssignment<*>>,
    ): Boolean {
        return newAssignment.assignee.isShiftStartStepIndex(newAssignment.shift.startStepIndex)
    }

    override fun calculatePenalty(assigneeSchedule: AssigneeSchedule<*>): Double {
        if (assigneeSchedule.sortedAssignments.isEmpty()) {
            return 0.0
        }

        return assigneeSchedule.sortedAssignments
            .count { !assigneeSchedule.assignee.isShiftStartStepIndex(it.shift.startStepIndex) }
            .toDouble()
            .div(assigneeSchedule.sortedAssignments.size)
    }
}
