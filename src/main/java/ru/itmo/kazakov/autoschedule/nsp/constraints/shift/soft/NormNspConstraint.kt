package ru.itmo.kazakov.autoschedule.nsp.constraints.shift.soft

import ru.itmo.kazakov.autoschedule.nsp.constraints.NspConstraint
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.AssigneeSchedule
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.ShiftAssignment
import kotlin.math.abs

class NormNspConstraint : NspConstraint {

    override fun canAddAssignment(
        newAssignment: ShiftAssignment<*>,
        existingAssignments: List<ShiftAssignment<*>>,
    ): Boolean {
        return existingAssignments
            .sumOf { it.shift.stepsCount }
            .plus(newAssignment.shift.stepsCount)
            .let { it <= newAssignment.assignee.stepsNorm }
    }

    override fun calculatePenalty(assigneeSchedule: AssigneeSchedule<*>): Double {
        return assigneeSchedule.sortedAssignments
            .sumOf { it.shift.stepsCount }
            .let { abs(1.0 - it.toDouble() / assigneeSchedule.assignee.stepsNorm) }
    }
}
