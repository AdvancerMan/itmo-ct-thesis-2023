package ru.itmo.kazakov.autoschedule.nsp.generator.individual

import ru.itmo.kazakov.autoschedule.algorithm.individual.Individual
import ru.itmo.kazakov.autoschedule.helper.WeightedEntity
import ru.itmo.kazakov.autoschedule.helper.WeightedRandomizerImpl
import kotlin.random.Random

class CompositeIndividualGenerator<I : Individual<I>>(

    delegateGenerators: List<WeightedEntity<IndividualGenerator<I>>>,

    random: Random,
) : IndividualGenerator<I> {

    private val weightedRandomizer = WeightedRandomizerImpl(
        delegateGenerators,
        random,
    )

    override fun generateIndividual(): I {
        return weightedRandomizer.nextEntity().generateIndividual()
    }
}
