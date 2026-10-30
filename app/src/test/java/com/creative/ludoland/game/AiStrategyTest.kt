package com.creative.ludoland.game

import org.junit.Assert.assertEquals
import org.junit.Test

class AiStrategyTest {

    @Test
    fun testMediumAiPicksCaptureWhenAvailable() {
        val mediumAi = MediumAi()

        // Red AI player
        val redPlayer = Player(
            color = PlayerColor.RED,
            name = "Red AI",
            type = PlayerType.AI,
            tokens = listOf(
                Token(id = 0, color = PlayerColor.RED, position = 4),  // Move to 10 -> Abs cell 11 (Captures Yellow)
                Token(id = 1, color = PlayerColor.RED, position = 15)  // Move to 21 -> Abs cell 22 (No capture)
            )
        )

        // Yellow Opponent token at Abs cell 11
        val yellowPlayer = Player(
            color = PlayerColor.YELLOW,
            name = "Yellow",
            tokens = listOf(
                Token(id = 0, color = PlayerColor.YELLOW, position = 49) // (14 + 49) % 52 = 11 (Non-safe cell)
            )
        )

        val state = GameState(
            players = listOf(redPlayer, yellowPlayer),
            currentPlayerIndex = 0,
            diceValue = 6,
            phase = GamePhase.WAITING_MOVE
        )

        val legalMoves = GameEngine.getLegalMoves(state)
        assertEquals(2, legalMoves.size)

        val chosenMove = mediumAi.chooseMove(state, legalMoves)
        assertEquals("Medium AI picks token 0 to execute capture", 0, chosenMove.id)
    }

    @Test
    fun testHardAiAvoidsDangerAndPrefersSafeCell() {
        val hardAi = HardAi()

        // Red AI player
        val redPlayer = Player(
            color = PlayerColor.RED,
            name = "Red AI",
            type = PlayerType.AI,
            tokens = listOf(
                Token(id = 0, color = PlayerColor.RED, position = 10), // Moves to 13 -> Abs cell 14 (Safe Cell!)
                Token(id = 1, color = PlayerColor.RED, position = 2)  // Moves to 5 -> Abs cell 6 (Threatened by Yellow at abs 3)
            )
        )

        // Yellow Opponent token at Abs cell 3 (in range to attack cell 6)
        val yellowPlayer = Player(
            color = PlayerColor.YELLOW,
            name = "Yellow",
            tokens = listOf(
                Token(0, PlayerColor.YELLOW, 41) // (14 + 41) % 52 = 3
            )
        )

        val state = GameState(
            players = listOf(redPlayer, yellowPlayer),
            currentPlayerIndex = 0,
            diceValue = 3,
            phase = GamePhase.WAITING_MOVE
        )

        val legalMoves = GameEngine.getLegalMoves(state)
        val chosenMove = hardAi.chooseMove(state, legalMoves)

        assertEquals("Hard AI prefers safe cell move to avoid threat", 0, chosenMove.id)
    }
}
