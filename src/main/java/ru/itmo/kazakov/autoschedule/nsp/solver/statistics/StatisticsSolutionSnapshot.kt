package ru.itmo.kazakov.autoschedule.nsp.solver.statistics

import ru.itmo.kazakov.autoschedule.nsp.model.ScheduleIndividual
import java.time.Duration

data class StatisticsSolutionSnapshot<ID>(
    val bestIndividual: ScheduleIndividual<ID>,
    val durationSinceAlgorithmInit: Duration,
    val iterationsPassed: Int,
)
