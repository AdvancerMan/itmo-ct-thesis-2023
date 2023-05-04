package ru.itmo.kazakov.autoschedule.nsp.model.team

import ru.itmo.kazakov.autoschedule.nsp.model.team.breaks.BreaksConstraintsInfo

interface Team<ID> {

    val members: List<ShiftAssignee<ID>>

    val teamConstraintsInfo: TeamConstraintsInfo

    val breaksConstraintsInfo: BreaksConstraintsInfo
}
