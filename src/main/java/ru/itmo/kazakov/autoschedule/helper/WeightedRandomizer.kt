package ru.itmo.kazakov.autoschedule.helper

interface WeightedRandomizer<T> {

    fun nextEntity(): T
}
