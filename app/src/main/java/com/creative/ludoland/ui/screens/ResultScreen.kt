package com.creative.ludoland.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creative.ludoland.game.Player
import com.creative.ludoland.game.PlayerColor
import com.creative.ludoland.ui.components.ClayButton
import com.creative.ludoland.ui.components.ClayCard
import com.creative.ludoland.ui.components.GameBackground
import com.creative.ludoland.ui.components.WinConfettiEffect
import com.creative.ludoland.ui.theme.LudoGreen
import com.creative.ludoland.ui.theme.LudoYellow
import com.creative.ludoland.ui.theme.color
import com.creative.ludoland.ui.theme.darkColor

@Composable
fun ResultScreen(
    players: List<Player> = emptyList(),
    winnersOrder: List<PlayerColor> = emptyList(),
    onPlayAgain: () -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rankedPlayers = winnersOrder.mapNotNull { color ->
        players.firstOrNull { it.color == color }
    }

    val first = rankedPlayers.getOrNull(0)
    val second = rankedPlayers.getOrNull(1)
    val third = rankedPlayers.getOrNull(2)

    GameBackground(modifier = modifier) {
        // Full screen confetti celebration overlay
        WinConfettiEffect()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "🏆 WINNERS PODIUM 🏆",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = LudoYellow
            )

            // Podium Visualization
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                // 2nd Place Podium
                if (second != null) {
                    PodiumColumn(
                        player = second,
                        placeText = "🥈 2ND",
                        podiumHeight = 110.dp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // 1st Place Podium (Tallest)
                if (first != null) {
                    PodiumColumn(
                        player = first,
                        placeText = "🥇 1ST",
                        podiumHeight = 150.dp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // 3rd Place Podium
                if (third != null) {
                    PodiumColumn(
                        player = third,
                        placeText = "🥉 3RD",
                        podiumHeight = 85.dp
                    )
                }
            }

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ClayButton(
                    onClick = onPlayAgain,
                    text = "PLAY AGAIN 🔄",
                    color = LudoGreen,
                    textColor = Color.White,
                    cornerRadius = 24.dp,
                    modifier = Modifier.fillMaxWidth(0.85f)
                )

                ClayButton(
                    onClick = onNavigateToHome,
                    text = "MAIN MENU 🏠",
                    color = Color.White,
                    textColor = Color(0xFF424242),
                    cornerRadius = 24.dp,
                    modifier = Modifier.fillMaxWidth(0.85f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun PodiumColumn(
    player: Player,
    placeText: String,
    podiumHeight: androidx.compose.ui.unit.Dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Player Avatar
        Box(
            modifier = Modifier
                .size(44.dp)
                .shadow(6.dp, CircleShape)
                .border(3.dp, player.color.color, CircleShape)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                drawCircle(player.color.color, size.width * 0.38f, center)
                drawCircle(player.color.darkColor, size.width * 0.2f, center)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = player.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Podium Pillar
        ClayCard(
            backgroundColor = player.color.color,
            borderColor = player.color.darkColor,
            cornerRadius = 16.dp,
            modifier = Modifier
                .width(80.dp)
                .height(podiumHeight)
        ) {
            Text(
                text = placeText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
