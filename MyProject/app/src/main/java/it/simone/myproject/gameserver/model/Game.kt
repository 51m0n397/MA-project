package it.simone.myproject.gameserver.model

data class Game (
        val id: String,
        val player1: Player,
        val player2: Player,
        val state: GameState,
        val timestamp: Float
)