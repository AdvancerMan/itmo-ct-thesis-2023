package ru.itmo.kazakov.autoschedule.algorithm.integration

import ru.itmo.kazakov.autoschedule.algorithm.individual.Individual
import ru.itmo.kazakov.autoschedule.algorithm.operator.MutationOperator

class JmetalMutationOperator<I : Individual<I>, J : JmetalIndividual<I>>(

    private val mutationOperator: MutationOperator<I>,
) : org.uma.jmetal.operator.mutation.MutationOperator<J> {

    override fun execute(solution: J): J {
        val individual = solution.innerIndividual
        val mutated = mutationOperator.mutate(individual)
        solution.innerIndividual = mutated
        return solution
    }

    override fun mutationProbability(): Double {
        return 1.0
    }
}
