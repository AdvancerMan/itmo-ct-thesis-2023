package ru.itmo.kazakov.autoschedule.nsp.solver.statistics

import org.slf4j.LoggerFactory
import ru.itmo.kazakov.autoschedule.algorithm.integration.JmetalIndividual
import ru.itmo.kazakov.autoschedule.nsp.model.ScheduleIndividual
import ru.itmo.kazakov.autoschedule.nsp.operator.BestIndividualSelectionOperator
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

class StatisticsCollectorImpl<ID>(

    private val logOnEveryIthSecond: Int,

    private val clock: Clock,

    private val bestIndividualSelectionOperator: BestIndividualSelectionOperator<ScheduleIndividual<ID>>,
) : NspStatisticsCollector<ID> {

    companion object {
        private val LOG = LoggerFactory.getLogger(StatisticsCollectorImpl::class.java)
    }

    override val solutionSnapshots: MutableList<StatisticsSolutionSnapshot<ID>> =
        Collections.synchronizedList(mutableListOf<StatisticsSolutionSnapshot<ID>>())

    val iterationsPassed = AtomicInteger(0)

    @Volatile
    var startInstant: Instant = Instant.EPOCH
        private set

    @Volatile
    private var secondsLogged = AtomicInteger(0)

    override fun initProgress() {
        iterationsPassed.set(0)
        secondsLogged.set(0)
        startInstant = clock.instant()
        solutionSnapshots.clear()
    }

    override fun updateProgress(population: List<JmetalIndividual<ScheduleIndividual<ID>>>) {
        iterationsPassed.getAndIncrement()

        val durationSinceAlgorithmInit = Duration.between(startInstant, clock.instant())
        solutionSnapshots.add(
            StatisticsSolutionSnapshot(
                bestIndividualSelectionOperator.select(population).innerIndividual,
                durationSinceAlgorithmInit,
                iterationsPassed.get(),
            ),
        )

        val newSecondsLogged = durationSinceAlgorithmInit.seconds.toInt()
        if (iterationsPassed.get() == 1 || newSecondsLogged / logOnEveryIthSecond != secondsLogged.get() / logOnEveryIthSecond) {
            val lastSnapshot = solutionSnapshots.last()

            LOG.debug(
                "In {} iterations and {} time algorithm got individual with penalty {}",
                lastSnapshot.iterationsPassed,
                lastSnapshot.durationSinceAlgorithmInit,
                lastSnapshot.bestIndividual.fitness.contentToString(),
            )
        }
        secondsLogged.set(newSecondsLogged)
    }
}
