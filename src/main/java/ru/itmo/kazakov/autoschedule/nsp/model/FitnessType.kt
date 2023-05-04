package ru.itmo.kazakov.autoschedule.nsp.model

enum class FitnessType(val fitnessIndex: Int) {
    FORECAST_PENALTY(0),
    HARD_CONSTRAINTS_PENALTY(1),
    SOFT_CONSTRAINTS_PENALTY(2),
    ;
}
