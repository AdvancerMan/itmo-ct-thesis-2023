package ru.itmo.kazakov.autoschedule.nsp.operator

import ru.itmo.kazakov.autoschedule.algorithm.individual.Individual
import ru.itmo.kazakov.autoschedule.algorithm.integration.JmetalIndividual

interface BestIndividualSelectionOperator<I : Individual<I>> {

    fun <J : JmetalIndividual<I>> select(population: List<J>): J
}
