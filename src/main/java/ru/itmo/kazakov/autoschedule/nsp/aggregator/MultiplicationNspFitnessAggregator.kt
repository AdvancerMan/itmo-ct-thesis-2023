package ru.itmo.kazakov.autoschedule.nsp.aggregator

import ru.itmo.kazakov.autoschedule.nsp.model.ScheduleIndividual

class MultiplicationNspFitnessAggregator : NspFitnessAggregator {

    override fun aggregateFitness(scheduleIndividual: ScheduleIndividual<*>): Double {
        var result = 1.0
        scheduleIndividual.fitness.forEach { result *= 1 + it }
        return result
    }
}
