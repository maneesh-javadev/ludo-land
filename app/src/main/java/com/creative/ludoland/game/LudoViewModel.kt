package com.creative.ludoland.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.creative.ludoland.data.GameSaveRepository
import com.creative.ludoland.data.SettingsRepository
import com.creative.ludoland.util.HapticManager
import com.creative.ludoland.util.SoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GameUiState(
    val gameState: GameState = GameEngine.createInitialState(),
    val isDiceRolling: Boolean = false,
    val showNoMovesToast: Boolean = false,
    val toastMessage: String = "",
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val colorBlindMode: Boolean = false,
    val defaultAiDifficulty: AiDifficulty = AiDifficulty.MEDIUM,
    val playerName: String = "YOU",
    val hasSavedGame: Boolean = false
)

class LudoViewModel(application: Application) : AndroidViewModel(application) {

    private val engine = GameEngine()
    private val settingsRepo = SettingsRepository(application)
    private val gameSaveRepo = GameSaveRepository(application)
    private val soundManager = SoundManager(application)
    private val hapticManager = HapticManager(application)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var aiDifficulty: AiDifficulty = AiDifficulty.MEDIUM

    init {
        viewModelScope.launch {
            launch {
                settingsRepo.soundEnabled.collect { enabled ->
                    _uiState.update { it.copy(soundEnabled = enabled) }
                    soundManager.isMuted = !enabled
                }
            }
            launch {
                settingsRepo.vibrationEnabled.collect { enabled ->
                    _uiState.update { it.copy(vibrationEnabled = enabled) }
                }
            }
            launch {
                settingsRepo.colorBlindMode.collect { enabled ->
                    _uiState.update { it.copy(colorBlindMode = enabled) }
                }
            }
            launch {
                settingsRepo.defaultAiDifficulty.collect { difficulty ->
                    aiDifficulty = difficulty
                    _uiState.update { it.copy(defaultAiDifficulty = difficulty) }
                }
            }
            launch {
                settingsRepo.playerName.collect { name ->
                    _uiState.update { it.copy(playerName = name) }
                }
            }
            launch {
                gameSaveRepo.savedGameState.collect { savedState ->
                    _uiState.update { it.copy(hasSavedGame = savedState != null) }
                }
            }
        }
        restartGame()
    }

    fun startCustomGame(players: List<Player>, difficulty: AiDifficulty = AiDifficulty.MEDIUM) {
        aiDifficulty = difficulty
        val initialState = GameState(
            players = players,
            currentPlayerIndex = 0,
            diceValue = null,
            consecutiveSixes = 0,
            phase = GamePhase.WAITING_ROLL,
            winnersOrder = emptyList(),
            lastEvent = "${players.first().name}'s turn! Tap dice to roll."
        )

        _uiState.update { it.copy(gameState = initialState) }
        saveCurrentGame(initialState)
    }

    fun restartGame() {
        val name = _uiState.value.playerName.ifEmpty { "YOU" }
        val defaultPlayers = listOf(
            Player(PlayerColor.BLUE, "BLUE", PlayerType.HUMAN),
            Player(PlayerColor.RED, name, PlayerType.HUMAN),
            Player(PlayerColor.GREEN, "AI", PlayerType.AI),
            Player(PlayerColor.YELLOW, "FRIEND", PlayerType.HUMAN)
        )

        startCustomGame(defaultPlayers, _uiState.value.defaultAiDifficulty)
    }

    fun resumeSavedGame() {
        viewModelScope.launch {
            val savedState = gameSaveRepo.savedGameState.first()
            if (savedState != null) {
                _uiState.update { it.copy(gameState = savedState) }
            }
        }
    }

    fun rollDice() {
        val state = _uiState.value.gameState
        if (state.phase != GamePhase.WAITING_ROLL || _uiState.value.isDiceRolling) return

        viewModelScope.launch {
            try {
                soundManager.playDiceRollSound()
                hapticManager.vibrateDiceRoll(_uiState.value.vibrationEnabled)
            } catch (_: Exception) {}

            _uiState.update { it.copy(isDiceRolling = true) }
            delay(500)

            val nextState = GameEngine.rollDice(state)
            val legalMoves = GameEngine.getLegalMoves(nextState)

            _uiState.update {
                it.copy(
                    gameState = nextState,
                    isDiceRolling = false
                )
            }
            saveCurrentGame(nextState)

            if (nextState.phase == GamePhase.WAITING_MOVE && legalMoves.size == 1) {
                delay(600)
                moveToken(legalMoves.first())
            } else if (nextState.phase == GamePhase.WAITING_ROLL && nextState.currentPlayerIndex != state.currentPlayerIndex && nextState.diceValue != null) {
                _uiState.update {
                    it.copy(
                        showNoMovesToast = true,
                        toastMessage = "${state.currentPlayer.name}: No moves available!"
                    )
                }
                delay(1500)
                _uiState.update { it.copy(showNoMovesToast = false) }

                if (isAiTurn(nextState)) {
                    delay(800)
                    rollDice()
                }
            } else if (isAiTurn(nextState)) {
                if (nextState.phase == GamePhase.WAITING_ROLL) {
                    delay(800)
                    rollDice()
                } else if (nextState.phase == GamePhase.WAITING_MOVE) {
                    delay(600)
                    makeAiMove(nextState)
                }
            }
        }
    }

    fun moveToken(token: Token) {
        val state = _uiState.value.gameState
        if (state.phase != GamePhase.WAITING_MOVE) return

        viewModelScope.launch {
            try {
                soundManager.playMoveSound()
            } catch (_: Exception) {}

            val nextState = GameEngine.applyMove(state, token)
            _uiState.update { it.copy(gameState = nextState) }
            saveCurrentGame(nextState)

            if (nextState.phase == GamePhase.GAME_OVER) {
                try {
                    soundManager.playWinSound()
                } catch (_: Exception) {}
                gameSaveRepo.clearSavedGame()
                return@launch
            }

            if (nextState.phase == GamePhase.WAITING_ROLL && isAiTurn(nextState)) {
                delay(800)
                rollDice()
            }
        }
    }

    private fun makeAiMove(state: GameState) {
        val legalMoves = GameEngine.getLegalMoves(state)
        if (legalMoves.isEmpty()) return

        val strategy = getStrategyForDifficulty(aiDifficulty)
        val bestMove = strategy.chooseMove(state, legalMoves)

        moveToken(bestMove)
    }

    private fun isAiTurn(state: GameState): Boolean {
        if (state.phase == GamePhase.GAME_OVER) return false
        return state.currentPlayer.type == PlayerType.AI
    }

    fun undo() {
        val prev = engine.undo()
        if (prev != null) {
            _uiState.update { it.copy(gameState = prev) }
            saveCurrentGame(prev)
        }
    }

    fun updateSoundSetting(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setSoundEnabled(enabled) }
    }

    fun updateVibrationSetting(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setVibrationEnabled(enabled) }
    }

    fun updateColorBlindModeSetting(enabled: Boolean) {
        viewModelScope.launch { settingsRepo.setColorBlindMode(enabled) }
    }

    fun updateDifficultySetting(difficulty: AiDifficulty) {
        viewModelScope.launch { settingsRepo.setDefaultAiDifficulty(difficulty) }
    }

    fun updatePlayerNameSetting(name: String) {
        viewModelScope.launch { settingsRepo.setPlayerName(name) }
    }

    private fun saveCurrentGame(state: GameState) {
        viewModelScope.launch {
            gameSaveRepo.saveGameState(state)
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }
}
