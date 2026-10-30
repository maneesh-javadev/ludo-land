package com.creative.ludoland.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creative.ludoland.game.AiDifficulty
import com.creative.ludoland.ui.components.ClayButton
import com.creative.ludoland.ui.components.ClayCard
import com.creative.ludoland.ui.components.GameBackground
import com.creative.ludoland.ui.theme.LudoBlue
import com.creative.ludoland.ui.theme.LudoGreen
import com.creative.ludoland.ui.theme.LudoRed

@Composable
fun SettingsScreen(
    soundEnabled: Boolean,
    onToggleSound: (Boolean) -> Unit,
    vibrationEnabled: Boolean,
    onToggleVibration: (Boolean) -> Unit,
    colorBlindMode: Boolean,
    onToggleColorBlindMode: (Boolean) -> Unit,
    aiDifficulty: AiDifficulty,
    onSelectDifficulty: (AiDifficulty) -> Unit,
    playerName: String,
    onPlayerNameChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    GameBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Title Header
            ClayCard(
                backgroundColor = LudoBlue,
                borderColor = Color(0xFF0D47A1),
                cornerRadius = 24.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "SETTINGS ⚙️",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Player Name Input
            ClayCard(
                backgroundColor = Color.White,
                borderColor = Color(0xFFD7CCC8),
                cornerRadius = 18.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Your Default Name",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF424242)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = playerName,
                        onValueChange = onPlayerNameChange,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sound Toggle
            ClayCard(
                backgroundColor = Color.White,
                borderColor = Color(0xFFD7CCC8),
                cornerRadius = 18.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sound Effects 🔊",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    Switch(checked = soundEnabled, onCheckedChange = onToggleSound)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Vibration Toggle
            ClayCard(
                backgroundColor = Color.White,
                borderColor = Color(0xFFD7CCC8),
                cornerRadius = 18.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Haptic Vibration 📳",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    Switch(checked = vibrationEnabled, onCheckedChange = onToggleVibration)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Color-Blind Mode Toggle
            ClayCard(
                backgroundColor = Color.White,
                borderColor = Color(0xFFD7CCC8),
                cornerRadius = 18.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Color-Blind Mode 👁️",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    Switch(checked = colorBlindMode, onCheckedChange = onToggleColorBlindMode)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // AI Difficulty Selector
            ClayCard(
                backgroundColor = Color.White,
                borderColor = Color(0xFFD7CCC8),
                cornerRadius = 18.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "Default AI Difficulty",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF424242)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AiDifficulty.entries.forEach { diff ->
                            val isSelected = aiDifficulty == diff
                            Box(
                                modifier = Modifier
                                    .shadow(if (isSelected) 4.dp else 1.dp, RoundedCornerShape(14.dp))
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSelected) LudoGreen else Color(0xFFEEEEEE))
                                    .clickable { onSelectDifficulty(diff) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = diff.name,
                                    color = if (isSelected) Color.White else Color(0xFF424242),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            ClayButton(
                onClick = onNavigateBack,
                text = "BACK TO MENU 🏠",
                color = LudoRed,
                textColor = Color.White,
                cornerRadius = 22.dp,
                modifier = Modifier.fillMaxWidth(0.85f)
            )

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
