package it.simone.myproject.gameserver.model

data class Player (
        val name: String,
        val score: Int,
        val state: PlayerState,
        val bonus: BonusType?
)