package ru.itmo.kazakov.autoschedule.nsp.factory

interface AutoscheduleWeightsContainer {

    fun getOrDefault(name: AutoscheduleWeightName, defaultValue: Double): Double
}
