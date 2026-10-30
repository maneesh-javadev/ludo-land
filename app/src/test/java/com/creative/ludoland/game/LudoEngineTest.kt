package com.creative.ludoland.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class FixedRandom(private val fixedValues: List<Int>) : Random() {
    private var index = 0
    override fun nextBits(bitCount: Int): Int = 0
    override fun nextInt(from: Int, until: Int): Int {
        val value = fixedValues[index % fixedValues.size]
        index++
        return value
    }
}

class LudoEngineTest {

    @Test
    fun testExitBaseOnlyOnSix() {
        val initialState = GameEngine.createInitialState(listOf(PlayerColor.RED, PlayerColor.YELLOW))

        // Roll 5 -> token in base cannot move
        val stateAfterFive = GameEngine.rollDice(initialState, FixedRandom(listOf(5)))
        val legalMovesAfterFive = GameEngine.getLegalMoves(stateAfterFive)
        assertTrue("No legal moves when rolling 5 with all tokens in base", legalMovesAfterFive.isEmpty())

        // Roll 6 -> token in base can move out
        val stateAfterSix = GameEngine.rollDice(initialState, FixedRandom(listOf(6)))
        val legalMovesAfterSix = GameEngine.getLegalMoves(stateAfterSix)
        assertEquals("Token can move out on rolling 6", 4, legalMovesAfterSix.size)

        val moveState = GameEngine.applyMove(stateAfterSix, legalMovesAfterSix.first())
        val movedToken = moveState.players.first().tokens.first { it.id == legalMovesAfterSix.first().id }
        assertEquals("Token moved to relative position 0 on main loop", 0, movedToken.position)
    }

    @Test
    fun testCaptureWorks() {
        // Setup RED token at relative pos 4, YELLOW token at relative pos 49 (both map to absolute cell 11)
        val redPlayer = Player(
            color = PlayerColor.RED,
            name = "Red",
            tokens = listOf(
                Token(0, PlayerColor.RED, 4), // Will move to 10 with roll of 6
                Token(1, PlayerColor.RED, -1),
                Token(2, PlayerColor.RED, -1),
                Token(3, PlayerColor.RED, -1)
            )
        )

        val yellowPlayer = Player(
            color = PlayerColor.YELLOW,
            name = "Yellow",
            tokens = listOf(
                Token(0, PlayerColor.YELLOW, 49), // Abs cell 11: (14 + 49) % 52 = 11 (Non-safe cell)
                Token(1, PlayerColor.YELLOW, -1),
                Token(2, PlayerColor.YELLOW, -1),
                Token(3, PlayerColor.YELLOW, -1)
            )
        )

        val state = GameState(
            players = listOf(redPlayer, yellowPlayer),
            currentPlayerIndex = 0,
            diceValue = 6,
            phase = GamePhase.WAITING_MOVE
        )

        val redTokenToMove = redPlayer.tokens[0]
        val newState = GameEngine.applyMove(state, redTokenToMove)

        // Yellow token should be captured and sent back to base (-1)
        val capturedYellowToken = newState.players[1].tokens[0]
        assertEquals("Captured enemy token returned to base", -1, capturedYellowToken.position)

        // Red player should get a bonus turn (currentPlayerIndex remains 0)
        assertEquals("Red retains turn after capture", 0, newState.currentPlayerIndex)
        assertEquals("Phase reset to WAITING_ROLL for bonus turn", GamePhase.WAITING_ROLL, newState.phase)
    }

    @Test
    fun testSafeCellsPreventCapture() {
        // Safe cell: Yellow start cell (absolute 14)
        // Red relative pos 13 maps to absolute (1 + 13) % 52 = 14 (Yellow start = Safe cell)
        val redPlayer = Player(
            color = PlayerColor.RED,
            name = "Red",
            tokens = listOf(
                Token(0, PlayerColor.RED, 10), // Will move to 13 with roll of 3
                Token(1, PlayerColor.RED, -1),
                Token(2, PlayerColor.RED, -1),
                Token(3, PlayerColor.RED, -1)
            )
        )

        val yellowPlayer = Player(
            color = PlayerColor.YELLOW,
            name = "Yellow",
            tokens = listOf(
                Token(0, PlayerColor.YELLOW, 0), // Rel 0 = Abs 14 (Safe cell)
                Token(1, PlayerColor.YELLOW, -1),
                Token(2, PlayerColor.YELLOW, -1),
                Token(3, PlayerColor.YELLOW, -1)
            )
        )

        val state = GameState(
            players = listOf(redPlayer, yellowPlayer),
            currentPlayerIndex = 0,
            diceValue = 3,
            phase = GamePhase.WAITING_MOVE
        )

        val newState = GameEngine.applyMove(state, redPlayer.tokens[0])

        // Yellow token should NOT be captured because cell 14 is a safe cell
        val yellowToken = newState.players[1].tokens[0]
        assertEquals("Yellow token remains on board at safe cell", 0, yellowToken.position)
    }

    @Test
    fun testExactFinish() {
        val redPlayer = Player(
            color = PlayerColor.RED,
            name = "Red",
            tokens = listOf(
                Token(0, PlayerColor.RED, 56), // Home column, 2 steps from 58
                Token(1, PlayerColor.RED, 58),
                Token(2, PlayerColor.RED, 58),
                Token(3, PlayerColor.RED, 58)
            )
        )

        val state = GameState(
            players = listOf(redPlayer),
            currentPlayerIndex = 0,
            diceValue = 2,
            phase = GamePhase.WAITING_MOVE
        )

        val newState = GameEngine.applyMove(state, redPlayer.tokens[0])

        val finishedToken = newState.players[0].tokens[0]
        assertEquals("Token reaches position 58 (finished)", 58, finishedToken.position)
    }

    @Test
    fun testOvershootIsBlocked() {
        val redPlayer = Player(
            color = PlayerColor.RED,
            name = "Red",
            tokens = listOf(
                Token(0, PlayerColor.RED, 56), // 2 steps from 58
                Token(1, PlayerColor.RED, -1),
                Token(2, PlayerColor.RED, -1),
                Token(3, PlayerColor.RED, -1)
            )
        )

        val state = GameState(
            players = listOf(redPlayer),
            currentPlayerIndex = 0,
            diceValue = 4, // 56 + 4 = 60 > 58 -> Overshoot!
            phase = GamePhase.WAITING_MOVE
        )

        val legalMoves = GameEngine.getLegalMoves(state)
        assertTrue("Overshooting finish is illegal move", legalMoves.isEmpty())
    }

    @Test
    fun testThreeSixesForfeit() {
        val initialState = GameEngine.createInitialState(listOf(PlayerColor.RED, PlayerColor.YELLOW))

        // Roll 1st 6
        val s1 = GameEngine.rollDice(initialState, FixedRandom(listOf(6)))
        assertEquals(1, s1.consecutiveSixes)

        // Make move so phase goes back to WAITING_ROLL
        val s1Moved = GameEngine.applyMove(s1, GameEngine.getLegalMoves(s1).first())
        assertEquals(0, s1Moved.currentPlayerIndex)

        // Roll 2nd 6
        val s2 = GameEngine.rollDice(s1Moved, FixedRandom(listOf(6)))
        assertEquals(2, s2.consecutiveSixes)

        val s2Moved = GameEngine.applyMove(s2, GameEngine.getLegalMoves(s2).first())
        assertEquals(0, s2Moved.currentPlayerIndex)

        // Roll 3rd 6 -> Turn forfeited!
        val s3 = GameEngine.rollDice(s2Moved, FixedRandom(listOf(6)))
        assertEquals("Turn forfeited, passes to Yellow (index 1)", 1, s3.currentPlayerIndex)
        assertEquals("Consecutive sixes reset", 0, s3.consecutiveSixes)
        assertEquals("Phase is WAITING_ROLL for next player", GamePhase.WAITING_ROLL, s3.phase)
    }

    @Test
    fun testBonusTurns() {
        val redPlayer = Player(
            color = PlayerColor.RED,
            name = "Red",
            tokens = listOf(
                Token(0, PlayerColor.RED, 57), // 1 step from 58
                Token(1, PlayerColor.RED, -1),
                Token(2, PlayerColor.RED, -1),
                Token(3, PlayerColor.RED, -1)
            )
        )
        val yellowPlayer = Player(color = PlayerColor.YELLOW, name = "Yellow")

        val state = GameState(
            players = listOf(redPlayer, yellowPlayer),
            currentPlayerIndex = 0,
            diceValue = 1,
            phase = GamePhase.WAITING_MOVE
        )

        val newState = GameEngine.applyMove(state, redPlayer.tokens[0])

        // Reaching center (pos 58) gives a bonus turn
        assertEquals("Bonus turn granted on reaching center", 0, newState.currentPlayerIndex)
        assertEquals(GamePhase.WAITING_ROLL, newState.phase)
    }

    @Test
    fun testRankingOrder() {
        val p1 = Player(PlayerColor.RED, "Red", tokens = listOf(Token(0, PlayerColor.RED, 58), Token(1, PlayerColor.RED, 58), Token(2, PlayerColor.RED, 58), Token(3, PlayerColor.RED, 57)))
        val p2 = Player(PlayerColor.YELLOW, "Yellow", tokens = listOf(Token(0, PlayerColor.YELLOW, 58), Token(1, PlayerColor.YELLOW, 58), Token(2, PlayerColor.YELLOW, 58), Token(3, PlayerColor.YELLOW, 57)))
        val p3 = Player(PlayerColor.GREEN, "Green", tokens = listOf(Token(0, PlayerColor.GREEN, 58), Token(1, PlayerColor.GREEN, 58), Token(2, PlayerColor.GREEN, 58), Token(3, PlayerColor.GREEN, 57)))
        val p4 = Player(PlayerColor.BLUE, "Blue", tokens = List(4) { Token(it, PlayerColor.BLUE, -1) })

        var state = GameState(players = listOf(p1, p2, p3, p4), currentPlayerIndex = 0, diceValue = 1, phase = GamePhase.WAITING_MOVE)

        // P1 finishes
        state = GameEngine.applyMove(state, p1.tokens.last())
        assertEquals(listOf(PlayerColor.RED), state.winnersOrder)

        // P2 finishes
        state = state.copy(currentPlayerIndex = 1, diceValue = 1, phase = GamePhase.WAITING_MOVE)
        state = GameEngine.applyMove(state, state.players[1].tokens.last())
        assertEquals(listOf(PlayerColor.RED, PlayerColor.YELLOW), state.winnersOrder)

        // P3 finishes -> 3 ranks decided -> Game Over
        state = state.copy(currentPlayerIndex = 2, diceValue = 1, phase = GamePhase.WAITING_MOVE)
        state = GameEngine.applyMove(state, state.players[2].tokens.last())

        assertEquals(GamePhase.GAME_OVER, state.phase)
        assertEquals(listOf(PlayerColor.RED, PlayerColor.YELLOW, PlayerColor.GREEN, PlayerColor.BLUE), state.winnersOrder)
        assertEquals(1, state.players.first { it.color == PlayerColor.RED }.rank)
        assertEquals(2, state.players.first { it.color == PlayerColor.YELLOW }.rank)
        assertEquals(3, state.players.first { it.color == PlayerColor.GREEN }.rank)
        assertEquals(4, state.players.first { it.color == PlayerColor.BLUE }.rank)
    }

    @Test
    fun testUndoSupport() {
        val engine = GameEngine()
        val s0 = engine.currentState

        engine.rollDice(FixedRandom(listOf(6)))
        val canUndo = engine.canUndo()
        assertTrue(canUndo)

        val undoneState = engine.undo()
        assertEquals(s0.currentPlayerIndex, undoneState?.currentPlayerIndex)
        assertEquals(s0.phase, undoneState?.phase)
    }
}
