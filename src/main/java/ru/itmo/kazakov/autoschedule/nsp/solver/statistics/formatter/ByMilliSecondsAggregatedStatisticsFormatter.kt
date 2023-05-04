package ru.itmo.kazakov.autoschedule.nsp.solver.statistics.formatter

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.json.JsonMapper
import ru.itmo.kazakov.autoschedule.nsp.aggregator.NspFitnessAggregator
import ru.itmo.kazakov.autoschedule.nsp.solver.statistics.NspStatisticsCollector

class ByMilliSecondsAggregatedStatisticsFormatter(

    private val nspFitnessAggregator: NspFitnessAggregator,
) : StatisticsFormatter {

    companion object {
        private val JSON_MAPPER = JsonMapper()
    }

    override fun <ID> format(solutionCollector: NspStatisticsCollector<ID>): JsonNode {
        return solutionCollector
            .solutionSnapshots
            .map {
                val aggregatedFitness = nspFitnessAggregator.aggregateFitness(it.bestIndividual)
                val timestamp = it.durationSinceAlgorithmInit.toMillis()
                PlotPoint(timestamp.toDouble(), aggregatedFitness)
            }
            .let { JSON_MAPPER.valueToTree(it) }
    }
}
