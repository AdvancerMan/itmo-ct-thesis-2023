package ru.itmo.kazakov.autoschedule.helper

data class WeightedEntity<out T>(
    val entity: T,
    val weight: Double,
)

internal infix fun <T> T.weighted(weight: Double): WeightedEntity<T> {
    return WeightedEntity(this, weight)
}
