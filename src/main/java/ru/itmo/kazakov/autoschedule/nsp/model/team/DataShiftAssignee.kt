package ru.itmo.kazakov.autoschedule.nsp.model.team

import ru.itmo.kazakov.autoschedule.helper.NspHelper

data class DataShiftAssignee<ID>(
    override val performance: Double,
    override val stepsNorm: Int,
    override val minStepsShift: Int,
    override val maxStepsShift: Int,
    override val identifier: ID,
    private val absenceStepIndices: List<IntRange>,
    private val workPlan: WorkPlan,
    private val shiftStartStepIndicesInDay: Set<Int>,
    private val nightStepIndicesInDay: List<IntRange>,
    private val dayStartOffsetSteps: Int,
    private val teamConstraintsInfo: TeamConstraintsInfo,
) : ShiftAssignee<ID> {

    init {
        require(dayStartOffsetSteps in 0 until teamConstraintsInfo.stepsInDay) {
            "dayStartOffsetSteps !in 0 until stepsInDay " +
                "($dayStartOffsetSteps !in 0 until ${teamConstraintsInfo.stepsInDay}) " +
                "for assignee $identifier"
        }
        require(minStepsShift > 0) {
            "minStepsShift <= 0 ($minStepsShift <= 0) " +
                "for assignee $identifier"
        }
    }

    override fun getDayIdByStepIndex(index: Int): Int {
        val shiftedIndex = if (dayStartOffsetSteps == 0) {
            index
        } else {
            index - dayStartOffsetSteps + teamConstraintsInfo.stepsInDay
        }

        val negativeIndexAddition = if (shiftedIndex < 0) -1 else 0
        return shiftedIndex / teamConstraintsInfo.stepsInDay + negativeIndexAddition
    }

    private fun getStepOfDayByStepIndex(index: Int): Int {
        return NspHelper.modulo(index - dayStartOffsetSteps, teamConstraintsInfo.stepsInDay)
    }

    private inline fun countSplittingByDay(
        indices: IntRange,
        count: (dayId: Int, steps: IntRange) -> Int,
    ): Int {
        require(NspHelper.rangeSize(indices) <= teamConstraintsInfo.stepsInDay)

        val firstDayId = getDayIdByStepIndex(indices.first)
        val lastDayId = getDayIdByStepIndex(indices.last)

        if (firstDayId == lastDayId) {
            return count(firstDayId, indices)
        }

        val firstStepOfDay = getStepOfDayByStepIndex(indices.first)
        val firstRangeSize = teamConstraintsInfo.stepsInDay - firstStepOfDay

        val firstDayCount = count(firstDayId, indices.first until indices.first + firstRangeSize)
        val lastDayCount = count(lastDayId, indices.first + firstRangeSize..indices.last)

        return firstDayCount + lastDayCount
    }

    private fun countOneDayNightStepIndices(steps: IntRange): Int {
        val stepsOfDay = getStepOfDayByStepIndex(steps.first)..getStepOfDayByStepIndex(steps.last)
        return nightStepIndicesInDay.sumOf {
            NspHelper.intersectionSize(stepsOfDay, it)
        }
    }

    override fun countNightStepIndices(indices: IntRange): Int {
        return countSplittingByDay(indices) { _, steps -> countOneDayNightStepIndices(steps) }
    }

    private fun countOneDayForbiddenStepIndices(steps: IntRange): Int {
        val forbiddenIntersections: MutableList<IntRange> = ArrayList(2)
        for (i in absenceStepIndices.indices) {
            val stepsIntersection = NspHelper.intersectRanges(absenceStepIndices[i], steps)
            if (!stepsIntersection.isEmpty()) {
                forbiddenIntersections.add(stepsIntersection)
            }
        }

        return when (forbiddenIntersections.size) {
            0 -> 0
            1 -> NspHelper.rangeSize(forbiddenIntersections[0])
            2 -> NspHelper.rangeSize(forbiddenIntersections[0])
                .plus(NspHelper.rangeSize(forbiddenIntersections[1]))
                .minus(NspHelper.intersectionSize(forbiddenIntersections[0], forbiddenIntersections[1]))
            else -> forbiddenIntersections.flatten().toSet().size
        }
    }

    override fun countForbiddenStepIndices(indices: IntRange): Int {
        return countSplittingByDay(indices) { _, steps -> countOneDayForbiddenStepIndices(steps) }
    }

    private fun isWorkDay(dayId: Int): Boolean {
        val dayOfWorkPlan = NspHelper.modulo(dayId - workPlan.daysOffset, workPlan.workDays + workPlan.daysOff)
        return dayOfWorkPlan < workPlan.workDays
    }

    override fun isShiftStartStepIndex(index: Int): Boolean {
        val dayId = getDayIdByStepIndex(index)
        if (!isWorkDay(dayId)) {
            return false
        }

        val stepOfDay = getStepOfDayByStepIndex(index)
        if (stepOfDay !in shiftStartStepIndicesInDay) {
            return false
        }

        for (i in nightStepIndicesInDay.indices) {
            if (stepOfDay in nightStepIndicesInDay[i]) {
                return false
            }
        }

        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataShiftAssignee<*>

        if (identifier != other.identifier) return false

        return true
    }

    override fun hashCode(): Int {
        return identifier?.hashCode() ?: 0
    }
}
