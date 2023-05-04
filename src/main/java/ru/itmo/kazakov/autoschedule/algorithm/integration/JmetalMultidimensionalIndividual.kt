package ru.itmo.kazakov.autoschedule.algorithm.integration

import org.uma.jmetal.solution.AbstractSolution
import org.uma.jmetal.solution.Solution
import ru.itmo.kazakov.autoschedule.algorithm.individual.Individual

class JmetalMultidimensionalIndividual<I : Individual<I>>(

    innerIndividual: I,
) : JmetalIndividual<I>, AbstractSolution<I>(1, innerIndividual.fitness.size) {

    @Volatile
    override var innerIndividual: I = innerIndividual
        set(value) {
            variables()[0] = value
            System.arraycopy(innerIndividual.fitness, 0, objectives(), 0, objectives().size)
            field = value
        }

    constructor(individualToCopy: JmetalMultidimensionalIndividual<I>) : this(individualToCopy.innerIndividual) {
        attributes().putAll(individualToCopy.attributes())
    }

    init {
        this.innerIndividual = innerIndividual
    }

    override fun copy(): Solution<I> {
        return JmetalMultidimensionalIndividual(innerIndividual.copy())
    }
}
