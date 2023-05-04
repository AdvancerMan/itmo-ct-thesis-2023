package ru.itmo.kazakov.autoschedule.nsp.model.schedule.cache

sealed interface ScheduleCacheKey<T : Any> : CacheKey<T> {

    object PlannedWorkByForecastStep : ScheduleCacheKey<DoubleArray>
}
