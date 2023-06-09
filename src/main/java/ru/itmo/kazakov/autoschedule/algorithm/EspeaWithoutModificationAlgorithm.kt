package ru.itmo.kazakov.autoschedule.algorithm

import org.uma.jmetal.algorithm.multiobjective.espea.ESPEA
import org.uma.jmetal.algorithm.multiobjective.espea.util.EnergyArchive
import org.uma.jmetal.algorithm.multiobjective.espea.util.ScalarizationWrapper
import org.uma.jmetal.operator.crossover.impl.NullCrossover
import org.uma.jmetal.operator.selection.impl.RandomSelection
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator
import ru.itmo.kazakov.autoschedule.algorithm.individual.Individual
import ru.itmo.kazakov.autoschedule.algorithm.integration.JmetalMultidimensionalIndividual
import ru.itmo.kazakov.autoschedule.algorithm.integration.JmetalMultidimensionalProblem
import ru.itmo.kazakov.autoschedule.algorithm.integration.JmetalMutationOperator
import ru.itmo.kazakov.autoschedule.algorithm.integration.StatisticsCollector
import ru.itmo.kazakov.autoschedule.algorithm.integration.StopConditionState
import ru.itmo.kazakov.autoschedule.algorithm.operator.MutationOperator
import ru.itmo.kazakov.autoschedule.nsp.generator.individual.IndividualGenerator

class EspeaWithoutModificationAlgorithm<I : Individual<I>>(

    mutationOperator: MutationOperator<I>,

    individualGenerator: IndividualGenerator<I>,

    fitnessDimension: Int,

    problemName: String,

    populationSize: Int,

    private val stopConditionState: StopConditionState,

    private val statisticsCollector: StatisticsCollector<I>,
) : Algorithm<I>, ESPEA<JmetalMultidimensionalIndividual<I>>(
    JmetalMultidimensionalProblem(individualGenerator, fitnessDimension, problemName),
    Int.MAX_VALUE,
    populationSize,
    NullCrossover(),
    NullCrossover(),
    JmetalMutationOperator(mutationOperator),
    RandomSelection(),
    ScalarizationWrapper(ScalarizationWrapper.ScalarizationType.UNIFORM),
    SequentialSolutionListEvaluator(),
    true,
    EnergyArchive.ReplacementStrategy.LARGEST_DIFFERENCE,
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
}
