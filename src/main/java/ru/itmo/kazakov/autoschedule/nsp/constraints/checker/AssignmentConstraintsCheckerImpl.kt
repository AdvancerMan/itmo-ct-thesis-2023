package ru.itmo.kazakov.autoschedule.nsp.constraints.checker

import ru.itmo.kazakov.autoschedule.nsp.constraints.NspConstraint
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.ShiftAssignment

class AssignmentConstraintsCheckerImpl(

    private val constraints: List<NspConstraint>
) : AssignmentConstraintsChecker {

    override fun canAddAssignment(
        newAssignment: ShiftAssignment<*>,
        existingAssignments: List<ShiftAssignment<*>>,
    ): Boolean {
        return constraints.all { it.canAddAssignment(newAssignment, existingAssignments) }
    }
}
