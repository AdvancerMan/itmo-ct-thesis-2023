package ru.itmo.kazakov.autoschedule.nsp.solver.statistics

interface StatisticsWriter {

    fun <ID> write(solutionCollector: NspStatisticsCollector<ID>)
}
