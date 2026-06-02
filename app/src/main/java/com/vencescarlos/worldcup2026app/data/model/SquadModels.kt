package com.vencescarlos.worldcup2026app.data.model

import com.google.gson.annotations.SerializedName

data class SquadResponse(
    @SerializedName("response")
    val response: List<SquadItem> = emptyList()
)

data class SquadItem(
    @SerializedName("team")
    val team: SquadTeamInfo? = null,

    @SerializedName("players")
    val players: List<PlayerInfo> = emptyList()
)

data class SquadTeamInfo(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("name")
    val name: String = "",

    @SerializedName("logo")
    val logo: String? = null
)

data class PlayerInfo(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("name")
    val name: String = "",

    @SerializedName("age")
    val age: Int? = null,

    @SerializedName("number")
    val number: Int? = null,

    @SerializedName("position")
    val position: String? = null,

    @SerializedName("photo")
    val photo: String? = null
)