package ru.itmo.kazakov.autoschedule.nsp.constraints.checker

import ru.itmo.kazakov.autoschedule.nsp.model.schedule.ShiftAssignment

interface AssignmentConstraintsChecker {
    fun canAddAssignment(
        newAssignment: ShiftAssignment<*>,
        existingAssignments: List<ShiftAssignment<*>>,
    ): Boolean
}
