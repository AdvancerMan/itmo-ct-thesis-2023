package ru.itmo.kazakov.autoschedule.nsp.solver.jmetal.onedimensional

import org.uma.jmetal.problem.Problem
import ru.itmo.kazakov.autoschedule.nsp.aggregator.NspFitnessAggregator
import ru.itmo.kazakov.autoschedule.nsp.generator.individual.IndividualGenerator
import ru.itmo.kazakov.autoschedule.nsp.model.ScheduleIndividual

class JmetalOnedimensionalScheduleProblem<ID>(

    private val individualGenerator: IndividualGenerator<ScheduleIndividual<ID>>,

    private val name: String,

    private val nspFitnessAggregator: NspFitnessAggregator,
) : Problem<JmetalOnedimensionalScheduleIndividual<ID>> {

    override fun numberOfVariables(): Int {
        return 1
    }

    override fun numberOfObjectives(): Int {
        return 1
    }

    override fun numberOfConstraints(): Int {
        return 1
    }

    override fun name(): String {
        return name
    }

    override fun createSolution(): JmetalOnedimensionalScheduleIndividual<ID> {
        return JmetalOnedimensionalScheduleIndividual(individualGenerator.generateIndividual(), nspFitnessAggregator)
    }

    override fun evaluate(
        solution: JmetalOnedimensionalScheduleIndividual<ID>,
    ): JmetalOnedimensionalScheduleIndividual<ID> {
        return solution
    }
}
