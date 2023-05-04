package ru.itmo.kazakov.autoschedule.nsp.constraints.shift.hard

import ru.itmo.kazakov.autoschedule.helper.NspHelper
import ru.itmo.kazakov.autoschedule.nsp.constraints.NspConstraint
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.AssigneeSchedule
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.ShiftAssignment

class ShiftIsInPeriodNspConstraint(

    private val forecastSteps: Int,
) : NspConstraint {

    override fun canAddAssignment(
        newAssignment: ShiftAssignment<*>,
        existingAssignments: List<ShiftAssignment<*>>,
    ): Boolean {
        return NspHelper.intersectionSize(0 until forecastSteps, newAssignment.shift.steps) > 0
    }

    override fun calculatePenalty(assigneeSchedule: AssigneeSchedule<*>): Double {
        if (assigneeSchedule.sortedAssignments.isEmpty()) {
            return 0.0
        }

        return assigneeSchedule.sortedAssignments
            .count { !canAddAssignment(it, assigneeSchedule.sortedAssignments) }
            .toDouble()
            .div(assigneeSchedule.sortedAssignments.size)
    }
}
