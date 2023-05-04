package ru.itmo.kazakov.autoschedule.nsp.model.team

import ru.itmo.kazakov.autoschedule.nsp.model.team.breaks.BreaksConstraintsInfo
import ru.itmo.kazakov.autoschedule.nsp.model.team.breaks.WithoutBreaksBreaksConstraintsInfo

data class DataTeam<ID>(
    override val members: List<ShiftAssignee<ID>>,
    override val teamConstraintsInfo: TeamConstraintsInfo,
    override val breaksConstraintsInfo: BreaksConstraintsInfo = WithoutBreaksBreaksConstraintsInfo,
) : Team<ID> {
    init {
        members
            .groupBy { it.identifier }
            .filter { it.value.size > 1 }
            .let {
                require(it.isEmpty()) { "Duplicated identifiers detected: $it" }
            }
    }
}
