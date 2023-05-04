package ru.itmo.kazakov.autoschedule.helper

import ru.itmo.kazakov.autoschedule.nsp.model.schedule.Break
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.BreakType
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.Shift
import ru.itmo.kazakov.autoschedule.nsp.model.team.breaks.DataBreaksConstraintsInfo

interface BreaksHelper {

    fun getBreakLength(breaksConstraintsInfo: DataBreaksConstraintsInfo, breakType: BreakType): Int

    fun getBreakEnd(breaksConstraintsInfo: DataBreaksConstraintsInfo, `break`: Break): Int

    fun getBreaksCount(breaksConstraintsInfo: DataBreaksConstraintsInfo, breakType: BreakType, shiftSize: Int): Int

    fun getWorkPeriods(
        breaksConstraintsInfo: DataBreaksConstraintsInfo,
        shift: Shift,
        sortedBreaks: List<Break>,
    ): Sequence<Int>
}
