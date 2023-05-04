package ru.itmo.kazakov.autoschedule.nsp.model.schedule.cache

import ru.itmo.kazakov.autoschedule.nsp.constraints.NspConstraint

sealed interface AssigneeCacheKey<T : Any> : CacheKey<T> {

    data class NspConstraintPenaltyAssigneeCacheKey(
        val constraint: NspConstraint,
    ) : AssigneeCacheKey<Double>
}
