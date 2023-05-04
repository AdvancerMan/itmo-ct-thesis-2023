package ru.itmo.kazakov.autoschedule.nsp.factory

enum class AutoscheduleWeightName(val propertyName: String) {
    MUTATION_MOVE_SCHEDULE("nsp.weight.mutation.moveScheduleShiftMutationOperator"),
    MUTATION_UNIFORMATION("nsp.weight.mutation.uniformationShiftMutationOperator"),
    MUTATION_FIX_NORM("nsp.weight.mutation.fixNormShiftMutationOperator"),
    MUTATION_ADD_SCHEDULE("nsp.weight.mutation.addScheduleShiftMutationOperator"),
    MUTATION_MOVE_BREAK("nsp.weight.mutation.moveShiftBreakMutationOperator"),

    GENERATOR_UNIFORM("nsp.weight.generator.uniformScheduleIndividualGenerator"),

    SOFT_UNIFORM_SHIFT_SIZE("nsp.weight.constraint.soft.uniformShiftSizeNspConstraint"),

    HARD_NORM("nsp.weight.constraint.hard.normNspConstraint"),
    HARD_FORBIDDEN_STEPS("nsp.weight.constraint.hard.forbiddenStepsNspConstraint"),
    HARD_REST_BETWEEN_SHIFTS("nsp.weight.constraint.hard.restBetweenShiftsNspConstraint"),
    HARD_SHIFT_SIZE("nsp.weight.constraint.hard.shiftSizeNspConstraint"),
    HARD_SHIFTS_PER_DAY("nsp.weight.constraint.hard.shiftsPerDayNspConstraint"),
    HARD_START_STEPS("nsp.weight.constraint.hard.startStepsNspConstraint"),
    HARD_SHIFT_IS_IN_PERIOD("nsp.weight.constraint.hard.shiftIsInPeriodNspConstraint"),
    HARD_FIRST_BREAK_IS_COMMON("nsp.weight.constraint.hard.firstBreakIsCommonNspConstraint"),
    HARD_BREAKS_ARE_IN_SHIFT("nsp.weight.constraint.hard.breaksAreInShiftNspConstraint"),
    HARD_BREAKS_COUNT("nsp.weight.constraint.hard.breaksCountNspConstraint"),
    HARD_WORK_PERIODS("nsp.weight.constraint.hard.workPeriodsNspConstraint"),
}
