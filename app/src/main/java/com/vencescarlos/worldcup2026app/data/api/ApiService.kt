package com.vencescarlos.worldcup2026app.data.api

import com.vencescarlos.worldcup2026app.data.model.SquadResponse
import com.vencescarlos.worldcup2026app.data.model.TeamsResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {

    @GET("teams2026.php")
    suspend fun getTeams(
        @Query("league") league: Int = 1,
        @Query("season") season: Int = 2026
    ): TeamsResponse

    @GET("squads.php")
    suspend fun getTestSquad(
        @Query("team") teamId: Int
    ): SquadResponse

    @GET("players/squads")
    suspend fun getOfficialSquad(
        @Query("team") teamId: Int,
        @Header("x-apisports-key") apiKey: String
    ): SquadResponse
}