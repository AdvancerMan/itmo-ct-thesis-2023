package ru.itmo.kazakov.autoschedule.nsp.operator

import ru.itmo.kazakov.autoschedule.algorithm.integration.JmetalIndividual
import ru.itmo.kazakov.autoschedule.algorithm.operator.NextPopulationSelectionBatcher
import ru.itmo.kazakov.autoschedule.algorithm.operator.PopulationBatch
import ru.itmo.kazakov.autoschedule.nsp.model.FitnessType
import ru.itmo.kazakov.autoschedule.nsp.model.ScheduleIndividual

class NspNextPopulationSelectionBatcher<ID>(

    private val fitnessEpsilon: Double,

    private val bestIndividualSelectionOperator: BestIndividualSelectionOperator<ScheduleIndividual<ID>>,
) : NextPopulationSelectionBatcher<ScheduleIndividual<ID>> {

    override fun <J : JmetalIndividual<ScheduleIndividual<ID>>> selectPopulationBatches(
        population: MutableList<J>,
        offspringPopulation: MutableList<J>,
        populationSize: Int,
    ): Sequence<PopulationBatch<ScheduleIndividual<ID>, J>> = sequence {
        val (softPopulation, hardPopulation) = population
            .partition { it.innerIndividual.getFitness(FitnessType.HARD_CONSTRAINTS_PENALTY) <= fitnessEpsilon }
        val (softOffspringPopulation, hardOffspringPopulation) = offspringPopulation
            .partition { it.innerIndividual.getFitness(FitnessType.HARD_CONSTRAINTS_PENALTY) <= fitnessEpsilon }

        val bestIndividual = bestIndividualSelectionOperator.select(population + offspringPopulation)
        yield(PopulationBatch(mutableListOf(), mutableListOf(bestIndividual), 1))

        yield(PopulationBatch(hardPopulation.toMutableList(), hardOffspringPopulation.toMutableList(), populationSize / 2))
        yield(PopulationBatch(softPopulation.toMutableList(), softOffspringPopulation.toMutableList(), populationSize / 2 - 1))
    }
}
