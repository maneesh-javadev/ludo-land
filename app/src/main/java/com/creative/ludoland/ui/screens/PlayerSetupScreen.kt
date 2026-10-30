package com.creative.ludoland.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creative.ludoland.game.AiDifficulty
import com.creative.ludoland.game.Player
import com.creative.ludoland.game.PlayerColor
import com.creative.ludoland.game.PlayerType
import com.creative.ludoland.ui.components.ClayButton
import com.creative.ludoland.ui.components.ClayCard
import com.creative.ludoland.ui.components.GameBackground
import com.creative.ludoland.ui.theme.LudoGreen
import com.creative.ludoland.ui.theme.LudoRed
import com.creative.ludoland.ui.theme.color

@Composable
fun PlayerSetupScreen(
    initialIsVsAiMode: Boolean = true,
    onStartGame: (List<Player>, AiDifficulty) -> Unit,
    onBackToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    var playerCount by remember { mutableIntStateOf(4) }
    var selectedDifficulty by remember { mutableStateOf(AiDifficulty.MEDIUM) }

    val allColors = listOf(PlayerColor.BLUE, PlayerColor.RED, PlayerColor.GREEN, PlayerColor.YELLOW)

    var playerConfigs by remember {
        mutableStateOf(
            allColors.mapIndexed { index, color ->
                val type = if (initialIsVsAiMode && index >= 1) PlayerType.AI else PlayerType.HUMAN
                val defaultName = when {
                    index == 0 -> "YOU"
                    type == PlayerType.AI -> "AI ${index}"
                    else -> "PLAYER ${index + 1}"
                }
                PlayerConfig(color = color, type = type, name = defaultName)
            }
        )
    }

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
                backgroundColor = LudoRed,
                borderColor = Color(0xFFB71C1C),
                cornerRadius = 22.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "PLAYER SETUP 🎮",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Player Count Selector (2, 3, 4)
            ClayCard(
                backgroundColor = Color.White,
                borderColor = Color(0xFFD7CCC8),
                cornerRadius = 18.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = "NUMBER OF PLAYERS",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF424242)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        listOf(2, 3, 4).forEach { count ->
                            SelectableChip(
                                text = "$count PLAYERS",
                                isSelected = playerCount == count,
                                onClick = { playerCount = count }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Player Slot Cards
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                playerConfigs.take(playerCount).forEachIndexed { index, config ->
                    PlayerSlotCard(
                        config = config,
                        onNameChange = { newName ->
                            playerConfigs = playerConfigs.toMutableList().also {
                                it[index] = it[index].copy(name = newName)
                            }
                        },
                        onTypeToggle = {
                            val newType = if (config.type == PlayerType.HUMAN) PlayerType.AI else PlayerType.HUMAN
                            playerConfigs = playerConfigs.toMutableList().also {
                                it[index] = it[index].copy(type = newType)
                            }
                        }
                    )
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
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = "AI DIFFICULTY",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF424242)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AiDifficulty.entries.forEach { difficulty ->
                            SelectableChip(
                                text = difficulty.name,
                                isSelected = selectedDifficulty == difficulty,
                                onClick = { selectedDifficulty = difficulty }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ClayButton(
                    onClick = onBackToHome,
                    text = "BACK 🏠",
                    color = Color.White,
                    textColor = Color(0xFF424242),
                    cornerRadius = 20.dp
                )

                ClayButton(
                    onClick = {
                        val activePlayers = playerConfigs.take(playerCount).map {
                            Player(color = it.color, name = it.name, type = it.type)
                        }
                        onStartGame(activePlayers, selectedDifficulty)
                    },
                    text = "START GAME 🎲",
                    color = LudoGreen,
                    textColor = Color.White,
                    cornerRadius = 20.dp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

private data class PlayerConfig(
    val color: PlayerColor,
    val type: PlayerType,
    val name: String
)

@Composable
private fun PlayerSlotCard(
    config: PlayerConfig,
    onNameChange: (String) -> Unit,
    onTypeToggle: () -> Unit
) {
    ClayCard(
        backgroundColor = Color.White,
        borderColor = config.color.color,
        cornerRadius = 16.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Color Circle
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(config.color.color)
                    .border(2.dp, Color.White, CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Name Input
            OutlinedTextField(
                value = config.name,
                onValueChange = onNameChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Human / AI Toggle Chip
            SelectableChip(
                text = if (config.type == PlayerType.HUMAN) "👤 HUMAN" else "🤖 AI",
                isSelected = true,
                selectedColor = if (config.type == PlayerType.HUMAN) Color(0xFF1E88E5) else Color(0xFFE91E63),
                onClick = onTypeToggle
            )
        }
    }
}

@Composable
private fun SelectableChip(
    text: String,
    isSelected: Boolean,
    selectedColor: Color = LudoGreen,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .shadow(if (isSelected) 4.dp else 1.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) selectedColor else Color(0xFFEEEEEE))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color(0xFF424242),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
