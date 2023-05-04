package ru.itmo.kazakov.autoschedule.algorithm.integration

import org.uma.jmetal.solution.Solution
import ru.itmo.kazakov.autoschedule.algorithm.individual.Individual

interface JmetalIndividual<I : Individual<I>> : Solution<I> {

    var innerIndividual: I
}
