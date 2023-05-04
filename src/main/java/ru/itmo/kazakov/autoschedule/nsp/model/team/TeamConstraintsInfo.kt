package ru.itmo.kazakov.autoschedule.nsp.model.team

data class TeamConstraintsInfo(
    val minRestStepsBetweenShifts: Int,
    val stepsInDay: Int,
    val forecastSteps: Int,
)
