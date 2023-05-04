package ru.itmo.kazakov.autoschedule.nsp.operator.mutation

import ru.itmo.kazakov.autoschedule.algorithm.operator.MutationOperator
import ru.itmo.kazakov.autoschedule.nsp.fitness.NspFitnessCalculator
import ru.itmo.kazakov.autoschedule.nsp.model.ScheduleIndividual
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.AssigneeSchedule
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.Schedule
import ru.itmo.kazakov.autoschedule.nsp.model.team.ShiftAssignee
import kotlin.random.Random

abstract class RandomUserScheduleMutationOperator<ID>(

    private val fitnessCalculator: NspFitnessCalculator<ID>,

    protected val random: Random,
) : MutationOperator<ScheduleIndividual<ID>> {

    abstract fun mutateAssigneeSchedule(assigneeSchedule: AssigneeSchedule<ID>): AssigneeSchedule<ID>

    protected open fun selectUsersForMutation(individual: ScheduleIndividual<ID>): Collection<ShiftAssignee<ID>> {
        return individual.schedule.assignments.keys
    }

    override fun mutate(individual: ScheduleIndividual<ID>): ScheduleIndividual<ID> {
        val newAssignmentsMap = individual.schedule.assignments.toMutableMap()
        val assignee = selectUsersForMutation(individual).randomOrNull(random)
            ?: return individual

        val scheduleToMutate = newAssignmentsMap.remove(assignee)
            ?: throw IllegalStateException("Kotlin map invariants violated")
        val mutatedSchedule = mutateAssigneeSchedule(scheduleToMutate)

        val removedAssignments = scheduleToMutate.sortedAssignments.toMutableList()
        mutatedSchedule.sortedAssignments.forEach { removedAssignments -= it }

        val addedAssignments = mutatedSchedule.sortedAssignments.toMutableList()
        scheduleToMutate.sortedAssignments.forEach { addedAssignments -= it }

        newAssignmentsMap[assignee] = mutatedSchedule
        val changedSchedule = Schedule(assignments = newAssignmentsMap)
        return individual.parameterizedCopy(
            schedule = changedSchedule,
            fitness = fitnessCalculator.recalculateFitness(
                individual,
                changedSchedule,
                removedAssignments,
                addedAssignments,
            ),
        )
    }
}
