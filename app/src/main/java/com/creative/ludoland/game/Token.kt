package com.creative.ludoland.game

import kotlinx.serialization.Serializable

@Serializable
data class Token(
    val id: Int,
    val color: PlayerColor,
    val position: Int = -1
)
