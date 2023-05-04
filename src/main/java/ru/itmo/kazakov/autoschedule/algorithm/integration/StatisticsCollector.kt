package ru.itmo.kazakov.autoschedule.algorithm.integration

import ru.itmo.kazakov.autoschedule.algorithm.individual.Individual

interface StatisticsCollector<I : Individual<I>> {

    fun initProgress()

    fun updateProgress(population: List<JmetalIndividual<I>>)
}
