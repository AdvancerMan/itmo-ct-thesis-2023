package ru.itmo.kazakov.autoschedule.nsp.constraints.shift.hard

import ru.itmo.kazakov.autoschedule.nsp.constraints.NspConstraint
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.AssigneeSchedule
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.ShiftAssignment

class ForbiddenStepsNspConstraint : NspConstraint {

    override fun canAddAssignment(
        newAssignment: ShiftAssignment<*>,
        existingAssignments: List<ShiftAssignment<*>>,
    ): Boolean {
        return newAssignment.assignee.countForbiddenStepIndices(newAssignment.shift.steps) == 0
    }

    override fun calculatePenalty(assigneeSchedule: AssigneeSchedule<*>): Double {
        val steps = assigneeSchedule.sortedAssignments.sumOf { it.shift.stepsCount }

        if (steps == 0) {
            return 0.0
        }

        val forbiddenSteps = assigneeSchedule
            .sortedAssignments
            .sumOf { assignment -> assigneeSchedule.assignee.countForbiddenStepIndices(assignment.shift.steps) }

        return forbiddenSteps.toDouble() / steps.toDouble()
    }
}
