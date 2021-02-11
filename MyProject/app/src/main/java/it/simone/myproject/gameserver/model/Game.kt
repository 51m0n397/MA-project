package it.simone.myproject.gameserver.model

data class Game (
        val id: String,
        var player1: Player,
        var player2: Player,
        var state: GameState,
        var timestamp: Float
)