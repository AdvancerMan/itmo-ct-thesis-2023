package ru.itmo.kazakov.autoschedule.algorithm.operator

import ru.itmo.kazakov.autoschedule.algorithm.individual.Individual

interface CrossoverOperator<I : Individual<I>> {
    fun crossover(firstIndividual: I, secondIndividual: I): I
}
