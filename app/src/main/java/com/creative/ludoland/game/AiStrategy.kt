package com.creative.ludoland.game

import kotlin.random.Random

enum class AiDifficulty {
    EASY,
    MEDIUM,
    HARD
}

interface AiStrategy {
    fun chooseMove(state: GameState, legalMoves: List<Token>): Token
}

class EasyAi(private val random: Random = Random) : AiStrategy {
    override fun chooseMove(state: GameState, legalMoves: List<Token>): Token {
        return legalMoves.random(random)
    }
}

class MediumAi : AiStrategy {
    override fun chooseMove(state: GameState, legalMoves: List<Token>): Token {
        if (legalMoves.isEmpty()) throw IllegalArgumentException("legalMoves cannot be empty")
        val dice = state.diceValue ?: return legalMoves.first()

        // 1. Capture opponent token
        val captureMove = legalMoves.firstOrNull { token ->
            willCapture(state, token, dice)
        }
        if (captureMove != null) return captureMove

        // 2. Finish token (reach 58)
        val finishMove = legalMoves.firstOrNull { token ->
            token.position != -1 && (token.position + dice) == 58
        }
        if (finishMove != null) return finishMove

        // 3. Exit base
        val exitBaseMove = legalMoves.firstOrNull { token -> token.position == -1 }
        if (exitBaseMove != null) return exitBaseMove

        // 4. Move out of danger
        val moveOutOfDanger = legalMoves.firstOrNull { token ->
            isInDanger(state, token)
        }
        if (moveOutOfDanger != null) return moveOutOfDanger

        // 5. Advance furthest token
        return legalMoves.maxByOrNull { it.position } ?: legalMoves.first()
    }

    private fun willCapture(state: GameState, token: Token, dice: Int): Boolean {
        val targetPos = if (token.position == -1) 0 else token.position + dice
        if (targetPos !in 0..51) return false
        val targetAbs = BoardMapping.getAbsolutePosition(token.color, targetPos) ?: return false
        if (targetAbs in BoardMapping.SAFE_CELLS) return false

        return state.players.any { opponent ->
            opponent.color != token.color && opponent.tokens.any { enemyToken ->
                enemyToken.position in 0..51 && BoardMapping.getAbsolutePosition(enemyToken.color, enemyToken.position) == targetAbs
            }
        }
    }

    private fun isInDanger(state: GameState, token: Token): Boolean {
        if (token.position !in 0..51) return false
        val absPos = BoardMapping.getAbsolutePosition(token.color, token.position) ?: return false
        if (absPos in BoardMapping.SAFE_CELLS) return false

        return state.players.any { opponent ->
            opponent.color != token.color && opponent.tokens.any { enemyToken ->
                if (enemyToken.position in 0..51) {
                    val enemyAbs = BoardMapping.getAbsolutePosition(enemyToken.color, enemyToken.position) ?: -1
                    val dist = (absPos - enemyAbs + 52) % 52
                    dist in 1..6
                } else false
            }
        }
    }
}

class HardAi : AiStrategy {
    override fun chooseMove(state: GameState, legalMoves: List<Token>): Token {
        if (legalMoves.isEmpty()) throw IllegalArgumentException("legalMoves cannot be empty")
        val dice = state.diceValue ?: return legalMoves.first()

        return legalMoves.maxByOrNull { token ->
            scoreMove(state, token, dice)
        } ?: legalMoves.first()
    }

    fun scoreMove(state: GameState, token: Token, dice: Int): Int {
        var score = 0
        val oldPos = token.position
        val newPos = if (oldPos == -1) 0 else oldPos + dice

        // Reaching finish
        if (newPos == 58) score += 120

        // Exit base
        if (oldPos == -1 && dice == 6) score += 60

        // Capture value
        if (willCapture(state, token, dice)) score += 100

        // Landing on safe cell
        if (newPos in 0..51) {
            val absPos = BoardMapping.getAbsolutePosition(token.color, newPos) ?: -1
            if (absPos in BoardMapping.SAFE_CELLS) score += 40
        }

        // Danger penalty at new position
        if (newPos in 0..51) {
            val absPos = BoardMapping.getAbsolutePosition(token.color, newPos) ?: -1
            if (absPos !in BoardMapping.SAFE_CELLS && isPositionUnderThreat(state, token.color, absPos)) {
                score -= 30
            }
        }

        // Progress bonus
        score += newPos / 2

        // Keeping tokens together / blocking (+15)
        val teamTokensOnTarget = state.currentPlayer.tokens.count {
            it.id != token.id && it.position == newPos
        }
        score += teamTokensOnTarget * 15

        return score
    }

    private fun willCapture(state: GameState, token: Token, dice: Int): Boolean {
        val targetPos = if (token.position == -1) 0 else token.position + dice
        if (targetPos !in 0..51) return false
        val targetAbs = BoardMapping.getAbsolutePosition(token.color, targetPos) ?: return false
        if (targetAbs in BoardMapping.SAFE_CELLS) return false

        return state.players.any { opponent ->
            opponent.color != token.color && opponent.tokens.any { enemyToken ->
                enemyToken.position in 0..51 && BoardMapping.getAbsolutePosition(enemyToken.color, enemyToken.position) == targetAbs
            }
        }
    }

    private fun isPositionUnderThreat(state: GameState, myColor: PlayerColor, absPos: Int): Boolean {
        return state.players.any { opponent ->
            opponent.color != myColor && opponent.tokens.any { enemyToken ->
                if (enemyToken.position in 0..51) {
                    val enemyAbs = BoardMapping.getAbsolutePosition(enemyToken.color, enemyToken.position) ?: -1
                    val dist = (absPos - enemyAbs + 52) % 52
                    dist in 1..6
                } else false
            }
        }
    }
}

fun getStrategyForDifficulty(difficulty: AiDifficulty): AiStrategy {
    return when (difficulty) {
        AiDifficulty.EASY -> EasyAi()
        AiDifficulty.MEDIUM -> MediumAi()
        AiDifficulty.HARD -> HardAi()
    }
}
