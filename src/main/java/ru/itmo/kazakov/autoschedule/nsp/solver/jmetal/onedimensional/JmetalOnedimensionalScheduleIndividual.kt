package ru.itmo.kazakov.autoschedule.nsp.solver.jmetal.onedimensional

import org.uma.jmetal.solution.AbstractSolution
import org.uma.jmetal.solution.Solution
import ru.itmo.kazakov.autoschedule.algorithm.integration.JmetalIndividual
import ru.itmo.kazakov.autoschedule.nsp.aggregator.NspFitnessAggregator
import ru.itmo.kazakov.autoschedule.nsp.model.FitnessType
import ru.itmo.kazakov.autoschedule.nsp.model.ScheduleIndividual

class JmetalOnedimensionalScheduleIndividual<ID>(

    innerIndividual: ScheduleIndividual<ID>,

    private val nspFitnessAggregator: NspFitnessAggregator,
) : JmetalIndividual<ScheduleIndividual<ID>>,
    AbstractSolution<ScheduleIndividual<ID>>(1, 1, 1) {

    @Volatile
    override var innerIndividual: ScheduleIndividual<ID> = innerIndividual
        set(value) {
            variables()[0] = value
            objectives()[0] = nspFitnessAggregator.aggregateFitness(value)
            constraints()[0] = -value.getFitness(FitnessType.HARD_CONSTRAINTS_PENALTY)
            field = value
        }

    constructor(individualToCopy: JmetalOnedimensionalScheduleIndividual<ID>) : this(
        individualToCopy.innerIndividual.copy(),
        individualToCopy.nspFitnessAggregator,
    ) {
        attributes().putAll(individualToCopy.attributes())
    }

    init {
        this.innerIndividual = innerIndividual
    }

    override fun copy(): Solution<ScheduleIndividual<ID>> {
        return JmetalOnedimensionalScheduleIndividual(this)
    }
}
