package ru.itmo.kazakov.autoschedule.nsp.model.schedule

import ru.itmo.kazakov.autoschedule.nsp.model.team.ShiftAssignee

data class ShiftAssignment<ID>(
    val shift: Shift,
    val assignee: ShiftAssignee<ID>,
    val sortedBreaks: List<Break>,
)
