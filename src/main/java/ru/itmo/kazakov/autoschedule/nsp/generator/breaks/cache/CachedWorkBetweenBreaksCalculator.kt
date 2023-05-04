package ru.itmo.kazakov.autoschedule.nsp.generator.breaks.cache

import ru.itmo.kazakov.autoschedule.nsp.model.team.TeamConstraintsInfo
import ru.itmo.kazakov.autoschedule.nsp.model.team.breaks.BreaksConstraintsInfo
import ru.itmo.kazakov.autoschedule.nsp.model.team.breaks.DataBreaksConstraintsInfo
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class CachedWorkBetweenBreaksCalculator(

    breaksConstraintsInfo: BreaksConstraintsInfo,

    teamConstraintsInfo: TeamConstraintsInfo,

    private val random: Random,
) : WorkBetweenBreaksCalculator {

    companion object {
        private const val MAX_WORK_WITHOUT_BREAKS_LIMIT = 8
        private const val WORK_BETWEEN_BREAKS_MAX_CACHED_SIZE_LIMIT = 6
    }

    private val maxWorkWithoutBreaks by lazy {
        require(breaksConstraintsInfo is DataBreaksConstraintsInfo) { "Could not get data without breaks constraints" }
        breaksConstraintsInfo.maxWorkStepsNoBreaks - breaksConstraintsInfo.minWorkStepsNoBreaks
    }

    private val cache = ConcurrentHashMap<Pair<Int, Int>, List<IntArray>>()
    private val cacheLock = ReentrantLock()

    init {
        if (breaksConstraintsInfo is DataBreaksConstraintsInfo) {
            require(maxWorkWithoutBreaks <= MAX_WORK_WITHOUT_BREAKS_LIMIT) {
                "Too much work without breaks; preventing out-of-memory errors..."
            }

            val workBetweenBreaksMaxCachedSize = breaksConstraintsInfo
                .getCommonBreaksForShiftSize(teamConstraintsInfo.stepsInDay)
                .plus(breaksConstraintsInfo.getLunchBreaksForShiftSize(teamConstraintsInfo.stepsInDay))

            require(workBetweenBreaksMaxCachedSize <= WORK_BETWEEN_BREAKS_MAX_CACHED_SIZE_LIMIT) {
                "Too many breaks; preventing out-of-memory errors..."
            }
        }
    }

    override fun getRandomWorkBetweenBreaks(resultSize: Int, remainingWorkSteps: Int): IntArray {
        return getWorkBetweenBreaks(resultSize, remainingWorkSteps)
            .random(random)
            .copyOf()
    }

    private fun getWorkBetweenBreaks(workBetweenBreaksSize: Int, remainingWorkSteps: Int): List<IntArray> {
        require(workBetweenBreaksSize <= WORK_BETWEEN_BREAKS_MAX_CACHED_SIZE_LIMIT)
        return cache[workBetweenBreaksSize to remainingWorkSteps] ?: cacheLock.withLock {
            val result = calculateWorkBetweenBreaks(workBetweenBreaksSize, remainingWorkSteps)
            cache[workBetweenBreaksSize to remainingWorkSteps] = result
            result
        }
    }

    private fun calculateWorkBetweenBreaks(workBetweenBreaksSize: Int, remainingWorkSteps: Int): List<IntArray> {
        require(remainingWorkSteps in 0..workBetweenBreaksSize * maxWorkWithoutBreaks)
        if (workBetweenBreaksSize == 1) {
            require(remainingWorkSteps <= maxWorkWithoutBreaks)
            return listOf(IntArray(1) { remainingWorkSteps })
        }

        val result = mutableListOf<IntArray>()
        val minCurrentWorkWithoutBreaks = max(0, remainingWorkSteps - (workBetweenBreaksSize - 1) * maxWorkWithoutBreaks)
        val maxCurrentWorkWithoutBreaks = min(maxWorkWithoutBreaks, remainingWorkSteps)
        for (currentWorkWithoutBreaks in minCurrentWorkWithoutBreaks..maxCurrentWorkWithoutBreaks) {
            getWorkBetweenBreaks(workBetweenBreaksSize - 1, remainingWorkSteps - currentWorkWithoutBreaks)
                .map { intArrayOf(currentWorkWithoutBreaks, *it) }
                .let { result.addAll(it) }
        }
        return result
    }
}
