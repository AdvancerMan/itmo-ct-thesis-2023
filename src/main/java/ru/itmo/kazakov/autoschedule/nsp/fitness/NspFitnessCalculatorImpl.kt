package ru.itmo.kazakov.autoschedule.nsp.fitness

import ru.itmo.kazakov.autoschedule.helper.BreaksHelper
import ru.itmo.kazakov.autoschedule.helper.NspHelper
import ru.itmo.kazakov.autoschedule.nsp.constraints.NspConstraint
import ru.itmo.kazakov.autoschedule.nsp.model.ScheduleIndividual
import ru.itmo.kazakov.autoschedule.nsp.model.forecast.Forecast
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.AssigneeSchedule
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.Schedule
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.ShiftAssignment
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.cache.ScheduleCacheKey
import ru.itmo.kazakov.autoschedule.nsp.model.team.Team
import ru.itmo.kazakov.autoschedule.nsp.model.team.breaks.BreaksConstraintsInfo
import ru.itmo.kazakov.autoschedule.nsp.model.team.breaks.DataBreaksConstraintsInfo
import kotlin.math.max

class NspFitnessCalculatorImpl<ID>(

    private val team: Team<ID>,

    private val forecast: Forecast,

    private val constraints: List<NspConstraint>,

    private val breaksHelper: BreaksHelper,

    private val breaksConstraintsInfo: BreaksConstraintsInfo,
) : NspFitnessCalculator<ID> {

    override val fitnessDimension: Int
        get() = 1 + constraints.size

    override fun addWorkFromAssignment(
        shiftAssignment: ShiftAssignment<*>,
        plannedWork: DoubleArray,
        workMultiplier: Double,
    ) {
        shiftAssignment.shift.steps.forEach { step ->
            addWorkFromStep(step, shiftAssignment, plannedWork, workMultiplier * 1.0)
        }

        if (breaksConstraintsInfo !is DataBreaksConstraintsInfo) {
            return
        }

        shiftAssignment.sortedBreaks.forEach { `break` ->
            for (stepInShift in `break`.breakStartInShift..breaksHelper.getBreakEnd(breaksConstraintsInfo, `break`)) {
                val step = shiftAssignment.shift.startStepIndex + stepInShift
                addWorkFromStep(step, shiftAssignment, plannedWork, workMultiplier * -1.0)
            }
        }
    }

    private fun addWorkFromStep(
        step: Int,
        shiftAssignment: ShiftAssignment<*>,
        plannedWork: DoubleArray,
        multiplier: Double,
    ) {
        if (step !in plannedWork.indices) {
            return
        }

        plannedWork[step] += shiftAssignment.assignee.performance * multiplier
    }

    override fun calculateFitness(schedule: Schedule<*>): DoubleArray {
        val forecastPenalty = calculateForecastPenalty(schedule, forecast)

        val result = DoubleArray(fitnessDimension) { 0.0 }
        result[0] = forecastPenalty
        constraints
            .forEachIndexed { i, constraint ->
                result[i + 1] = team
                    .members
                    .sumOf { assignee ->
                        val assigneeSchedule = schedule.assignments[assignee]
                            ?: AssigneeSchedule(assignee, emptyList())

                        constraint.calculatePenalty(assigneeSchedule)
                    }
                    .div(team.members.size)
            }

        return result
    }

    override fun calculateForecastPenalty(schedule: Schedule<*>, forecast: Forecast): Double {
        val plannedWork = calculatePlannedWork(schedule)

        var relativeStepPenaltySize = 0
        val relativeStepPenalty = forecast.stepsForecast
            .count { it > forecast.epsilon }
            .let { DoubleArray(it) }

        var forecastCoverPenalty = 0.0
        forecast.stepsForecast.indices.forEach { i ->
            if (forecast.stepsForecast[i] <= forecast.epsilon) {
                return@forEach
            }
            relativeStepPenalty[relativeStepPenaltySize] = plannedWork[i] / forecast.stepsForecast[i]

            forecastCoverPenalty += max(0.0, forecast.stepsForecast[i] - plannedWork[i])
                .div(forecast.stepsForecast[i])

            relativeStepPenaltySize++
        }
        if (relativeStepPenaltySize != 0) {
            forecastCoverPenalty /= relativeStepPenaltySize
        }

        return (1 + NspHelper.calculateDispersionIndex(relativeStepPenalty))
            .times(1 + forecastCoverPenalty)
            .minus(1)
    }

    private fun calculatePlannedWork(schedule: Schedule<*>): DoubleArray {
        return schedule.computeIfAbsentCacheValue(ScheduleCacheKey.PlannedWorkByForecastStep) {
            val plannedWork = DoubleArray(forecast.stepsForecast.size) { 0.0 }
            schedule
                .assignments
                .forEach { (_, assigneeSchedule) ->
                    assigneeSchedule.sortedAssignments.forEach {
                        addWorkFromAssignment(it, plannedWork)
                    }
                }
            plannedWork
        }
    }

    private fun recalculatePlannedWork(
        originalIndividual: ScheduleIndividual<ID>,
        changedSchedule: Schedule<ID>,
        removedAssignments: List<ShiftAssignment<ID>>,
        addedAssignments: List<ShiftAssignment<ID>>,
    ): DoubleArray {
        return changedSchedule.computeIfAbsentCacheValue(ScheduleCacheKey.PlannedWorkByForecastStep) {
            val plannedWork = calculatePlannedWork(originalIndividual.schedule).copyOf()
            addedAssignments.forEach { assignment -> addWorkFromAssignment(assignment, plannedWork) }
            removedAssignments.forEach { assignment -> addWorkFromAssignment(assignment, plannedWork, -1.0) }
            plannedWork
        }
    }

    override fun recalculateFitness(
        originalIndividual: ScheduleIndividual<ID>,
        changedSchedule: Schedule<ID>,
        removedAssignments: List<ShiftAssignment<ID>>,
        addedAssignments: List<ShiftAssignment<ID>>,
    ): DoubleArray {
        val resultFitness = originalIndividual.fitness.copyOf()
        recalculatePlannedWork(originalIndividual, changedSchedule, removedAssignments, addedAssignments)
        resultFitness[0] = calculateForecastPenalty(changedSchedule, forecast)

        val affectedAssignees = removedAssignments.map { it.assignee }
            .plus(addedAssignments.map { it.assignee })
            .toSet()

        constraints.forEachIndexed { constraintIndex, constraint ->
            affectedAssignees.forEach { assignee ->
                val penaltyDelta = constraint.calculatePenaltyDelta(
                    assignee,
                    originalIndividual.schedule,
                    changedSchedule,
                    removedAssignments.filter { it.assignee == assignee },
                    addedAssignments.filter { it.assignee == assignee },
                )
                resultFitness[constraintIndex + 1] += penaltyDelta / team.members.size
            }
        }
        return resultFitness
    }

    override fun calculateFitnessRepresentation(schedule: Schedule<ID>): Map<String, Double> {
        return calculateByAssigneeFitnessRepresentation(schedule)
            .mapValues { it.value.values.sum() }
            .plus(mapOf("forecastPenalty" to calculateForecastPenalty(schedule, forecast)))
    }

    override fun calculateByAssigneeFitnessRepresentation(schedule: Schedule<ID>): Map<String, Map<ID, Double>> {
        return team
            .members
            .flatMap { assignee ->
                val assigneeSchedule = schedule.assignments[assignee]
                    ?: AssigneeSchedule(assignee, emptyList())

                constraints
                    .flatMap { it.calculateFitnessRepresentation(assigneeSchedule).entries }
                    .map { Triple(it.key, assignee.identifier, it.value) }
            }
            .groupBy { it.first }
            .mapValues { (_, fitnessEntries) ->
                fitnessEntries.associate { it.second to it.third }
            }
    }
}
