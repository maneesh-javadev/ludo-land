package com.creative.ludoland.game

import kotlinx.serialization.Serializable

@Serializable
enum class PlayerType {
    HUMAN,
    AI
}

@Serializable
data class Player(
    val color: PlayerColor,
    val name: String,
    val type: PlayerType = PlayerType.HUMAN,
    val tokens: List<Token> = List(4) { id -> Token(id = id, color = color, position = -1) },
    val rank: Int? = null
)
