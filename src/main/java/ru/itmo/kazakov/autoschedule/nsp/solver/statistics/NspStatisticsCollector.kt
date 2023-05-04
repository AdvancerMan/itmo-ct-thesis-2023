package ru.itmo.kazakov.autoschedule.nsp.solver.statistics

import ru.itmo.kazakov.autoschedule.algorithm.integration.StatisticsCollector
import ru.itmo.kazakov.autoschedule.nsp.model.ScheduleIndividual

interface NspStatisticsCollector<ID> : StatisticsCollector<ScheduleIndividual<ID>> {

    val solutionSnapshots: List<StatisticsSolutionSnapshot<ID>>
}
