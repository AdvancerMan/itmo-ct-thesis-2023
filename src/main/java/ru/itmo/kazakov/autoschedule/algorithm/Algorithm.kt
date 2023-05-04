package ru.itmo.kazakov.autoschedule.algorithm

import ru.itmo.kazakov.autoschedule.algorithm.individual.Individual

interface Algorithm<I : Individual<I>> {

    fun solve(): List<I>
}
