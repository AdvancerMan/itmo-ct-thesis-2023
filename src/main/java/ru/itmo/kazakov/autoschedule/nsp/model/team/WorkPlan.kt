package ru.itmo.kazakov.autoschedule.nsp.model.team

data class WorkPlan(
    val workDays: Int,
    val daysOff: Int,
    val daysOffset: Int = 0,
)
