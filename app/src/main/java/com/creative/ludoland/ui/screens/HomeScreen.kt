package com.creative.ludoland.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creative.ludoland.ui.components.ClayButton
import com.creative.ludoland.ui.components.ClayCard
import com.creative.ludoland.ui.components.GameBackground
import com.creative.ludoland.ui.components.LudoLandLogo
import com.creative.ludoland.ui.theme.LudoBlue
import com.creative.ludoland.ui.theme.LudoGreen
import com.creative.ludoland.ui.theme.LudoRed
import com.creative.ludoland.ui.theme.LudoYellow

@Composable
fun HomeScreen(
    onPlayVsAi: () -> Unit,
    onPlayWithFriends: () -> Unit,
    onResumeGame: () -> Unit,
    hasSavedGame: Boolean = false,
    onNavigateToSettings: () -> Unit,
    gamesPlayed: Int = 0,
    winsCount: Int = 0,
    modifier: Modifier = Modifier
) {
    GameBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Title Header Card with Logo
            ClayCard(
                backgroundColor = LudoRed,
                borderColor = Color(0xFFB71C1C),
                cornerRadius = 24.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(12.dp)
                ) {
                    LudoLandLogo()
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Classic Board Adventure",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = LudoYellow
                    )
                }
            }

            // Main Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Resume Game Button (shown if saved game exists)
                if (hasSavedGame) {
                    ClayButton(
                        onClick = onResumeGame,
                        text = "RESUME GAME ⏯️",
                        color = Color(0xFFE91E63),
                        textColor = Color.White,
                        cornerRadius = 24.dp,
                        modifier = Modifier.fillMaxWidth(0.85f)
                    )
                }

                ClayButton(
                    onClick = onPlayVsAi,
                    text = "PLAY VS AI 🤖",
                    color = LudoGreen,
                    textColor = Color.White,
                    cornerRadius = 24.dp,
                    modifier = Modifier.fillMaxWidth(0.85f)
                )

                ClayButton(
                    onClick = onPlayWithFriends,
                    text = "PLAY WITH FRIENDS 👥",
                    color = LudoYellow,
                    textColor = Color(0xFF3E2723),
                    cornerRadius = 24.dp,
                    modifier = Modifier.fillMaxWidth(0.85f)
                )

                ClayButton(
                    onClick = onNavigateToSettings,
                    text = "SETTINGS ⚙️",
                    color = LudoBlue,
                    textColor = Color.White,
                    cornerRadius = 20.dp,
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
            }

            // Stats Footer
            ClayCard(
                backgroundColor = Color.White,
                borderColor = Color(0xFFD7CCC8),
                cornerRadius = 16.dp,
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Played: $gamesPlayed",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF424242)
                    )
                    Text(
                        text = "|",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.LightGray
                    )
                    Text(
                        text = "Wins: $winsCount",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = LudoGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
