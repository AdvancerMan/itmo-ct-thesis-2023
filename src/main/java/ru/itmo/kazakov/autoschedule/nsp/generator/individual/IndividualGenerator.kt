package ru.itmo.kazakov.autoschedule.nsp.generator.individual

import ru.itmo.kazakov.autoschedule.algorithm.individual.Individual

interface IndividualGenerator<I : Individual<I>> {

    fun generateIndividual(): I
}
