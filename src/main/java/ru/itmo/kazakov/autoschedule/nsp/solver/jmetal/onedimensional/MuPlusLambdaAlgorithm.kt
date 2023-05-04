package ru.itmo.kazakov.autoschedule.nsp.solver.jmetal.onedimensional

import org.uma.jmetal.algorithm.singleobjective.evolutionstrategy.ElitistEvolutionStrategy
import org.uma.jmetal.util.pseudorandom.JMetalRandom
import ru.itmo.kazakov.autoschedule.algorithm.Algorithm
import ru.itmo.kazakov.autoschedule.algorithm.integration.JmetalMutationOperator
import ru.itmo.kazakov.autoschedule.algorithm.integration.StatisticsCollector
import ru.itmo.kazakov.autoschedule.algorithm.integration.StopConditionState
import ru.itmo.kazakov.autoschedule.algorithm.operator.MutationOperator
import ru.itmo.kazakov.autoschedule.algorithm.operator.NextPopulationSelectionBatcher
import ru.itmo.kazakov.autoschedule.nsp.aggregator.MultiplicationNspFitnessAggregator
import ru.itmo.kazakov.autoschedule.nsp.generator.individual.IndividualGenerator
import ru.itmo.kazakov.autoschedule.nsp.model.ScheduleIndividual

class MuPlusLambdaAlgorithm<ID>(

    mutationOperator: MutationOperator<ScheduleIndividual<ID>>,

    private val nextPopulationSelectionBatcher: NextPopulationSelectionBatcher<ScheduleIndividual<ID>>,

    individualGenerator: IndividualGenerator<ScheduleIndividual<ID>>,

    fitnessDimension: Int,

    problemName: String,

    private val populationSize: Int,

    private val stopConditionState: StopConditionState,

    private val statisticsCollector: StatisticsCollector<ScheduleIndividual<ID>>,
) : Algorithm<ScheduleIndividual<ID>>, ElitistEvolutionStrategy<JmetalOnedimensionalScheduleIndividual<ID>>(
    JmetalOnedimensionalScheduleProblem(individualGenerator, problemName, MultiplicationNspFitnessAggregator()),
    populationSize,
    populationSize,
    Int.MAX_VALUE,
    JmetalMutationOperator(mutationOperator),
) {

    @Suppress("UNCHECKED_CAST")
    private val comparator = ElitistEvolutionStrategy::class.java.getDeclaredField("comparator")
        .also { it.isAccessible = true }
        .let { it.get(this) as Comparator<JmetalOnedimensionalScheduleIndividual<ID>> }

    init {
        problem = JmetalOnedimensionalScheduleProblem(individualGenerator, problemName, MultiplicationNspFitnessAggregator())
    }

    override fun solve(): List<ScheduleIndividual<ID>> {
        this.run()
        return population.map { it.innerIndividual }
    }

    override fun isStoppingConditionReached(): Boolean {
        super.isStoppingConditionReached()
        return stopConditionState.isStoppingConditionReached()
    }

    override fun initProgress() {
        super.initProgress()
        stopConditionState.initProgress()
        statisticsCollector.initProgress()
    }

    override fun updateProgress() {
        super.updateProgress()
        stopConditionState.updateProgress()
        statisticsCollector.updateProgress(population)
    }

    override fun replacement(
        population: MutableList<JmetalOnedimensionalScheduleIndividual<ID>>,
        offspringPopulation: MutableList<JmetalOnedimensionalScheduleIndividual<ID>>,
    ): MutableList<JmetalOnedimensionalScheduleIndividual<ID>> {
        return nextPopulationSelectionBatcher.selectPopulationBatches(population, offspringPopulation, populationSize)
            .flatMap { populationBatch ->
                if (populationBatch.population.size + populationBatch.offspringPopulation.size <= populationBatch.selectionBatchSize) {
                    return@flatMap populationBatch.population + populationBatch.offspringPopulation
                }

                populationBatch.population
                    .apply { addAll(populationBatch.offspringPopulation) }
                    .sortedWith(comparator)
                    .take(populationBatch.selectionBatchSize)
            }
            .toMutableList()
            .let { selectedPopulation ->
                val jointPopulation = population + offspringPopulation

                val sampledPopulation = MutableList(populationSize - selectedPopulation.size) {
                    val randomIndex = JMetalRandom.getInstance().randomGenerator
                        .nextInt(0, jointPopulation.size - 1)
                    jointPopulation[randomIndex]
                }

                selectedPopulation.addAll(sampledPopulation)
                selectedPopulation
            }
    }
}
