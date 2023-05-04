package ru.itmo.kazakov.autoschedule.nsp.solver.statistics.formatter

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.json.JsonMapper
import ru.itmo.kazakov.autoschedule.nsp.fitness.NspFitnessCalculator
import ru.itmo.kazakov.autoschedule.nsp.model.forecast.Forecast
import ru.itmo.kazakov.autoschedule.nsp.model.team.TeamConstraintsInfo
import ru.itmo.kazakov.autoschedule.nsp.solver.statistics.NspStatisticsCollector

class ByDaysForecastStatisticsFormatter(

    private val fitnessCalculator: NspFitnessCalculator<*>,

    private val teamConstraintsInfo: TeamConstraintsInfo,

    private val forecast: Forecast,
) : StatisticsFormatter {

    companion object {
        private val JSON_MAPPER = JsonMapper()
    }

    override fun <ID> format(solutionCollector: NspStatisticsCollector<ID>): JsonNode {
        @Suppress("UNCHECKED_CAST")
        val castedFitnessCalculator = fitnessCalculator as NspFitnessCalculator<ID>

        val bestSolution = solutionCollector.solutionSnapshots.last().bestIndividual
        val daysInPeriod = forecast.size / teamConstraintsInfo.stepsInDay

        return (0 until daysInPeriod)
            .map { day ->
                val dayForecast = forecast.stepsForecast.copyOf()
                dayForecast.indices.forEach { i ->
                    if (i / teamConstraintsInfo.stepsInDay != day) {
                        dayForecast[i] = 0.0
                    }
                }

                val forecastPenalty = castedFitnessCalculator.calculateForecastPenalty(
                    bestSolution.schedule,
                    Forecast(dayForecast, forecast.epsilon),
                )

                PlotPoint(day.toDouble(), forecastPenalty)
            }
            .let { JSON_MAPPER.valueToTree(it) }
    }
}
