package ru.itmo.kazakov.autoschedule.nsp.constraints.shift.hard

import ru.itmo.kazakov.autoschedule.helper.PenaltyHelper
import ru.itmo.kazakov.autoschedule.nsp.constraints.NspConstraint
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.AssigneeSchedule
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.ShiftAssignment

class ShiftSizeNspConstraint : NspConstraint {

    override fun canAddAssignment(
        newAssignment: ShiftAssignment<*>,
        existingAssignments: List<ShiftAssignment<*>>,
    ): Boolean {
        return newAssignment.shift.stepsCount in newAssignment.assignee.minStepsShift..newAssignment.assignee.maxStepsShift
    }

    override fun calculatePenalty(assigneeSchedule: AssigneeSchedule<*>): Double {
        if (assigneeSchedule.sortedAssignments.isEmpty()) {
            return 0.0
        }

        return assigneeSchedule
            .sortedAssignments
            .sumOf { shift ->
                val minStepsShift = assigneeSchedule.assignee.minStepsShift
                val maxStepsShift = assigneeSchedule.assignee.maxStepsShift
                PenaltyHelper.calculateEntityLengthPenalty(shift.shift.stepsCount, minStepsShift, maxStepsShift)
            }
            .div(assigneeSchedule.sortedAssignments.size)
    }
}
