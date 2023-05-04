package ru.itmo.kazakov.autoschedule.nsp.solver

import ru.itmo.kazakov.autoschedule.algorithm.integration.StopConditionState
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger

class StopConditionStateImpl(

    private val solverSettings: SolverSettings,

    private val clock: Clock,
) : StopConditionState {

    private val iterationsPassed = AtomicInteger(0)

    @Volatile
    private var startInstant: Instant = Instant.EPOCH

    override fun isStoppingConditionReached(): Boolean {
        val isStoppedByTime = Duration.between(startInstant, clock.instant()) >= solverSettings.approximateMaxTimeSpent
        val isStoppedByIterations = iterationsPassed.get() >= solverSettings.maxIterations
        return isStoppedByIterations || isStoppedByTime
    }

    override fun initProgress() {
        iterationsPassed.set(0)
        startInstant = clock.instant()
    }

    override fun updateProgress() {
        iterationsPassed.getAndIncrement()
    }
}
