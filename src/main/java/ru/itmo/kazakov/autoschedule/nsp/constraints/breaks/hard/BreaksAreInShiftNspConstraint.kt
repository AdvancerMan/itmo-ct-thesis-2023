package ru.itmo.kazakov.autoschedule.nsp.constraints.breaks.hard

import ru.itmo.kazakov.autoschedule.helper.BreaksHelper
import ru.itmo.kazakov.autoschedule.helper.NspHelper
import ru.itmo.kazakov.autoschedule.nsp.constraints.breaks.ShiftLocalBreaksNspConstraint
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.Break
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.ShiftAssignment
import ru.itmo.kazakov.autoschedule.nsp.model.team.breaks.BreaksConstraintsInfo
import ru.itmo.kazakov.autoschedule.nsp.model.team.breaks.DataBreaksConstraintsInfo

class BreaksAreInShiftNspConstraint(

    private val breaksConstraintsInfo: BreaksConstraintsInfo,

    private val breaksHelper: BreaksHelper,
) : ShiftLocalBreaksNspConstraint() {

    override fun checkConstraint(shiftAssignment: ShiftAssignment<*>): Boolean {
        if (breaksConstraintsInfo !is DataBreaksConstraintsInfo) {
            return true
        }

        return shiftAssignment.sortedBreaks.all { `break` ->
            val breakRange = calculateGlobalBreakRange(`break`, shiftAssignment)

            NspHelper.intersectionSize(breakRange, shiftAssignment.shift.steps) == NspHelper.rangeSize(breakRange)
        }
    }

    private fun calculateGlobalBreakRange(`break`: Break, shiftAssignment: ShiftAssignment<*>): IntRange {
        require(breaksConstraintsInfo is DataBreaksConstraintsInfo)

        val breakStart = `break`.breakStartInShift + shiftAssignment.shift.startStepIndex
        val breakEnd = breaksHelper.getBreakEnd(breaksConstraintsInfo, `break`)
            .plus(shiftAssignment.shift.startStepIndex)
        return breakStart..breakEnd
    }

    override fun calculatePenalty(shiftAssignment: ShiftAssignment<*>): Double {
        if (breaksConstraintsInfo !is DataBreaksConstraintsInfo || shiftAssignment.sortedBreaks.isEmpty()) {
            return 0.0
        }

        return shiftAssignment.sortedBreaks
            .sumOf { `break` ->
                val breakRange = calculateGlobalBreakRange(`break`, shiftAssignment)

                val breakRangeSize = NspHelper.rangeSize(breakRange)
                val inShiftBreakRangeSize = NspHelper.intersectionSize(breakRange, shiftAssignment.shift.steps)
                1 - inShiftBreakRangeSize.toDouble() / breakRangeSize
            }
            .div(shiftAssignment.sortedBreaks.size)
    }
}
