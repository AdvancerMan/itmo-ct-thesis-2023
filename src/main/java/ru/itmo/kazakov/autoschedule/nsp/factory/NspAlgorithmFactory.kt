package ru.itmo.kazakov.autoschedule.nsp.factory

import ru.itmo.kazakov.autoschedule.algorithm.integration.StatisticsCollector
import ru.itmo.kazakov.autoschedule.algorithm.integration.StopConditionState
import ru.itmo.kazakov.autoschedule.nsp.aggregator.NspFitnessAggregator
import ru.itmo.kazakov.autoschedule.nsp.fitness.NspFitnessCalculator
import ru.itmo.kazakov.autoschedule.nsp.generator.breaks.BreaksGenerator
import ru.itmo.kazakov.autoschedule.nsp.model.ScheduleIndividual
import ru.itmo.kazakov.autoschedule.nsp.model.forecast.Forecast
import ru.itmo.kazakov.autoschedule.nsp.model.team.Team
import ru.itmo.kazakov.autoschedule.nsp.operator.BestIndividualSelectionOperator

interface NspAlgorithmFactory {

    val nspFitnessAggregator: NspFitnessAggregator

    fun <ID> buildFitnessCalculator(forecast: Forecast, team: Team<ID>): NspFitnessCalculator<ID>

    fun <ID> buildBreaksGenerator(forecast: Forecast, team: Team<ID>): BreaksGenerator

    fun <ID> buildBestIndividualSelectionOperator(
        forecast: Forecast,
        team: Team<ID>,
    ): BestIndividualSelectionOperator<ScheduleIndividual<ID>>

    fun <ID> buildAlgorithm(
        forecast: Forecast,
        team: Team<ID>,
        stopConditionState: StopConditionState,
        statisticsCollector: StatisticsCollector<ScheduleIndividual<ID>>,
    ): ru.itmo.kazakov.autoschedule.algorithm.Algorithm<ScheduleIndividual<ID>>
}
