package it.simone.myproject.gameserver.model

import com.google.gson.annotations.SerializedName

enum class GameState {
    @SerializedName("0")
    WAITING,
    @SerializedName("1")
    PLAYING,
    @SerializedName("2")
    OVER
}