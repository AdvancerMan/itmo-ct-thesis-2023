package ru.itmo.kazakov.autoschedule.nsp.generator.breaks

import ru.itmo.kazakov.autoschedule.helper.BreaksHelper
import ru.itmo.kazakov.autoschedule.nsp.generator.breaks.cache.WorkBetweenBreaksCalculator
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.Break
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.BreakType
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.Shift
import ru.itmo.kazakov.autoschedule.nsp.model.team.breaks.BreaksConstraintsInfo
import ru.itmo.kazakov.autoschedule.nsp.model.team.breaks.DataBreaksConstraintsInfo
import ru.itmo.kazakov.autoschedule.nsp.model.team.breaks.WithoutBreaksBreaksConstraintsInfo
import kotlin.random.Random

class ConstraintsAwareBreaksGenerator(

    private val breaksHelper: BreaksHelper,

    private val breaksConstraintsInfo: BreaksConstraintsInfo,

    private val workBetweenBreaksCalculator: WorkBetweenBreaksCalculator,

    private val random: Random,
) : BreaksGenerator {

    override fun generateSortedBreaks(shift: Shift): List<Break> {
        when (breaksConstraintsInfo) {
            is WithoutBreaksBreaksConstraintsInfo -> return emptyList()
            is DataBreaksConstraintsInfo -> {}
        }

        val commonBreaksCount = breaksConstraintsInfo.getCommonBreaksForShiftSize(shift.stepsCount)
        val lunchBreaksCount = breaksConstraintsInfo.getLunchBreaksForShiftSize(shift.stepsCount)

        if (commonBreaksCount == 0 && lunchBreaksCount == 0) {
            return emptyList()
        }

        val workBetweenBreaks = generateWorkBetweenBreaks(commonBreaksCount, lunchBreaksCount, shift)
        return internalGenerateSortedBreaks(lunchBreaksCount, commonBreaksCount, workBetweenBreaks)
    }

    private fun generateWorkBetweenBreaks(commonBreaksCount: Int, lunchBreaksCount: Int, shift: Shift): IntArray {
        require(breaksConstraintsInfo is DataBreaksConstraintsInfo) {
            "Expected breaksConstraintsInfo to be DataBreaksConstraintsInfo"
        }

        val workBetweenBreaksSize = commonBreaksCount + lunchBreaksCount + 1
        val possibleWorkIncreasings = workBetweenBreaksSize
            .times(breaksConstraintsInfo.maxWorkStepsNoBreaks - breaksConstraintsInfo.minWorkStepsNoBreaks)

        val remainingWorkSteps = shift.stepsCount
            .minus(commonBreaksCount * breaksConstraintsInfo.commonBreakSteps)
            .minus(lunchBreaksCount * breaksConstraintsInfo.lunchBreakSteps)
            .minus(breaksConstraintsInfo.minWorkStepsNoBreaks * workBetweenBreaksSize)
            .coerceIn(0..possibleWorkIncreasings)

        val workBetweenBreaks = workBetweenBreaksCalculator
            .getRandomWorkBetweenBreaks(workBetweenBreaksSize, remainingWorkSteps)
        workBetweenBreaks.indices.forEach { workBetweenBreaks[it] += breaksConstraintsInfo.minWorkStepsNoBreaks }
        return workBetweenBreaks
    }

    private fun internalGenerateSortedBreaks(
        lunchBreaksCount: Int,
        commonBreaksCount: Int,
        workBetweenBreaks: IntArray
    ): List<Break> {
        require(breaksConstraintsInfo is DataBreaksConstraintsInfo) {
            "Expected breaksConstraintsInfo to be DataBreaksConstraintsInfo"
        }
        require(commonBreaksCount > 0) {
            "Expecting at least 1 common break to be planned"
        }
        require(lunchBreaksCount <= 1) {
            "More than 1 lunch break is currently not supported"
        }

        val lunchIndex = if (lunchBreaksCount == 0) {
            -1
        } else {
            random.nextInt(1, lunchBreaksCount + commonBreaksCount)
        }

        val sortedBreaks: MutableList<Break> = ArrayList(lunchBreaksCount + commonBreaksCount)
        sortedBreaks.add(Break(workBetweenBreaks[0], BreakType.COMMON))
        for (i in 1 until lunchBreaksCount + commonBreaksCount) {
            val lastBreakType = sortedBreaks.last().breakType
            val previousBreakSize = breaksHelper.getBreakLength(breaksConstraintsInfo, lastBreakType)
            val breakStartInShift = sortedBreaks.last().breakStartInShift + previousBreakSize + workBetweenBreaks[i]

            val type = if (i == lunchIndex) BreakType.LUNCH else BreakType.COMMON
            sortedBreaks.add(Break(breakStartInShift, type))
        }
        return sortedBreaks
    }
}
