package com.creative.ludoland

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.creative.ludoland.data.DataStoreManager
import com.creative.ludoland.game.LudoViewModel
import com.creative.ludoland.ui.screens.GameScreen
import com.creative.ludoland.ui.screens.HomeScreen
import com.creative.ludoland.ui.screens.PlayerSetupScreen
import com.creative.ludoland.ui.screens.ResultScreen
import com.creative.ludoland.ui.screens.SettingsScreen
import com.creative.ludoland.ui.screens.SplashScreen
import com.creative.ludoland.ui.theme.LudoLandTheme
import kotlinx.coroutines.launch

enum class Screen {
    SPLASH,
    HOME,
    SETUP,
    GAME,
    RESULT,
    SETTINGS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LudoLandTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LudoLandApp()
                }
            }
        }
    }
}

@Composable
fun LudoLandApp(
    viewModel: LudoViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }

    val gamesPlayed by dataStoreManager.gamesPlayed.collectAsState(initial = 0)
    val winsCount by dataStoreManager.winsCount.collectAsState(initial = 0)

    var currentScreen by remember { mutableStateOf(Screen.SPLASH) }
    var isVsAiMode by remember { mutableStateOf(true) }
    val uiState by viewModel.uiState.collectAsState()

    Crossfade(targetState = currentScreen, label = "screen_transition") { screen: Screen ->
        when (screen) {
            Screen.SPLASH -> SplashScreen(
                onNavigateToHome = {
                    currentScreen = Screen.HOME
                }
            )

            Screen.HOME -> HomeScreen(
                onPlayVsAi = {
                    isVsAiMode = true
                    currentScreen = Screen.SETUP
                },
                onPlayWithFriends = {
                    isVsAiMode = false
                    currentScreen = Screen.SETUP
                },
                onResumeGame = {
                    viewModel.resumeSavedGame()
                    currentScreen = Screen.GAME
                },
                hasSavedGame = uiState.hasSavedGame,
                onNavigateToSettings = {
                    currentScreen = Screen.SETTINGS
                },
                gamesPlayed = gamesPlayed,
                winsCount = winsCount
            )

            Screen.SETUP -> PlayerSetupScreen(
                initialIsVsAiMode = isVsAiMode,
                onStartGame = { configuredPlayers, aiDifficulty ->
                    viewModel.startCustomGame(configuredPlayers, aiDifficulty)
                    scope.launch { dataStoreManager.incrementGamesPlayed() }
                    currentScreen = Screen.GAME
                },
                onBackToHome = {
                    currentScreen = Screen.HOME
                }
            )

            Screen.GAME -> GameScreen(
                uiState = uiState,
                onRollDice = {
                    viewModel.rollDice()
                },
                onTokenClick = { token ->
                    viewModel.moveToken(token)
                },
                onUndo = {
                    viewModel.undo()
                },
                onRestartGame = {
                    viewModel.restartGame()
                },
                onNavigateToHome = {
                    currentScreen = Screen.HOME
                },
                onGameOver = {
                    scope.launch { dataStoreManager.incrementWins() }
                    currentScreen = Screen.RESULT
                }
            )

            Screen.RESULT -> ResultScreen(
                players = uiState.gameState.players,
                winnersOrder = uiState.gameState.winnersOrder,
                onPlayAgain = {
                    viewModel.restartGame()
                    currentScreen = Screen.GAME
                },
                onNavigateToHome = {
                    currentScreen = Screen.HOME
                }
            )

            Screen.SETTINGS -> SettingsScreen(
                soundEnabled = uiState.soundEnabled,
                onToggleSound = { enabled ->
                    viewModel.updateSoundSetting(enabled)
                },
                vibrationEnabled = uiState.vibrationEnabled,
                onToggleVibration = { enabled ->
                    viewModel.updateVibrationSetting(enabled)
                },
                colorBlindMode = uiState.colorBlindMode,
                onToggleColorBlindMode = { enabled ->
                    viewModel.updateColorBlindModeSetting(enabled)
                },
                aiDifficulty = uiState.defaultAiDifficulty,
                onSelectDifficulty = { diff ->
                    viewModel.updateDifficultySetting(diff)
                },
                playerName = uiState.playerName,
                onPlayerNameChange = { name ->
                    viewModel.updatePlayerNameSetting(name)
                },
                onNavigateBack = {
                    currentScreen = Screen.HOME
                }
            )
        }
    }
}
