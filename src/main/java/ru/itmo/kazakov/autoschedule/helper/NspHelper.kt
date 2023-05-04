package ru.itmo.kazakov.autoschedule.helper

import kotlin.math.max
import kotlin.math.min

object NspHelper {
    fun modulo(x: Int, mod: Int): Int {
        return (x % mod + mod) % mod
    }

    fun modulo(x: Long, mod: Long): Long {
        return (x % mod + mod) % mod
    }

    fun rangeSize(range: IntRange): Int {
        require(range.step == 1)
        return range.last - range.first + 1
    }

    fun intersectRanges(range1: IntRange, range2: IntRange): IntRange {
        require(range1.step == 1)
        require(range2.step == 1)

        val from = max(range1.first, range2.first)
        val to = min(range1.last, range2.last)

        if (from > to) {
            return IntRange.EMPTY
        }

        return from..to
    }

    fun intersectionSize(range1: IntRange, range2: IntRange): Int {
        require(range1.step == 1)
        require(range2.step == 1)

        val from = max(range1.first, range2.first)
        val to = min(range1.last, range2.last)

        return max(0, to - from + 1)
    }

    fun <E> sizedSubList(list: MutableList<E>, from: Int, size: Int): MutableList<E> {
        val (javaFrom, javaTo) = if (size < 0) {
            from + size + 1 to from + 1
        } else {
            from to from + size
        }

        return list.subList(
            javaFrom.coerceIn(0..list.size),
            javaTo.coerceIn(0..list.size)
        )
    }

    // dispersion index is used as normalized variance
    fun calculateDispersionIndex(numbers: DoubleArray, size: Int = numbers.size): Double {
        if (size <= 1) {
            return 0.0
        }

        var average = 0.0
        repeat(size) {
            average += numbers[it]
        }

        average /= size

        var variance = 0.0
        repeat(size) {
            variance += (numbers[it] - average) * (numbers[it] - average)
        }

        variance /= size - 1

        if ((variance / average).isFinite()) {
            variance /= average
        } else if (variance != 0.0) {
            throw IllegalArgumentException(
                "Could not calculate dispersion index: " +
                    "variance / average = $variance / $average = ${variance / average}"
            )
        }

        return variance
    }
}
