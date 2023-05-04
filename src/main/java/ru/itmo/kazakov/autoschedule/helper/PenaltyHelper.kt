package ru.itmo.kazakov.autoschedule.helper

object PenaltyHelper {

    fun calculateEntityLengthPenalty(
        entityLength: Number,
        minEntityLength: Number,
        maxEntityLength: Number,
    ): Double {
        val typedEntityLength = entityLength.toDouble()
        val typedMinEntityLength = minEntityLength.toDouble()
        val typedMaxEntityLength = maxEntityLength.toDouble()

        return if (typedEntityLength < typedMinEntityLength) {
            val dividend = typedMinEntityLength - typedEntityLength
            val divisor = typedMinEntityLength.takeIf { it != 0.0 } ?: 1.0

            dividend / divisor
        } else if (typedEntityLength > typedMaxEntityLength) {
            val dividend = typedEntityLength - typedMaxEntityLength
            val divisor = typedMaxEntityLength.takeIf { it != 0.0 } ?: 1.0

            dividend / divisor
        } else {
            0.0
        }
    }
}
