package ru.itmo.kazakov.autoschedule.nsp.model.schedule.cache

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

abstract class CachedEntity {
    private val cache: ConcurrentMap<CacheKey<*>, Any> = ConcurrentHashMap()

    fun <R, K : CacheKey<R>> computeIfAbsentCacheValue(cacheKey: K, compute: () -> R): R {
        @Suppress("UNCHECKED_CAST")
        return cache.computeIfAbsent(cacheKey) { compute() } as R
    }
}
