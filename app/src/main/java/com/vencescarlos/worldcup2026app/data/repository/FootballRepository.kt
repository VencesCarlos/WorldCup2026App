package com.vencescarlos.worldcup2026app.data.repository

import com.vencescarlos.worldcup2026app.data.api.ApiKeys
import com.vencescarlos.worldcup2026app.data.api.RetrofitClient
import com.vencescarlos.worldcup2026app.data.model.SquadResponse
import com.vencescarlos.worldcup2026app.data.model.TeamsResponse

class FootballRepository {

    private val testApiService = RetrofitClient.testApiService
    private val officialApiService = RetrofitClient.officialApiService

    suspend fun getTeams(): TeamsResponse {
        return testApiService.getTeams()
    }

    suspend fun getSquad(teamId: Int): SquadResponse {
        return officialApiService.getOfficialSquad(
            teamId = teamId,
            apiKey = ApiKeys.API_SPORTS_KEY
        )
    }

    suspend fun getTestSquad(teamId: Int): SquadResponse {
        return testApiService.getTestSquad(teamId)
    }
}