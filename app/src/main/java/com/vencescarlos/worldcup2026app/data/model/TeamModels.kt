package com.vencescarlos.worldcup2026app.data.model

import com.google.gson.annotations.SerializedName

data class TeamsResponse(
    @SerializedName("response")
    val response: List<TeamItem> = emptyList()
)

data class TeamItem(
    @SerializedName("team")
    val team: TeamInfo? = null,

    @SerializedName("venue")
    val venue: VenueInfo? = null
)

data class TeamInfo(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("name")
    val name: String = "",

    @SerializedName("code")
    val code: String? = null,

    @SerializedName("country")
    val country: String? = null,

    @SerializedName("founded")
    val founded: Int? = null,

    @SerializedName("national")
    val national: Boolean? = null,

    @SerializedName("logo")
    val logo: String? = null
)

data class VenueInfo(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("address")
    val address: String? = null,

    @SerializedName("city")
    val city: String? = null,

    @SerializedName("capacity")
    val capacity: Int? = null,

    @SerializedName("surface")
    val surface: String? = null,

    @SerializedName("image")
    val image: String? = null
)