package ru.itmo.kazakov.autoschedule.algorithm.operator

import ru.itmo.kazakov.autoschedule.algorithm.individual.Individual
import ru.itmo.kazakov.autoschedule.algorithm.integration.JmetalIndividual

interface NextPopulationSelectionBatcher<I : Individual<I>> {

    fun <J : JmetalIndividual<I>> selectPopulationBatches(
        population: MutableList<J>,
        offspringPopulation: MutableList<J>,
        populationSize: Int,
    ): Sequence<PopulationBatch<I, J>>
}
