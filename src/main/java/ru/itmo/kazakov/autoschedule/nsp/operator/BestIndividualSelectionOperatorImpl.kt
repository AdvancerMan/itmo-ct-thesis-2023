package ru.itmo.kazakov.autoschedule.nsp.operator

import ru.itmo.kazakov.autoschedule.algorithm.integration.JmetalIndividual
import ru.itmo.kazakov.autoschedule.nsp.aggregator.NspFitnessAggregator
import ru.itmo.kazakov.autoschedule.nsp.model.FitnessType
import ru.itmo.kazakov.autoschedule.nsp.model.ScheduleIndividual

class BestIndividualSelectionOperatorImpl<ID>(

    private val fitnessEpsilon: Double,

    private val nspFitnessAggregator: NspFitnessAggregator,
) : BestIndividualSelectionOperator<ScheduleIndividual<ID>> {

    override fun <J : JmetalIndividual<ScheduleIndividual<ID>>> select(population: List<J>): J {
        val minHardConstraintsPenalty = population
            .minOf { it.innerIndividual.getFitness(FitnessType.HARD_CONSTRAINTS_PENALTY) }

        return population
            .filter { it.innerIndividual.getFitness(FitnessType.HARD_CONSTRAINTS_PENALTY) <= minHardConstraintsPenalty + fitnessEpsilon }
            .minBy { nspFitnessAggregator.aggregateFitness(it.innerIndividual) }
    }
}
