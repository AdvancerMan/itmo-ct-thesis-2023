package ru.itmo.kazakov.autoschedule.nsp.solver

import ru.itmo.kazakov.autoschedule.nsp.model.ScheduleIndividual
import ru.itmo.kazakov.autoschedule.nsp.model.forecast.Forecast
import ru.itmo.kazakov.autoschedule.nsp.model.team.Team

interface NspSolver {

    fun <ID> solve(
        forecast: Forecast,
        team: Team<ID>,
        solverSettings: SolverSettings,
    ): ScheduleIndividual<ID>
}
