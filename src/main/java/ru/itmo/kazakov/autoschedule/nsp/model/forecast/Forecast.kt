package ru.itmo.kazakov.autoschedule.nsp.model.forecast

data class Forecast(
    val stepsForecast: DoubleArray,
    val epsilon: Double = 1e-6,
) {

    val size: Int
        get() = stepsForecast.size

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Forecast

        if (!stepsForecast.contentEquals(other.stepsForecast)) return false
        if (epsilon != other.epsilon) return false

        return true
    }

    override fun hashCode(): Int {
        var result = stepsForecast.contentHashCode()
        result = 31 * result + epsilon.hashCode()
        return result
    }

    override fun toString(): String {
        return "Forecast(stepsForecast=${stepsForecast.contentToString()}, epsilon=$epsilon)"
    }
}
