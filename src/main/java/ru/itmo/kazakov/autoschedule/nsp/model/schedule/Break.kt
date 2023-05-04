package ru.itmo.kazakov.autoschedule.nsp.model.schedule

data class Break(
    val breakStartInShift: Int,
    val breakType: BreakType,
)
