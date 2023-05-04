package ru.itmo.kazakov.autoschedule.nsp.constraints.breaks.hard

import ru.itmo.kazakov.autoschedule.helper.BreaksHelper
import ru.itmo.kazakov.autoschedule.helper.PenaltyHelper
import ru.itmo.kazakov.autoschedule.nsp.constraints.breaks.ShiftLocalBreaksNspConstraint
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.BreakType
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.ShiftAssignment
import ru.itmo.kazakov.autoschedule.nsp.model.team.breaks.BreaksConstraintsInfo
import ru.itmo.kazakov.autoschedule.nsp.model.team.breaks.DataBreaksConstraintsInfo

class BreaksCountNspConstraint(

    private val breaksConstraintsInfo: BreaksConstraintsInfo,

    private val breaksHelper: BreaksHelper,
) : ShiftLocalBreaksNspConstraint() {

    override fun checkConstraint(shiftAssignment: ShiftAssignment<*>): Boolean {
        if (breaksConstraintsInfo !is DataBreaksConstraintsInfo) {
            return true
        }

        val breaksCountMap = shiftAssignment.sortedBreaks
            .groupingBy { it.breakType }
            .eachCount()

        return BreakType.values()
            .all { breakType ->
                val breaksCount = breaksCountMap[breakType] ?: 0
                val shiftSize = shiftAssignment.shift.stepsCount

                breaksHelper.getBreaksCount(breaksConstraintsInfo, breakType, shiftSize) == breaksCount
            }
    }

    override fun calculatePenalty(shiftAssignment: ShiftAssignment<*>): Double {
        if (breaksConstraintsInfo !is DataBreaksConstraintsInfo) {
            return 0.0
        }

        val breaksCountMap = shiftAssignment.sortedBreaks
            .groupingBy { it.breakType }
            .eachCount()

        return BreakType.values()
            .sumOf { breakType ->
                val breaksCount = breaksCountMap[breakType] ?: 0
                val shiftSize = shiftAssignment.shift.stepsCount
                val expectedBreaksCount = breaksHelper.getBreaksCount(breaksConstraintsInfo, breakType, shiftSize)

                PenaltyHelper.calculateEntityLengthPenalty(breaksCount, expectedBreaksCount, expectedBreaksCount)
            }
            .div(BreakType.values().size)
    }
}
