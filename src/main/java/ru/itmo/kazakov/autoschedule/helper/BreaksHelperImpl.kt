package ru.itmo.kazakov.autoschedule.helper

import ru.itmo.kazakov.autoschedule.nsp.model.schedule.Break
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.BreakType
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.Shift
import ru.itmo.kazakov.autoschedule.nsp.model.team.breaks.DataBreaksConstraintsInfo

class BreaksHelperImpl : BreaksHelper {

    override fun getBreakLength(breaksConstraintsInfo: DataBreaksConstraintsInfo, breakType: BreakType): Int {
        return when (breakType) {
            BreakType.COMMON -> breaksConstraintsInfo.commonBreakSteps
            BreakType.LUNCH -> breaksConstraintsInfo.lunchBreakSteps
        }
    }

    override fun getBreakEnd(breaksConstraintsInfo: DataBreaksConstraintsInfo, `break`: Break): Int {
        return `break`.breakStartInShift + getBreakLength(breaksConstraintsInfo, `break`.breakType)
    }

    override fun getBreaksCount(
        breaksConstraintsInfo: DataBreaksConstraintsInfo,
        breakType: BreakType,
        shiftSize: Int,
    ): Int {
        return when (breakType) {
            BreakType.COMMON -> breaksConstraintsInfo.getCommonBreaksForShiftSize(shiftSize)
            BreakType.LUNCH -> breaksConstraintsInfo.getLunchBreaksForShiftSize(shiftSize)
        }
    }

    override fun getWorkPeriods(
        breaksConstraintsInfo: DataBreaksConstraintsInfo,
        shift: Shift,
        sortedBreaks: List<Break>,
    ): Sequence<Int> {
        return sequenceOf(Break(-breaksConstraintsInfo.commonBreakSteps, BreakType.COMMON))
            .plus(sortedBreaks)
            .plus(Break(shift.stepsCount, BreakType.COMMON))
            .zipWithNext()
            .map { (previousBreak, nextBreak) ->
                nextBreak.breakStartInShift - getBreakEnd(breaksConstraintsInfo, previousBreak)
            }
    }
}
