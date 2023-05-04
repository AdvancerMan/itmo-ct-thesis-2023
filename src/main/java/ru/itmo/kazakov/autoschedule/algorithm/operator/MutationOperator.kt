package ru.itmo.kazakov.autoschedule.algorithm.operator

import ru.itmo.kazakov.autoschedule.algorithm.individual.Individual

interface MutationOperator<I : Individual<I>> {
    fun mutate(individual: I): I
}
