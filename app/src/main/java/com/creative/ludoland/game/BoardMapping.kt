package com.creative.ludoland.game

object BoardMapping {

    // Absolute start cell indices on the 52-cell main loop
    val START_INDICES: Map<PlayerColor, Int> = mapOf(
        PlayerColor.RED to 1,
        PlayerColor.YELLOW to 14,
        PlayerColor.BLUE to 27,
        PlayerColor.GREEN to 40
    )

    // Safe cells on absolute loop: 4 start cells + 4 star cells (8 cells after each start)
    val SAFE_CELLS: Set<Int> = setOf(1, 9, 14, 22, 27, 35, 40, 48)

    // 52-cell main loop coordinates (row, col) on a 15x15 grid (clockwise)
    val MAIN_LOOP_COORDINATES: List<Pair<Int, Int>> = listOf(
        Pair(6, 0), Pair(6, 1), Pair(6, 2), Pair(6, 3), Pair(6, 4), Pair(6, 5), // 0..5
        Pair(5, 6), Pair(4, 6), Pair(3, 6), Pair(2, 6), Pair(1, 6), Pair(0, 6), // 6..11
        Pair(0, 7), Pair(0, 8), // 12..13
        Pair(1, 8), Pair(2, 8), Pair(3, 8), Pair(4, 8), Pair(5, 8), // 14..18
        Pair(6, 9), Pair(6, 10), Pair(6, 11), Pair(6, 12), Pair(6, 13), Pair(6, 14), // 19..24
        Pair(7, 14), Pair(8, 14), // 25..26
        Pair(8, 13), Pair(8, 12), Pair(8, 11), Pair(8, 10), Pair(8, 9), // 27..31
        Pair(9, 8), Pair(10, 8), Pair(11, 8), Pair(12, 8), Pair(13, 8), Pair(14, 8), // 32..37
        Pair(14, 7), Pair(14, 6), // 38..39
        Pair(13, 6), Pair(12, 6), Pair(11, 6), Pair(10, 6), Pair(9, 6), // 40..44
        Pair(8, 5), Pair(8, 4), Pair(8, 3), Pair(8, 2), Pair(8, 1), Pair(8, 0), // 45..50
        Pair(7, 0) // 51
    )

    // Home column coordinates for each color (positions 52..57)
    val HOME_COLUMN_COORDINATES: Map<PlayerColor, List<Pair<Int, Int>>> = mapOf(
        PlayerColor.RED to listOf(Pair(7, 1), Pair(7, 2), Pair(7, 3), Pair(7, 4), Pair(7, 5), Pair(7, 6)),
        PlayerColor.YELLOW to listOf(Pair(1, 7), Pair(2, 7), Pair(3, 7), Pair(4, 7), Pair(5, 7), Pair(6, 7)),
        PlayerColor.BLUE to listOf(Pair(7, 13), Pair(7, 12), Pair(7, 11), Pair(7, 10), Pair(7, 9), Pair(7, 8)),
        PlayerColor.GREEN to listOf(Pair(13, 7), Pair(12, 7), Pair(11, 7), Pair(10, 7), Pair(9, 7), Pair(8, 7))
    )

    // Base slot coordinates (position = -1, 4 slots per color)
    val BASE_SLOT_COORDINATES: Map<PlayerColor, List<Pair<Int, Int>>> = mapOf(
        PlayerColor.RED to listOf(Pair(1, 1), Pair(1, 4), Pair(4, 1), Pair(4, 4)),
        PlayerColor.YELLOW to listOf(Pair(1, 10), Pair(1, 13), Pair(4, 10), Pair(4, 13)),
        PlayerColor.BLUE to listOf(Pair(10, 10), Pair(10, 13), Pair(13, 10), Pair(13, 13)),
        PlayerColor.GREEN to listOf(Pair(10, 1), Pair(10, 4), Pair(13, 1), Pair(13, 4))
    )

    fun getAbsolutePosition(color: PlayerColor, relativePos: Int): Int? {
        if (relativePos !in 0..51) return null
        val start = START_INDICES[color] ?: 0
        return (start + relativePos) % 52
    }

    fun getCoordinates(color: PlayerColor, position: Int, tokenId: Int = 0): Pair<Int, Int> {
        return when {
            position == -1 -> BASE_SLOT_COORDINATES[color]?.getOrElse(tokenId % 4) { Pair(0, 0) } ?: Pair(0, 0)
            position in 0..51 -> {
                val absPos = getAbsolutePosition(color, position) ?: 0
                MAIN_LOOP_COORDINATES.getOrElse(absPos) { Pair(0, 0) }
            }
            position in 52..57 -> {
                val index = position - 52
                HOME_COLUMN_COORDINATES[color]?.getOrElse(index) { Pair(7, 7) } ?: Pair(7, 7)
            }
            position >= 58 -> Pair(7, 7)
            else -> Pair(0, 0)
        }
    }
}
