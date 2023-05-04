package ru.itmo.kazakov.autoschedule.nsp.fitness

import ru.itmo.kazakov.autoschedule.nsp.model.ScheduleIndividual
import ru.itmo.kazakov.autoschedule.nsp.model.forecast.Forecast
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.Schedule
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.ShiftAssignment

interface NspFitnessCalculator<ID> {

    val fitnessDimension: Int

    fun addWorkFromAssignment(
        shiftAssignment: ShiftAssignment<*>,
        plannedWork: DoubleArray,
        workMultiplier: Double = 1.0,
    )

    fun calculateFitness(schedule: Schedule<*>): DoubleArray

    fun calculateForecastPenalty(schedule: Schedule<*>, forecast: Forecast): Double

    fun recalculateFitness(
        originalIndividual: ScheduleIndividual<ID>,
        changedSchedule: Schedule<ID>,
        removedAssignments: List<ShiftAssignment<ID>>,
        addedAssignments: List<ShiftAssignment<ID>>,
    ): DoubleArray

    fun calculateFitnessRepresentation(schedule: Schedule<ID>): Map<String, Double>

    fun calculateByAssigneeFitnessRepresentation(schedule: Schedule<ID>): Map<String, Map<ID, Double>>
}
