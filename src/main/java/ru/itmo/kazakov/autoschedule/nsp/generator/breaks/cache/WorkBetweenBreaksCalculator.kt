package ru.itmo.kazakov.autoschedule.nsp.generator.breaks.cache

interface WorkBetweenBreaksCalculator {

    fun getRandomWorkBetweenBreaks(resultSize: Int, remainingWorkSteps: Int): IntArray
}
