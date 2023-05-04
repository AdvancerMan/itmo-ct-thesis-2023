package ru.itmo.kazakov.autoschedule.algorithm

import org.uma.jmetal.algorithm.multiobjective.pesa2.PESA2
import org.uma.jmetal.operator.crossover.impl.NullCrossover
import org.uma.jmetal.util.evaluator.impl.NullEvaluator
import ru.itmo.kazakov.autoschedule.algorithm.individual.Individual
import ru.itmo.kazakov.autoschedule.algorithm.integration.JmetalMultidimensionalIndividual
import ru.itmo.kazakov.autoschedule.algorithm.integration.JmetalMultidimensionalProblem
import ru.itmo.kazakov.autoschedule.algorithm.integration.JmetalMutationOperator
import ru.itmo.kazakov.autoschedule.algorithm.integration.StatisticsCollector
import ru.itmo.kazakov.autoschedule.algorithm.integration.StopConditionState
import ru.itmo.kazakov.autoschedule.algorithm.operator.MutationOperator
import ru.itmo.kazakov.autoschedule.algorithm.operator.NextPopulationSelectionBatcher
import ru.itmo.kazakov.autoschedule.nsp.generator.individual.IndividualGenerator

class Pesa2Algorithm<I : Individual<I>>(

    mutationOperator: MutationOperator<I>,

    private val nextPopulationSelectionBatcher: NextPopulationSelectionBatcher<I>,

    individualGenerator: IndividualGenerator<I>,

    fitnessDimension: Int,

    problemName: String,

    populationSize: Int,

    private val stopConditionState: StopConditionState,

    private val statisticsCollector: StatisticsCollector<I>,
) : Algorithm<I>, PESA2<JmetalMultidimensionalIndividual<I>>(
    JmetalMultidimensionalProblem(individualGenerator, fitnessDimension, problemName),
    Int.MAX_VALUE,
    populationSize,
    populationSize,
    5,
    NullCrossover(),
    JmetalMutationOperator(mutationOperator),
    NullEvaluator(),
) {

    override fun solve(): List<I> {
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
        population: MutableList<JmetalMultidimensionalIndividual<I>>,
        offspringPopulation: MutableList<JmetalMultidimensionalIndividual<I>>,
    ): MutableList<JmetalMultidimensionalIndividual<I>> {
        return nextPopulationSelectionBatcher.selectPopulationBatches(population, offspringPopulation, getMaxPopulationSize())
            .flatMap {
                if (it.population.size + it.offspringPopulation.size <= it.selectionBatchSize) {
                    return@flatMap it.population + it.offspringPopulation
                }

                val oldMaxPopulationSize = maxPopulationSize
                maxPopulationSize = it.selectionBatchSize
                val result = super.replacement(it.population, it.offspringPopulation)
                maxPopulationSize = oldMaxPopulationSize
                result
            }
            .toMutableList()
    }
}
