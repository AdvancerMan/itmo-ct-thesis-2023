package ru.itmo.kazakov.autoschedule.nsp.constraints.shift.soft

import ru.itmo.kazakov.autoschedule.helper.NspHelper
import ru.itmo.kazakov.autoschedule.nsp.constraints.NspConstraint
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.AssigneeSchedule
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.ShiftAssignment

class UniformShiftSizeNspConstraint : NspConstraint {

    override fun canAddAssignment(
        newAssignment: ShiftAssignment<*>,
        existingAssignments: List<ShiftAssignment<*>>,
    ): Boolean {
        return true
    }

    override fun calculatePenalty(assigneeSchedule: AssigneeSchedule<*>): Double {
        return DoubleArray(assigneeSchedule.sortedAssignments.size) {
            assigneeSchedule.sortedAssignments[it].shift.stepsCount.toDouble()
        }
            .let { NspHelper.calculateDispersionIndex(it) }
    }
}
