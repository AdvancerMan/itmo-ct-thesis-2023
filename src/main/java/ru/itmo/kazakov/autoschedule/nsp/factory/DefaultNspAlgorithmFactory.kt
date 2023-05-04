package ru.itmo.kazakov.autoschedule.nsp.factory

import ru.itmo.kazakov.autoschedule.algorithm.Algorithm
import ru.itmo.kazakov.autoschedule.algorithm.EspeaAlgorithm
import ru.itmo.kazakov.autoschedule.algorithm.integration.StatisticsCollector
import ru.itmo.kazakov.autoschedule.algorithm.integration.StopConditionState
import ru.itmo.kazakov.autoschedule.algorithm.operator.MutationOperator
import ru.itmo.kazakov.autoschedule.helper.BreaksHelper
import ru.itmo.kazakov.autoschedule.helper.BreaksHelperImpl
import ru.itmo.kazakov.autoschedule.helper.weighted
import ru.itmo.kazakov.autoschedule.nsp.aggregator.NspFitnessAggregator
import ru.itmo.kazakov.autoschedule.nsp.constraints.NspConstraint
import ru.itmo.kazakov.autoschedule.nsp.constraints.WeightedSumNspConstraint
import ru.itmo.kazakov.autoschedule.nsp.constraints.breaks.hard.BreaksAreInShiftNspConstraint
import ru.itmo.kazakov.autoschedule.nsp.constraints.breaks.hard.BreaksCountNspConstraint
import ru.itmo.kazakov.autoschedule.nsp.constraints.breaks.hard.FirstBreakIsCommonNspConstraint
import ru.itmo.kazakov.autoschedule.nsp.constraints.breaks.hard.WorkPeriodsNspConstraint
import ru.itmo.kazakov.autoschedule.nsp.constraints.checker.AssignmentConstraintsChecker
import ru.itmo.kazakov.autoschedule.nsp.constraints.checker.AssignmentConstraintsCheckerImpl
import ru.itmo.kazakov.autoschedule.nsp.constraints.shift.hard.ForbiddenStepsNspConstraint
import ru.itmo.kazakov.autoschedule.nsp.constraints.shift.hard.RestBetweenShiftsNspConstraint
import ru.itmo.kazakov.autoschedule.nsp.constraints.shift.hard.ShiftIsInPeriodNspConstraint
import ru.itmo.kazakov.autoschedule.nsp.constraints.shift.hard.ShiftSizeNspConstraint
import ru.itmo.kazakov.autoschedule.nsp.constraints.shift.hard.ShiftsPerDayNspConstraint
import ru.itmo.kazakov.autoschedule.nsp.constraints.shift.hard.StartStepsNspConstraint
import ru.itmo.kazakov.autoschedule.nsp.constraints.shift.soft.NormNspConstraint
import ru.itmo.kazakov.autoschedule.nsp.constraints.shift.soft.UniformShiftSizeNspConstraint
import ru.itmo.kazakov.autoschedule.nsp.fitness.NspFitnessCalculator
import ru.itmo.kazakov.autoschedule.nsp.fitness.NspFitnessCalculatorImpl
import ru.itmo.kazakov.autoschedule.nsp.generator.breaks.BreaksGenerator
import ru.itmo.kazakov.autoschedule.nsp.generator.breaks.ConstraintsAwareBreaksGenerator
import ru.itmo.kazakov.autoschedule.nsp.generator.breaks.cache.CachedWorkBetweenBreaksCalculator
import ru.itmo.kazakov.autoschedule.nsp.generator.individual.CompositeIndividualGenerator
import ru.itmo.kazakov.autoschedule.nsp.generator.individual.UniformScheduleIndividualGenerator
import ru.itmo.kazakov.autoschedule.nsp.model.ScheduleIndividual
import ru.itmo.kazakov.autoschedule.nsp.model.forecast.Forecast
import ru.itmo.kazakov.autoschedule.nsp.model.team.Team
import ru.itmo.kazakov.autoschedule.nsp.model.team.breaks.DataBreaksConstraintsInfo
import ru.itmo.kazakov.autoschedule.nsp.operator.BestIndividualSelectionOperator
import ru.itmo.kazakov.autoschedule.nsp.operator.BestIndividualSelectionOperatorImpl
import ru.itmo.kazakov.autoschedule.nsp.operator.NspNextPopulationSelectionBatcher
import ru.itmo.kazakov.autoschedule.nsp.operator.mutation.CompositeMutationOperator
import ru.itmo.kazakov.autoschedule.nsp.operator.mutation.breaks.MoveShiftBreakMutationOperator
import ru.itmo.kazakov.autoschedule.nsp.operator.mutation.shift.AddScheduleShiftMutationOperator
import ru.itmo.kazakov.autoschedule.nsp.operator.mutation.shift.FixNormShiftMutationOperator
import ru.itmo.kazakov.autoschedule.nsp.operator.mutation.shift.MoveScheduleShiftMutationOperator
import ru.itmo.kazakov.autoschedule.nsp.operator.mutation.shift.UniformationShiftMutationOperator
import kotlin.random.Random

class DefaultNspAlgorithmFactory(

    private val populationSize: Int,

    private val fitnessEpsilon: Double,

    override val nspFitnessAggregator: NspFitnessAggregator,

    private val weightsContainer: AutoscheduleWeightsContainer,

    private val random: Random,
) : NspAlgorithmFactory {

    private fun <ID> buildConstraintsComponents(
        forecast: Forecast,
        team: Team<ID>,
    ): Triple<BreaksHelper, NspFitnessCalculator<ID>, AssignmentConstraintsChecker> {
        val breaksHelper = BreaksHelperImpl()

        val hardConstraints = createHardConstraints(team, forecast, breaksHelper)
        val softConstraints = createSoftConstraints()

        val fitnessCalculator = NspFitnessCalculatorImpl(
            team,
            forecast,
            listOf(hardConstraints, softConstraints),
            breaksHelper,
            team.breaksConstraintsInfo,
        )
        val assignmentConstraintsChecker = AssignmentConstraintsCheckerImpl(listOf(hardConstraints, softConstraints))

        return Triple(breaksHelper, fitnessCalculator, assignmentConstraintsChecker)
    }

    override fun <ID> buildFitnessCalculator(forecast: Forecast, team: Team<ID>): NspFitnessCalculator<ID> {
        return buildConstraintsComponents(forecast, team).second
    }

    override fun <ID> buildBreaksGenerator(forecast: Forecast, team: Team<ID>): BreaksGenerator {
        val workBetweenBreaksCalculator = CachedWorkBetweenBreaksCalculator(
            team.breaksConstraintsInfo,
            team.teamConstraintsInfo,
            random,
        )

        return ConstraintsAwareBreaksGenerator(
            BreaksHelperImpl(),
            team.breaksConstraintsInfo,
            workBetweenBreaksCalculator,
            random,
        )
    }

    override fun <ID> buildBestIndividualSelectionOperator(
        forecast: Forecast,
        team: Team<ID>,
    ): BestIndividualSelectionOperator<ScheduleIndividual<ID>> {
        return BestIndividualSelectionOperatorImpl(
            fitnessEpsilon,
            nspFitnessAggregator,
        )
    }

    override fun <ID> buildAlgorithm(
        forecast: Forecast,
        team: Team<ID>,
        stopConditionState: StopConditionState,
        statisticsCollector: StatisticsCollector<ScheduleIndividual<ID>>,
    ): Algorithm<ScheduleIndividual<ID>> {
        val (breaksHelper, fitnessCalculator, assignmentConstraintsChecker) = buildConstraintsComponents(forecast, team)

        val workBetweenBreaksCalculator = CachedWorkBetweenBreaksCalculator(
            team.breaksConstraintsInfo,
            team.teamConstraintsInfo,
            random,
        )
        val breaksGenerator = ConstraintsAwareBreaksGenerator(
            breaksHelper,
            team.breaksConstraintsInfo,
            workBetweenBreaksCalculator,
            random,
        )
        val individualGenerator = createIndividualGenerator(
            forecast,
            team,
            fitnessCalculator,
            assignmentConstraintsChecker,
            breaksGenerator,
            random,
        )

        val mutationOperator = createMutationOperator(
            fitnessCalculator,
            assignmentConstraintsChecker,
            random,
            team,
            forecast,
            breaksGenerator,
        )

        val nextPopulationSelectionBatcher = NspNextPopulationSelectionBatcher(
            fitnessEpsilon,
            buildBestIndividualSelectionOperator(forecast, team),
        )

        return EspeaAlgorithm(
            mutationOperator,
            nextPopulationSelectionBatcher,
            individualGenerator,
            fitnessCalculator.fitnessDimension,
            "Support planning problem",
            populationSize,
            stopConditionState,
            statisticsCollector,
        )
    }

    private fun <ID> createMutationOperator(
        fitnessCalculator: NspFitnessCalculator<ID>,
        assignmentConstraintsChecker: AssignmentConstraintsChecker,
        random: Random,
        team: Team<ID>,
        forecast: Forecast,
        breaksGenerator: BreaksGenerator,
    ): MutationOperator<ScheduleIndividual<ID>> {
        return listOfNotNull(
            MoveScheduleShiftMutationOperator<ID>(fitnessCalculator, random, forecast.size)
                .weighted(weightsContainer.getOrDefault(AutoscheduleWeightName.MUTATION_MOVE_SCHEDULE, 1.0)),
            UniformationShiftMutationOperator<ID>(fitnessCalculator, random, breaksGenerator)
                .weighted(weightsContainer.getOrDefault(AutoscheduleWeightName.MUTATION_UNIFORMATION, 1.0)),
            FixNormShiftMutationOperator<ID>(fitnessCalculator, random, breaksGenerator)
                .weighted(weightsContainer.getOrDefault(AutoscheduleWeightName.MUTATION_FIX_NORM, 1.0)),
            AddScheduleShiftMutationOperator<ID>(forecast, fitnessCalculator, assignmentConstraintsChecker, breaksGenerator, random)
                .weighted(weightsContainer.getOrDefault(AutoscheduleWeightName.MUTATION_ADD_SCHEDULE, 1.0)),
            MoveShiftBreakMutationOperator<ID>(fitnessCalculator, random)
                .weighted(weightsContainer.getOrDefault(AutoscheduleWeightName.MUTATION_MOVE_BREAK, 1.0))
                .takeIf { team.breaksConstraintsInfo is DataBreaksConstraintsInfo },
        )
            .let { CompositeMutationOperator(it, random) }
    }

    private fun <ID> createIndividualGenerator(
        forecast: Forecast,
        team: Team<ID>,
        fitnessCalculator: NspFitnessCalculator<ID>,
        assignmentConstraintsChecker: AssignmentConstraintsChecker,
        breaksGenerator: BreaksGenerator,
        random: Random,
    ): CompositeIndividualGenerator<ScheduleIndividual<ID>> {
        return listOf(
            UniformScheduleIndividualGenerator(
                forecast,
                team,
                fitnessCalculator,
                assignmentConstraintsChecker,
                breaksGenerator,
                random,
            ).weighted(weightsContainer.getOrDefault(AutoscheduleWeightName.GENERATOR_UNIFORM, 1.0)),
        )
            .let { CompositeIndividualGenerator(it, random) }
    }

    private fun createSoftConstraints(): NspConstraint {
        return listOf(
            UniformShiftSizeNspConstraint()
                .weighted(weightsContainer.getOrDefault(AutoscheduleWeightName.SOFT_UNIFORM_SHIFT_SIZE, 1.0)),
        )
            .let { WeightedSumNspConstraint(it) }
    }

    private fun <ID> createHardConstraints(
        team: Team<ID>,
        forecast: Forecast,
        breaksHelper: BreaksHelper,
    ): NspConstraint {
        return listOf(
            NormNspConstraint()
                .weighted(weightsContainer.getOrDefault(AutoscheduleWeightName.HARD_NORM, 1.0)),
            ForbiddenStepsNspConstraint()
                .weighted(weightsContainer.getOrDefault(AutoscheduleWeightName.HARD_FORBIDDEN_STEPS, 1.0)),
            RestBetweenShiftsNspConstraint(team.teamConstraintsInfo)
                .weighted(weightsContainer.getOrDefault(AutoscheduleWeightName.HARD_REST_BETWEEN_SHIFTS, 1.0)),
            ShiftSizeNspConstraint()
                .weighted(weightsContainer.getOrDefault(AutoscheduleWeightName.HARD_SHIFT_SIZE, 1.0)),
            ShiftsPerDayNspConstraint()
                .weighted(weightsContainer.getOrDefault(AutoscheduleWeightName.HARD_SHIFTS_PER_DAY, 1.0)),
            StartStepsNspConstraint()
                .weighted(weightsContainer.getOrDefault(AutoscheduleWeightName.HARD_START_STEPS, 1.0)),
            ShiftIsInPeriodNspConstraint(forecast.size)
                .weighted(weightsContainer.getOrDefault(AutoscheduleWeightName.HARD_SHIFT_IS_IN_PERIOD, 1.0)),
            // breaks
            FirstBreakIsCommonNspConstraint()
                .weighted(weightsContainer.getOrDefault(AutoscheduleWeightName.HARD_FIRST_BREAK_IS_COMMON, 1.0)),
            BreaksAreInShiftNspConstraint(team.breaksConstraintsInfo, breaksHelper)
                .weighted(weightsContainer.getOrDefault(AutoscheduleWeightName.HARD_BREAKS_ARE_IN_SHIFT, 1.0)),
            BreaksCountNspConstraint(team.breaksConstraintsInfo, breaksHelper)
                .weighted(weightsContainer.getOrDefault(AutoscheduleWeightName.HARD_BREAKS_COUNT, 1.0)),
            WorkPeriodsNspConstraint(team.breaksConstraintsInfo, breaksHelper)
                .weighted(weightsContainer.getOrDefault(AutoscheduleWeightName.HARD_WORK_PERIODS, 1.0)),
        )
            .let { WeightedSumNspConstraint(it) }
    }
}
