package ru.itmo.kazakov.autoschedule.nsp.model.team

interface ShiftAssignee<ID> {

    val performance: Double

    val stepsNorm: Int

    val minStepsShift: Int

    val maxStepsShift: Int

    val identifier: ID

    fun getDayIdByStepIndex(index: Int): Int

    fun countNightStepIndices(indices: IntRange): Int

    fun countForbiddenStepIndices(indices: IntRange): Int

    fun isShiftStartStepIndex(index: Int): Boolean
}
