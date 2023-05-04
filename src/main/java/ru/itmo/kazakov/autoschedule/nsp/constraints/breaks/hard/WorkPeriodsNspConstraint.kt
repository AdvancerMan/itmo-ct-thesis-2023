package ru.itmo.kazakov.autoschedule.nsp.constraints.breaks.hard

import ru.itmo.kazakov.autoschedule.helper.BreaksHelper
import ru.itmo.kazakov.autoschedule.helper.PenaltyHelper
import ru.itmo.kazakov.autoschedule.nsp.constraints.breaks.ShiftLocalBreaksNspConstraint
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.ShiftAssignment
import ru.itmo.kazakov.autoschedule.nsp.model.team.breaks.BreaksConstraintsInfo
import ru.itmo.kazakov.autoschedule.nsp.model.team.breaks.DataBreaksConstraintsInfo

class WorkPeriodsNspConstraint(

    private val breaksConstraintsInfo: BreaksConstraintsInfo,

    private val breaksHelper: BreaksHelper,
) : ShiftLocalBreaksNspConstraint() {

    override fun checkConstraint(shiftAssignment: ShiftAssignment<*>): Boolean {
        if (breaksConstraintsInfo !is DataBreaksConstraintsInfo) {
            return true
        }

        return breaksHelper.getWorkPeriods(breaksConstraintsInfo, shiftAssignment.shift, shiftAssignment.sortedBreaks)
            .all { workPeriod ->
                workPeriod in breaksConstraintsInfo.minWorkStepsNoBreaks..breaksConstraintsInfo.maxWorkStepsNoBreaks
            }
    }

    override fun calculatePenalty(shiftAssignment: ShiftAssignment<*>): Double {
        if (breaksConstraintsInfo !is DataBreaksConstraintsInfo || shiftAssignment.sortedBreaks.isEmpty()) {
            return 0.0
        }

        return breaksHelper.getWorkPeriods(breaksConstraintsInfo, shiftAssignment.shift, shiftAssignment.sortedBreaks)
            .sumOf { workPeriod ->
                val minWorkStepsNoBreaks = breaksConstraintsInfo.minWorkStepsNoBreaks
                val maxWorkStepsNoBreaks = breaksConstraintsInfo.maxWorkStepsNoBreaks
                PenaltyHelper.calculateEntityLengthPenalty(workPeriod, minWorkStepsNoBreaks, maxWorkStepsNoBreaks)
            }
            .div(shiftAssignment.sortedBreaks.size + 1)
    }
}
