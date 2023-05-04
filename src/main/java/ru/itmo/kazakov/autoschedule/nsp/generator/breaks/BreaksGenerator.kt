package ru.itmo.kazakov.autoschedule.nsp.generator.breaks

import ru.itmo.kazakov.autoschedule.nsp.model.schedule.Break
import ru.itmo.kazakov.autoschedule.nsp.model.schedule.Shift

interface BreaksGenerator {
    fun generateSortedBreaks(shift: Shift): List<Break>
}
