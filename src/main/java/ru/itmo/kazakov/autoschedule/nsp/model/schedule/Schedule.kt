package ru.itmo.kazakov.autoschedule.nsp.model.schedule

import ru.itmo.kazakov.autoschedule.nsp.model.schedule.cache.CachedEntity
import ru.itmo.kazakov.autoschedule.nsp.model.team.ShiftAssignee

data class Schedule<ID>(
    val assignments: Map<ShiftAssignee<ID>, AssigneeSchedule<ID>>,
) : CachedEntity()
