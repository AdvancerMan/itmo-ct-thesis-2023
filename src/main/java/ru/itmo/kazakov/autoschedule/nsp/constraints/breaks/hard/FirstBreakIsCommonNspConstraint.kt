package ru.itmo.kazakov.autoschedule.nsp.constraints.breaks.hard

import ru.itmo.kazakov.autoschedule.nsp.constraints.breaks.ShiftLocalBreaksNspConstraint
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.BreakType
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.ShiftAssignment

class FirstBreakIsCommonNspConstraint : ShiftLocalBreaksNspConstraint() {

    override fun checkConstraint(shiftAssignment: ShiftAssignment<*>): Boolean {
        val breakType = shiftAssignment.sortedBreaks.firstOrNull()?.breakType
        return breakType == null || breakType == BreakType.COMMON
    }

    override fun calculatePenalty(shiftAssignment: ShiftAssignment<*>): Double {
        return if (checkConstraint(shiftAssignment)) {
            0.0
        } else {
            1.0
        }
    }
}
