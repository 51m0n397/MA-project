package it.simone.myproject.gameserver.model

import com.google.gson.annotations.SerializedName

enum class PlayerState {
    @SerializedName("0")
    ALIVE,
    @SerializedName("1")
    DEAD
}