package com.creative.ludoland.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creative.ludoland.ui.theme.LudoBlue
import com.creative.ludoland.ui.theme.LudoGreen
import com.creative.ludoland.ui.theme.LudoRed
import com.creative.ludoland.ui.theme.LudoYellow

@Composable
fun LudoLandLogo(
    modifier: Modifier = Modifier
) {
    val word1 = listOf(
        Pair("L", LudoRed),
        Pair("U", LudoYellow),
        Pair("D", LudoGreen),
        Pair("O", LudoBlue)
    )

    val word2 = listOf(
        Pair("L", LudoRed),
        Pair("A", LudoYellow),
        Pair("N", LudoGreen),
        Pair("D", LudoBlue)
    )

    Row(
        modifier = modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Word 1: LUDO
        Row {
            word1.forEach { (char, color) ->
                Text(
                    text = char,
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = color,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.4f),
                            offset = Offset(2f, 3f),
                            blurRadius = 4f
                        )
                    )
                )
            }
        }

        Text(
            text = " ",
            fontSize = 28.sp
        )

        // Word 2: LAND
        Row {
            word2.forEach { (char, color) ->
                Text(
                    text = char,
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = color,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.4f),
                            offset = Offset(2f, 3f),
                            blurRadius = 4f
                        )
                    )
                )
            }
        }
    }
}
