package ru.itmo.kazakov.autoschedule.nsp.constraints.shift.hard

import ru.itmo.kazakov.autoschedule.nsp.constraints.NspConstraint
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.AssigneeSchedule
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.ShiftAssignment
import ru.itmo.kazakov.autoschedule.nsp.model.team.TeamConstraintsInfo
import kotlin.math.max

class RestBetweenShiftsNspConstraint(

    private val teamConstraintsInfo: TeamConstraintsInfo,
) : NspConstraint {

    override fun canAddAssignment(
        newAssignment: ShiftAssignment<*>,
        existingAssignments: List<ShiftAssignment<*>>,
    ): Boolean {
        return existingAssignments
            .all { existingAssignment ->
                val restBetweenShifts = max(
                    existingAssignment.shift.startStepIndex - newAssignment.shift.endStepIndex,
                    newAssignment.shift.startStepIndex - existingAssignment.shift.endStepIndex,
                )

                restBetweenShifts >= teamConstraintsInfo.minRestStepsBetweenShifts
            }
    }

    override fun calculatePenalty(assigneeSchedule: AssigneeSchedule<*>): Double {
        if (assigneeSchedule.sortedAssignments.isEmpty()) {
            return 0.0
        }

        return assigneeSchedule.sortedAssignments
            .sortedBy { it.shift.startStepIndex }
            .zipWithNext()
            .sumOf { (previousAssignment, currentAssignment) ->
                val needRestSteps = currentAssignment.shift.startStepIndex
                    .minus(previousAssignment.shift.endStepIndex)
                    .minus(teamConstraintsInfo.minRestStepsBetweenShifts)
                    .takeIf { it < 0 }
                    ?.unaryMinus()
                    ?: 0

                needRestSteps.toDouble() / teamConstraintsInfo.minRestStepsBetweenShifts.toDouble()
            }
            .div(assigneeSchedule.sortedAssignments.size)
    }
}
