package ru.itmo.kazakov.autoschedule.nsp.solver.statistics.formatter

import com.fasterxml.jackson.databind.JsonNode
import ru.itmo.kazakov.autoschedule.nsp.solver.statistics.NspStatisticsCollector

interface StatisticsFormatter {

    fun <ID> format(solutionCollector: NspStatisticsCollector<ID>): JsonNode
}
