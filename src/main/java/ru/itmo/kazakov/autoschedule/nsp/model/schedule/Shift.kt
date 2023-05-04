package ru.itmo.kazakov.autoschedule.nsp.model.schedule

data class Shift(
    // inclusive
    val startStepIndex: Int,
    // exclusive
    val endStepIndex: Int,
) {

    init {
        assert(startStepIndex < endStepIndex) {
            "Invalid shift assignment created. Expected startStepIndex < endStepIndex, " +
                "got $startStepIndex >= $endStepIndex"
        }
    }

    val stepsCount: Int
        get() = endStepIndex - startStepIndex

    val steps: IntRange
        get() = startStepIndex until endStepIndex

    companion object {
        fun ofStepsCount(
            firstStepIndex: Int,
            stepsCount: Int,
        ): Shift {
            return Shift(firstStepIndex, firstStepIndex + stepsCount)
        }
    }
}
