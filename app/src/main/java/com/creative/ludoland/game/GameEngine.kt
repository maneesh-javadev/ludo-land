package com.creative.ludoland.game

import kotlin.random.Random

class GameEngine(initialState: GameState? = null) {

    private val historyStack = ArrayDeque<GameState>()

    var currentState: GameState = initialState ?: createInitialState()
        private set

    companion object {
        fun createInitialState(
            colors: List<PlayerColor> = listOf(PlayerColor.RED, PlayerColor.YELLOW, PlayerColor.GREEN, PlayerColor.BLUE)
        ): GameState {
            val players = colors.map { color ->
                Player(
                    color = color,
                    name = color.name,
                    type = PlayerType.HUMAN,
                    tokens = List(4) { id -> Token(id = id, color = color, position = -1) }
                )
            }
            return GameState(
                players = players,
                currentPlayerIndex = 0,
                diceValue = null,
                consecutiveSixes = 0,
                phase = GamePhase.WAITING_ROLL,
                winnersOrder = emptyList(),
                lastEvent = "Game started. ${players.first().name}'s turn."
            )
        }

        fun rollDice(state: GameState, random: Random = Random): GameState {
            if (state.phase != GamePhase.WAITING_ROLL) return state

            val rolledValue = random.nextInt(1, 7)
            val isSix = rolledValue == 6
            val newConsecutiveSixes = if (isSix) state.consecutiveSixes + 1 else 0

            if (newConsecutiveSixes >= 3) {
                return passTurn(
                    state.copy(
                        diceValue = rolledValue,
                        consecutiveSixes = 0,
                        lastEvent = "Three 6s in a row! Turn forfeited for ${state.currentPlayer.name}."
                    )
                )
            }

            val stateWithRoll = state.copy(
                diceValue = rolledValue,
                consecutiveSixes = newConsecutiveSixes,
                phase = GamePhase.WAITING_MOVE,
                lastEvent = "${state.currentPlayer.name} rolled $rolledValue."
            )

            val legalMoves = getLegalMoves(stateWithRoll)

            return if (legalMoves.isEmpty()) {
                passTurn(
                    stateWithRoll.copy(
                        lastEvent = "${state.currentPlayer.name} rolled $rolledValue. No legal moves."
                    )
                )
            } else {
                stateWithRoll
            }
        }

        fun getLegalMoves(state: GameState): List<Token> {
            if (state.phase != GamePhase.WAITING_MOVE || state.diceValue == null) {
                return emptyList()
            }

            val dice = state.diceValue
            val player = state.currentPlayer

            return player.tokens.filter { token ->
                isMoveLegal(token, dice)
            }
        }

        private fun isMoveLegal(token: Token, dice: Int): Boolean {
            return when {
                token.position == -1 -> dice == 6
                token.position in 0..57 -> (token.position + dice) <= 58
                else -> false
            }
        }

        fun applyMove(state: GameState, tokenToMove: Token): GameState {
            if (state.phase != GamePhase.WAITING_MOVE || state.diceValue == null) return state
            val legalMoves = getLegalMoves(state)
            if (tokenToMove !in legalMoves) return state

            val dice = state.diceValue
            val player = state.currentPlayer

            val oldPos = tokenToMove.position
            val newPos = if (oldPos == -1) 0 else oldPos + dice

            val movedToken = tokenToMove.copy(position = newPos)

            // Check captures
            var captureOccurred = false
            val updatedPlayers = state.players.map { p ->
                if (p.color == player.color) {
                    p.copy(tokens = p.tokens.map { if (it.id == movedToken.id) movedToken else it })
                } else {
                    if (newPos in 0..51) {
                        val movedAbsPos = BoardMapping.getAbsolutePosition(player.color, newPos)
                        if (movedAbsPos != null && movedAbsPos !in BoardMapping.SAFE_CELLS) {
                            val updatedTokens = p.tokens.map { enemyToken ->
                                if (enemyToken.position in 0..51) {
                                    val enemyAbsPos = BoardMapping.getAbsolutePosition(enemyToken.color, enemyToken.position)
                                    if (enemyAbsPos == movedAbsPos) {
                                        captureOccurred = true
                                        enemyToken.copy(position = -1)
                                    } else enemyToken
                                } else enemyToken
                            }
                            p.copy(tokens = updatedTokens)
                        } else p
                    } else p
                }
            }

            // Check if player has finished all 4 tokens
            val updatedCurrentPlayer = updatedPlayers.first { it.color == player.color }
            val hasAllFinished = updatedCurrentPlayer.tokens.all { it.position == 58 }

            var winnersOrder = state.winnersOrder
            var newlyRankedPlayers = updatedPlayers

            if (hasAllFinished && player.color !in winnersOrder) {
                winnersOrder = winnersOrder + player.color
                val rank = winnersOrder.size
                newlyRankedPlayers = newlyRankedPlayers.map {
                    if (it.color == player.color) it.copy(rank = rank) else it
                }
            }

            // Check game over
            val activeUnfinished = newlyRankedPlayers.filter { p -> p.tokens.any { it.position < 58 } }
            val isGameOver = activeUnfinished.size <= 1 || winnersOrder.size >= 3 || winnersOrder.size >= (newlyRankedPlayers.size - 1)

            if (isGameOver) {
                val finalWinnersOrder = if (activeUnfinished.size == 1 && activeUnfinished.first().color !in winnersOrder) {
                    winnersOrder + activeUnfinished.first().color
                } else winnersOrder

                val finalPlayers = newlyRankedPlayers.map { p ->
                    val rankIndex = finalWinnersOrder.indexOf(p.color)
                    if (rankIndex != -1) p.copy(rank = rankIndex + 1) else p
                }

                return state.copy(
                    players = finalPlayers,
                    diceValue = null,
                    phase = GamePhase.GAME_OVER,
                    winnersOrder = finalWinnersOrder,
                    lastEvent = "Game Over! ${finalWinnersOrder.firstOrNull()?.name ?: ""} wins!"
                )
            }

            // Determine bonus turn
            val bonusTurn = (dice == 6) || captureOccurred || (newPos == 58)

            return if (bonusTurn) {
                state.copy(
                    players = newlyRankedPlayers,
                    diceValue = null,
                    phase = GamePhase.WAITING_ROLL,
                    winnersOrder = winnersOrder,
                    lastEvent = when {
                        captureOccurred -> "${player.name} captured an opponent token! Bonus turn."
                        newPos == 58 -> "${player.name} reached center! Bonus turn."
                        dice == 6 -> "${player.name} rolled a 6! Extra turn."
                        else -> "${player.name} gets a bonus turn."
                    }
                )
            } else {
                passTurn(
                    state.copy(
                        players = newlyRankedPlayers,
                        winnersOrder = winnersOrder,
                        lastEvent = "${player.name} moved token to $newPos."
                    )
                )
            }
        }

        private fun passTurn(state: GameState): GameState {
            if (state.players.isEmpty()) return state
            var nextIndex = (state.currentPlayerIndex + 1) % state.players.size
            var loopCount = 0
            while (state.players[nextIndex].tokens.all { it.position == 58 } && loopCount < state.players.size) {
                nextIndex = (nextIndex + 1) % state.players.size
                loopCount++
            }

            return state.copy(
                currentPlayerIndex = nextIndex,
                diceValue = null,
                consecutiveSixes = 0,
                phase = GamePhase.WAITING_ROLL
            )
        }
    }

    fun rollDice(random: Random = Random): GameState {
        pushState(currentState)
        val newState = rollDice(currentState, random)
        currentState = newState
        return newState
    }

    fun getLegalMoves(): List<Token> {
        return getLegalMoves(currentState)
    }

    fun applyMove(tokenToMove: Token): GameState {
        pushState(currentState)
        val newState = applyMove(currentState, tokenToMove)
        currentState = newState
        return newState
    }

    private fun pushState(state: GameState) {
        historyStack.addLast(state)
    }

    fun canUndo(): Boolean = historyStack.isNotEmpty()

    fun undo(): GameState? {
        if (historyStack.isEmpty()) return null
        val previousState = historyStack.removeLast()
        currentState = previousState
        return previousState
    }
}
