package ru.itmo.kazakov.autoschedule.nsp.model

import ru.itmo.kazakov.autoschedule.algorithm.individual.Individual
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.Schedule

class ScheduleIndividual<ID>(

    val schedule: Schedule<ID>,

    override val fitness: DoubleArray,
) : Individual<ScheduleIndividual<ID>> {

    fun getFitness(fitnessType: FitnessType): Double {
        return fitness[fitnessType.fitnessIndex]
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScheduleIndividual<*>

        if (schedule != other.schedule) return false
        if (!fitness.contentEquals(other.fitness)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = schedule.hashCode()
        result = 31 * result + fitness.contentHashCode()
        return result
    }

    override fun toString(): String {
        return "ScheduleIndividual(schedule=$schedule, fitness=${fitness.contentToString()})"
    }

    override fun copy(): ScheduleIndividual<ID> {
        return parameterizedCopy()
    }

    fun parameterizedCopy(
        schedule: Schedule<ID> = this.schedule,
        fitness: DoubleArray = this.fitness,
    ): ScheduleIndividual<ID> {
        return ScheduleIndividual(schedule, fitness.copyOf())
    }
}
