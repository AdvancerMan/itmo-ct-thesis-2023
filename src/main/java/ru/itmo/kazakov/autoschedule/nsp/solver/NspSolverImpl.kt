package ru.itmo.kazakov.autoschedule.nsp.solver

import org.slf4j.LoggerFactory
import ru.itmo.kazakov.autoschedule.algorithm.integration.JmetalMultidimensionalIndividual
import ru.itmo.kazakov.autoschedule.nsp.factory.NspAlgorithmFactory
import ru.itmo.kazakov.autoschedule.nsp.model.ScheduleIndividual
import ru.itmo.kazakov.autoschedule.nsp.model.forecast.Forecast
import ru.itmo.kazakov.autoschedule.nsp.model.team.Team
import ru.itmo.kazakov.autoschedule.nsp.solver.statistics.StatisticsCollectorImpl
import ru.itmo.kazakov.autoschedule.nsp.solver.statistics.StatisticsWriter
import java.time.Clock
import java.time.Duration

class NspSolverImpl(

    private val nspAlgorithmFactory: NspAlgorithmFactory,

    private val clock: Clock,

    private val statisticsWriter: StatisticsWriter,
) : NspSolver {

    companion object {
        private val LOG = LoggerFactory.getLogger(NspSolverImpl::class.java)
    }

    override fun <ID> solve(
        forecast: Forecast,
        team: Team<ID>,
        solverSettings: SolverSettings,
    ): ScheduleIndividual<ID> {
        val stopConditionState = StopConditionStateImpl(solverSettings, clock)
        val bestIndividualSelectionOperator = nspAlgorithmFactory.buildBestIndividualSelectionOperator(forecast, team)

        val solutions = mutableListOf<ScheduleIndividual<ID>>()
        repeat(solverSettings.algorithmResolveIterations) {
            val statisticsCollector = StatisticsCollectorImpl(
                solverSettings.logOnEveryIthSecond,
                clock,
                bestIndividualSelectionOperator,
            )

            val algorithm = nspAlgorithmFactory
                .buildAlgorithm(forecast, team, stopConditionState, statisticsCollector)

            solutions.addAll(algorithm.solve())
            statisticsWriter.write(statisticsCollector)

            LOG.info(
                "Calculated a schedule in {}, {} iterations, using settings {}",
                Duration.between(statisticsCollector.startInstant, clock.instant()),
                statisticsCollector.iterationsPassed,
                solverSettings,
            )
        }

        return bestIndividualSelectionOperator
            .select(solutions.map { JmetalMultidimensionalIndividual(it) })
            .innerIndividual
    }
}
