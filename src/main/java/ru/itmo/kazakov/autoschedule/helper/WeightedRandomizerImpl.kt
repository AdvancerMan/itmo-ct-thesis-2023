package ru.itmo.kazakov.autoschedule.helper

import java.util.Arrays
import kotlin.random.Random

class WeightedRandomizerImpl<T>(

    weightedEntities: List<WeightedEntity<T>>,

    private val random: Random,
) : WeightedRandomizer<T> {

    private val entities: List<T>
    private val summedWeights: DoubleArray

    init {
        val weightSum = weightedEntities.sumOf { it.weight }
        this.entities = weightedEntities.map { it.entity }
        this.summedWeights = DoubleArray(weightedEntities.size)

        repeat(weightedEntities.size) {
            summedWeights[it] = weightedEntities[it].weight / weightSum + summedWeights.getOrElse(it - 1) { 0.0 }
        }
        summedWeights[summedWeights.size - 1] = 1.0
    }

    override fun nextEntity(): T {
        val generatedWeight = random.nextDouble()
        val index = Arrays.binarySearch(summedWeights, generatedWeight)

        return if (index >= 0) {
            entities[index]
        } else {
            entities[-index - 1]
        }
    }
}
