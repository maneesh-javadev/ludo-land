package com.creative.ludoland.ui.theme

import androidx.compose.ui.graphics.Color
import com.creative.ludoland.game.PlayerColor

val PlayerColor.color: Color
    get() = when (this) {
        PlayerColor.RED -> LudoRed
        PlayerColor.YELLOW -> LudoYellow
        PlayerColor.GREEN -> LudoGreen
        PlayerColor.BLUE -> LudoBlue
    }

val PlayerColor.darkColor: Color
    get() = when (this) {
        PlayerColor.RED -> LudoRedDark
        PlayerColor.YELLOW -> LudoYellowDark
        PlayerColor.GREEN -> LudoGreenDark
        PlayerColor.BLUE -> LudoBlueDark
    }

val PlayerColor.displayName: String
    get() = name.lowercase().replaceFirstChar { it.uppercase() }
