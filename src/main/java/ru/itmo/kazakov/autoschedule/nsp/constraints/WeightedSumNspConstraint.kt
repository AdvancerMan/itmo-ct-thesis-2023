package ru.itmo.kazakov.autoschedule.nsp.constraints

import ru.itmo.kazakov.autoschedule.helper.WeightedEntity
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.AssigneeSchedule
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.Schedule
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.ShiftAssignment
import ru.itmo.kazakov.autoschedule.nsp.model.team.ShiftAssignee

class WeightedSumNspConstraint(

    private val weightedConstraints: List<WeightedEntity<NspConstraint>>,
) : NspConstraint {

    private val weightsSum: Double = weightedConstraints.sumOf { it.weight }

    override fun canAddAssignment(
        newAssignment: ShiftAssignment<*>,
        existingAssignments: List<ShiftAssignment<*>>,
    ): Boolean {
        return weightedConstraints.all {
            it.entity.canAddAssignment(newAssignment, existingAssignments)
        }
    }

    override fun calculatePenalty(assigneeSchedule: AssigneeSchedule<*>): Double {
        return weightedConstraints
            .sumOf {
                val delegatePenalty = it.entity.calculatePenalty(assigneeSchedule)
                require(delegatePenalty.isFinite()) { "Got $delegatePenalty penalty for ${it.entity}" }

                it.weight * delegatePenalty
            }
            .div(weightsSum)
    }

    override fun <ID> calculatePenaltyDelta(
        assignee: ShiftAssignee<ID>,
        originalSchedule: Schedule<ID>,
        changedSchedule: Schedule<ID>,
        removedAssignments: List<ShiftAssignment<ID>>,
        addedAssignments: List<ShiftAssignment<ID>>,
    ): Double {
        return weightedConstraints
            .sumOf {
                val delegatePenaltyDelta = it.entity.calculatePenaltyDelta(assignee, originalSchedule, changedSchedule, removedAssignments, addedAssignments)
                require(delegatePenaltyDelta.isFinite()) { "Got $delegatePenaltyDelta penalty delta for ${it.entity}" }

                it.weight * delegatePenaltyDelta
            }
            .div(weightsSum)
    }

    override fun calculateFitnessRepresentation(assigneeSchedule: AssigneeSchedule<*>): Map<String, Double> {
        return weightedConstraints
            .flatMap { it.entity.calculateFitnessRepresentation(assigneeSchedule).entries }
            .associate { it.key to it.value }
    }
}
