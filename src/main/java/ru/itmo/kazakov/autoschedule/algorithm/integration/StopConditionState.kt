package ru.itmo.kazakov.autoschedule.algorithm.integration

interface StopConditionState {

    fun isStoppingConditionReached(): Boolean

    fun initProgress()

    fun updateProgress()
}
