package ru.itmo.kazakov.autoschedule

import org.uma.jmetal.util.pseudorandom.JMetalRandom
import ru.itmo.kazakov.autoschedule.configuration.PropertiesAutoscheduleWeightsContainer
import ru.itmo.kazakov.autoschedule.nsp.aggregator.MultiplicationNspFitnessAggregator
import ru.itmo.kazakov.autoschedule.nsp.factory.DefaultNspAlgorithmFactory
import ru.itmo.kazakov.autoschedule.nsp.model.forecast.Forecast
import ru.itmo.kazakov.autoschedule.nsp.model.team.DataShiftAssignee
import ru.itmo.kazakov.autoschedule.nsp.model.team.DataTeam
import ru.itmo.kazakov.autoschedule.nsp.model.team.TeamConstraintsInfo
import ru.itmo.kazakov.autoschedule.nsp.model.team.WorkPlan
import ru.itmo.kazakov.autoschedule.nsp.model.team.breaks.DataBreaksConstraintsInfo
import ru.itmo.kazakov.autoschedule.nsp.solver.NspSolverImpl
import ru.itmo.kazakov.autoschedule.nsp.solver.SolverSettings
import ru.itmo.kazakov.autoschedule.nsp.solver.statistics.FileStatisticsWriter
import ru.itmo.kazakov.autoschedule.nsp.solver.statistics.formatter.ByDaysForecastStatisticsFormatter
import ru.itmo.kazakov.autoschedule.nsp.solver.statistics.formatter.ByMilliSecondsAggregatedStatisticsFormatter
import java.nio.file.Path
import java.time.Clock
import java.time.Duration
import kotlin.random.Random

fun main() {
    val forecast = listOf(
        2.0, 1.0, 0.1, 0.1, 0.1, 0.1,
        1.0, 1.0, 2.0, 2.0, 3.0, 3.0,
        4.0, 4.0, 4.0, 4.0, 4.0, 4.0,
        4.0, 4.0, 4.0, 3.0, 3.0, 2.0,
    )
        .flatMap { hourForecast -> List(4) { hourForecast } }
        .let { dayForecast -> List(7) { dayForecast } }
        .flatten()
        .toDoubleArray()
        .let { Forecast(it) }

    val teamConstraintsInfo = TeamConstraintsInfo(
        minRestStepsBetweenShifts = 48,
        stepsInDay = 96,
        forecastSteps = forecast.size,
    )

    val team = DataTeam(
        listOf(
            DataShiftAssignee(
                1.0,
                160,
                32,
                32,
                1,
                emptyList(),
                WorkPlan(5, 2),
                (0 until teamConstraintsInfo.stepsInDay).toSet(),
                emptyList(),
                0,
                teamConstraintsInfo,
            ),
        ),
        teamConstraintsInfo,
        DataBreaksConstraintsInfo(
            4,
            12,
            1,
            2,
            commonBreaksForShiftSize = intArrayOf(
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                1, 1, 1, 1,
                1, 1, 1, 1,
                1, 1, 1, 1,
                2, 2, 2, 2,
                2, 2, 2, 2,
                2, 2, 2, 2,
                3,
            ),
            lunchBreaksForShiftSize = intArrayOf(
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                0, 0, 0, 0,
                1,
            ),
        ),
    )

    val nspFitnessAggregator = MultiplicationNspFitnessAggregator()

    JMetalRandom.getInstance().seed = 42
    val factory = DefaultNspAlgorithmFactory(
        100,
        1e-9,
        nspFitnessAggregator,
        PropertiesAutoscheduleWeightsContainer("properties/nsp-weights.properties"),
        Random(42),
    )

    val fitnessCalculator = factory.buildFitnessCalculator(forecast, team)

    val solver = NspSolverImpl(
        factory,
        Clock.systemUTC(),
        FileStatisticsWriter(
            Path.of("results"),
            listOf(
                ByDaysForecastStatisticsFormatter(fitnessCalculator, team.teamConstraintsInfo, forecast),
                ByMilliSecondsAggregatedStatisticsFormatter(nspFitnessAggregator),
            ),
            Clock.systemUTC(),
        ),
    )

    val solverSettings = SolverSettings(
        maxIterations = 250_000,
        approximateMaxTimeSpent = Duration.ofSeconds(10),
        logOnEveryIthSecond = 1,
    )

    val schedule = solver.solve(forecast, team, solverSettings)

    println("Schedule is ${schedule.schedule}")
    println("Penalty is ${schedule.fitness.contentToString()}")
}
