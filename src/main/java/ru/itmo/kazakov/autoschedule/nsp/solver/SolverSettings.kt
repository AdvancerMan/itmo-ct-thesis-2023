package ru.itmo.kazakov.autoschedule.nsp.solver

import java.time.Duration

data class SolverSettings(
    val maxIterations: Int,
    val approximateMaxTimeSpent: Duration,
    val logOnEveryIthSecond: Int,
    val algorithmResolveIterations: Int = 1,
)
