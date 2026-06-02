package com.vencescarlos.worldcup2026app.data.api

import com.vencescarlos.worldcup2026app.data.model.SquadResponse
import com.vencescarlos.worldcup2026app.data.model.TeamsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("teams2026.php")
    suspend fun getTeams(
        @Query("league") league: Int = 1,
        @Query("season") season: Int = 2026
    ): TeamsResponse

    @GET("squads.php")
    suspend fun getSquad(
        @Query("team") teamId: Int
    ): SquadResponse
}