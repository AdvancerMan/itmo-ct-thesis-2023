package ru.itmo.kazakov.autoschedule.nsp.model.team.breaks

class DataBreaksConstraintsInfo(
    val minWorkStepsNoBreaks: Int,
    val maxWorkStepsNoBreaks: Int,
    val commonBreakSteps: Int,
    val lunchBreakSteps: Int,
    private val commonBreaksForShiftSize: IntArray,
    private val lunchBreaksForShiftSize: IntArray,
) : BreaksConstraintsInfo {

    fun getCommonBreaksForShiftSize(shiftSize: Int): Int {
        return commonBreaksForShiftSize[shiftSize.coerceIn(commonBreaksForShiftSize.indices)]
    }

    fun getLunchBreaksForShiftSize(shiftSize: Int): Int {
        return lunchBreaksForShiftSize[shiftSize.coerceIn(lunchBreaksForShiftSize.indices)]
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataBreaksConstraintsInfo

        if (minWorkStepsNoBreaks != other.minWorkStepsNoBreaks) return false
        if (maxWorkStepsNoBreaks != other.maxWorkStepsNoBreaks) return false
        if (commonBreakSteps != other.commonBreakSteps) return false
        if (lunchBreakSteps != other.lunchBreakSteps) return false
        if (!commonBreaksForShiftSize.contentEquals(other.commonBreaksForShiftSize)) return false
        if (!lunchBreaksForShiftSize.contentEquals(other.lunchBreaksForShiftSize)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = minWorkStepsNoBreaks
        result = 31 * result + maxWorkStepsNoBreaks
        result = 31 * result + commonBreakSteps
        result = 31 * result + lunchBreakSteps
        result = 31 * result + commonBreaksForShiftSize.contentHashCode()
        result = 31 * result + lunchBreaksForShiftSize.contentHashCode()
        return result
    }

    override fun toString(): String {
        return "DataBreaksConstraintsInfo(" +
            "minWorkStepsNoBreaks=$minWorkStepsNoBreaks, " +
            "maxWorkStepsNoBreaks=$maxWorkStepsNoBreaks, " +
            "commonBreakSteps=$commonBreakSteps, " +
            "lunchBreakSteps=$lunchBreakSteps, " +
            "commonBreaksForShiftSize=${commonBreaksForShiftSize.contentToString()}, " +
            "lunchBreaksForShiftSize=${lunchBreaksForShiftSize.contentToString()}" +
            ")"
    }
}
