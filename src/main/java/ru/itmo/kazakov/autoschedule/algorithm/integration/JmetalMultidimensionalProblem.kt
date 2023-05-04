package ru.itmo.kazakov.autoschedule.algorithm.integration

import org.uma.jmetal.problem.Problem
import ru.itmo.kazakov.autoschedule.algorithm.individual.Individual
import ru.itmo.kazakov.autoschedule.nsp.generator.individual.IndividualGenerator

class JmetalMultidimensionalProblem<I : Individual<I>>(

    private val individualGenerator: IndividualGenerator<I>,

    private val fitnessDimension: Int,

    private val name: String,
) : Problem<JmetalMultidimensionalIndividual<I>> {

    override fun numberOfVariables(): Int {
        return 1
    }

    override fun numberOfObjectives(): Int {
        return fitnessDimension
    }

    override fun numberOfConstraints(): Int {
        return 0
    }

    override fun name(): String {
        return name
    }

    override fun createSolution(): JmetalMultidimensionalIndividual<I> {
        return JmetalMultidimensionalIndividual(individualGenerator.generateIndividual())
    }

    override fun evaluate(solution: JmetalMultidimensionalIndividual<I>): JmetalMultidimensionalIndividual<I> {
        return solution
    }
}
