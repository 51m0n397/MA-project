package it.simone.myproject.gameserver.model

import com.google.gson.annotations.SerializedName

enum class BonusType {
    @SerializedName("0")
    FAST,
    @SerializedName("1")
    BIG,
    @SerializedName("2")
    FREEZE
}

