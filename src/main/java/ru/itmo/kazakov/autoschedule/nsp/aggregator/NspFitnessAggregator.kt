package ru.itmo.kazakov.autoschedule.nsp.aggregator

import ru.itmo.kazakov.autoschedule.nsp.model.ScheduleIndividual

interface NspFitnessAggregator {

    fun aggregateFitness(scheduleIndividual: ScheduleIndividual<*>): Double
}
