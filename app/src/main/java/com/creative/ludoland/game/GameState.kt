package com.creative.ludoland.game

import kotlinx.serialization.Serializable

@Serializable
enum class GamePhase {
    WAITING_ROLL,
    WAITING_MOVE,
    GAME_OVER
}

@Serializable
data class GameState(
    val players: List<Player>,
    val currentPlayerIndex: Int = 0,
    val diceValue: Int? = null,
    val consecutiveSixes: Int = 0,
    val phase: GamePhase = GamePhase.WAITING_ROLL,
    val winnersOrder: List<PlayerColor> = emptyList(),
    val lastEvent: String = ""
) {
    val currentPlayer: Player
        get() = players[currentPlayerIndex]
}
