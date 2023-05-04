package ru.itmo.kazakov.autoschedule.algorithm.operator

import ru.itmo.kazakov.autoschedule.algorithm.individual.Individual
import ru.itmo.kazakov.autoschedule.algorithm.integration.JmetalIndividual

data class PopulationBatch<I : Individual<I>, J : JmetalIndividual<I>>(
    val population: MutableList<J>,
    val offspringPopulation: MutableList<J>,
    val selectionBatchSize: Int,
)
