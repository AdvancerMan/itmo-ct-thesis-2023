package ru.itmo.kazakov.autoschedule.nsp.operator.mutation

import ru.itmo.kazakov.autoschedule.algorithm.individual.Individual
import ru.itmo.kazakov.autoschedule.algorithm.operator.MutationOperator
import ru.itmo.kazakov.autoschedule.helper.WeightedEntity
import ru.itmo.kazakov.autoschedule.helper.WeightedRandomizerImpl
import kotlin.random.Random

class CompositeMutationOperator<I : Individual<I>>(

    mutationOperators: List<WeightedEntity<MutationOperator<I>>>,

    random: Random,
) : MutationOperator<I> {

    private val weightedRandomizer = WeightedRandomizerImpl(
        mutationOperators,
        random,
    )

    override fun mutate(individual: I): I {
        return weightedRandomizer.nextEntity().mutate(individual)
    }
}
