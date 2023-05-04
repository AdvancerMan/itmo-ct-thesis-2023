package ru.itmo.kazakov.autoschedule.nsp.solver.statistics.formatter

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.json.JsonMapper
import ru.itmo.kazakov.autoschedule.nsp.solver.statistics.NspStatisticsCollector

class IterationsAndDurationStatisticsFormatter : StatisticsFormatter {

    companion object {
        private val JSON_MAPPER = JsonMapper()
    }

    override fun <ID> format(solutionCollector: NspStatisticsCollector<ID>): JsonNode {
        return solutionCollector
            .solutionSnapshots
            .maxBy { it.iterationsPassed }
            .let {
                DurationData(
                    it.iterationsPassed,
                    it.durationSinceAlgorithmInit.seconds,
                    it.durationSinceAlgorithmInit.nano,
                )
            }
            .let { JSON_MAPPER.valueToTree(it) }
    }

    private data class DurationData(
        val iterations: Int,
        val durationSecondsPart: Long,
        val durationNanosPart: Int,
    )
}
