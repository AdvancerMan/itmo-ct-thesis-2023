package ru.itmo.kazakov.autoschedule.algorithm.individual

interface Individual<Self : Individual<Self>> {

    val fitness: DoubleArray

    fun copy(): Self
}
