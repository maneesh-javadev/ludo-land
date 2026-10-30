package com.creative.ludoland.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.creative.ludoland.game.GameEngine
import com.creative.ludoland.game.GamePhase
import com.creative.ludoland.game.GameState
import com.creative.ludoland.game.GameUiState
import com.creative.ludoland.game.Player
import com.creative.ludoland.game.PlayerColor
import com.creative.ludoland.game.PlayerType
import com.creative.ludoland.game.Token
import com.creative.ludoland.ui.components.ClayButton
import com.creative.ludoland.ui.components.ClayCard
import com.creative.ludoland.ui.components.DiceView
import com.creative.ludoland.ui.components.GameBackground
import com.creative.ludoland.ui.components.LudoBoard
import com.creative.ludoland.ui.components.LudoLandLogo
import com.creative.ludoland.ui.components.PlayerCard
import com.creative.ludoland.ui.theme.LudoBlue
import com.creative.ludoland.ui.theme.LudoGreen
import com.creative.ludoland.ui.theme.LudoRed

@Composable
fun GameScreen(
    uiState: GameUiState,
    onRollDice: () -> Unit,
    onTokenClick: (Token) -> Unit,
    onUndo: () -> Unit,
    onRestartGame: () -> Unit,
    onNavigateToHome: () -> Unit,
    onGameOver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameState = uiState.gameState
    var showQuitDialog by remember { mutableStateOf(false) }

    BackHandler {
        showQuitDialog = true
    }

    LaunchedEffect(gameState.phase) {
        if (gameState.phase == GamePhase.GAME_OVER) {
            onGameOver()
        }
    }

    val movableTokens = if (gameState.phase == GamePhase.WAITING_MOVE) {
        GameEngine.getLegalMoves(gameState)
    } else emptyList()

    val isHumanTurn = gameState.phase == GamePhase.WAITING_ROLL &&
            gameState.currentPlayer.type == PlayerType.HUMAN

    GameBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { showQuitDialog = true },
                    modifier = Modifier.semantics { contentDescription = "Return to Home" }
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home Icon",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    gameState.players.forEach { player ->
                        val isActive = gameState.currentPlayer.color == player.color
                        PlayerCard(
                            player = player,
                            isActive = isActive,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                IconButton(
                    onClick = onRestartGame,
                    modifier = Modifier.semantics { contentDescription = "Restart Game" }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart Icon",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // 2. Logo
            LudoLandLogo()

            // 3. Ludo Board
            LudoBoard(
                gameState = gameState,
                movableTokens = movableTokens,
                colorBlindMode = uiState.colorBlindMode,
                onTokenClick = onTokenClick,
                modifier = Modifier.fillMaxWidth()
            )

            // Toast Bubble
            AnimatedVisibility(
                visible = uiState.showNoMovesToast,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .shadow(8.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFE53935))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = uiState.toastMessage.ifEmpty { "No moves available!" },
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 4. 3D Dice Box
            DiceView(
                diceValue = gameState.diceValue,
                isRolling = uiState.isDiceRolling,
                onClick = onRollDice,
                enabled = isHumanTurn,
                size = 68.dp,
                modifier = Modifier.semantics { contentDescription = "Roll Dice" }
            )

            // 5. Bottom Control Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ClayButton(
                    onClick = onRollDice,
                    text = "🎲 ROLL",
                    color = Color(0xFFE91E63),
                    textColor = Color.White,
                    enabled = isHumanTurn,
                    cornerRadius = 18.dp,
                    modifier = Modifier.semantics { contentDescription = "Roll Dice Button" }
                )

                ClayButton(
                    onClick = onRestartGame,
                    text = "RESTART 🔄",
                    color = LudoBlue,
                    textColor = Color.White,
                    cornerRadius = 20.dp,
                    modifier = Modifier.semantics { contentDescription = "Restart Game Button" }
                )

                ClayButton(
                    onClick = onUndo,
                    text = "↩️ UNDO",
                    color = Color(0xFFFF9800),
                    textColor = Color.White,
                    cornerRadius = 18.dp,
                    modifier = Modifier.semantics { contentDescription = "Undo Move Button" }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
        }

        // Confirm Quit Dialog
        if (showQuitDialog) {
            Dialog(onDismissRequest = { showQuitDialog = false }) {
                ClayCard(
                    backgroundColor = Color.White,
                    borderColor = Color(0xFFD7CCC8),
                    cornerRadius = 24.dp,
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "QUIT GAME?",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF212121)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Your progress will be saved automatically.",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ClayButton(
                                onClick = { showQuitDialog = false },
                                text = "RESUME 🎮",
                                color = LudoGreen,
                                textColor = Color.White,
                                cornerRadius = 18.dp
                            )
                            ClayButton(
                                onClick = {
                                    showQuitDialog = false
                                    onNavigateToHome()
                                },
                                text = "QUIT 🏠",
                                color = LudoRed,
                                textColor = Color.White,
                                cornerRadius = 18.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 700)
@Composable
fun GameScreenPreview() {
    val samplePlayers = listOf(
        Player(PlayerColor.BLUE, "BLUE", PlayerType.HUMAN),
        Player(PlayerColor.RED, "YOU", PlayerType.HUMAN),
        Player(PlayerColor.GREEN, "AI", PlayerType.AI),
        Player(PlayerColor.YELLOW, "FRIEND", PlayerType.HUMAN)
    )

    val sampleState = GameState(
        players = samplePlayers,
        currentPlayerIndex = 1,
        phase = GamePhase.WAITING_ROLL
    )

    GameScreen(
        uiState = GameUiState(gameState = sampleState),
        onRollDice = {},
        onTokenClick = {},
        onUndo = {},
        onRestartGame = {},
        onNavigateToHome = {},
        onGameOver = {}
    )
}
