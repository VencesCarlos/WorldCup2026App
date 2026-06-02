package com.vencescarlos.worldcup2026app.data.repository

import com.vencescarlos.worldcup2026app.data.api.RetrofitClient
import com.vencescarlos.worldcup2026app.data.model.SquadResponse
import com.vencescarlos.worldcup2026app.data.model.TeamsResponse

class FootballRepository {

    private val apiService = RetrofitClient.apiService

    suspend fun getTeams(): TeamsResponse {
        return apiService.getTeams()
    }

    suspend fun getSquad(teamId: Int): SquadResponse {
        return apiService.getSquad(teamId)
    }
}