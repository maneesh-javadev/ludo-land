package com.creative.ludoland.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.creative.ludoland.game.BoardMapping
import com.creative.ludoland.game.GameState
import com.creative.ludoland.game.PlayerColor
import com.creative.ludoland.game.Token
import com.creative.ludoland.ui.theme.BoardBorder
import com.creative.ludoland.ui.theme.ClayShadow
import com.creative.ludoland.ui.theme.CreamTile
import com.creative.ludoland.ui.theme.LudoBlue
import com.creative.ludoland.ui.theme.LudoBlueDark
import com.creative.ludoland.ui.theme.LudoGreen
import com.creative.ludoland.ui.theme.LudoGreenDark
import com.creative.ludoland.ui.theme.LudoRed
import com.creative.ludoland.ui.theme.LudoRedDark
import com.creative.ludoland.ui.theme.LudoYellow
import com.creative.ludoland.ui.theme.LudoYellowDark
import com.creative.ludoland.ui.theme.color
import com.creative.ludoland.ui.theme.darkColor
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LudoBoard(
    modifier: Modifier = Modifier,
    gameState: GameState? = null,
    movableTokens: List<Token> = emptyList(),
    colorBlindMode: Boolean = false,
    onTokenClick: (Token) -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "token_glow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.30f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "token_pulse"
    )

    val boardShape = RoundedCornerShape(24.dp)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth(0.92f)
            .widthIn(max = 500.dp)
            .aspectRatio(1f)
            .shadow(elevation = 14.dp, shape = boardShape, ambientColor = ClayShadow, spotColor = ClayShadow)
            .border(width = 6.dp, color = BoardBorder, shape = boardShape)
            .clip(boardShape)
            .background(Color(0xFFFFF8E1))
            .semantics { contentDescription = "Ludo Board" }
    ) {
        val boardSizePx = constraints.maxWidth.toFloat()
        val cellSize = boardSizePx / 15f
        val tileSizeDp = with(androidx.compose.ui.platform.LocalDensity.current) { cellSize.toDp() }

        val reusablePath = remember { Path() }

        Canvas(modifier = Modifier.fillMaxSize()) {
            for (index in 0..51) {
                val coord = BoardMapping.MAIN_LOOP_COORDINATES[index]
                val row = coord.first
                val col = coord.second

                val isRedStart = (row == 6 && col == 1)
                val isYellowStart = (row == 1 && col == 8)
                val isBlueStart = (row == 8 && col == 13)
                val isGreenStart = (row == 13 && col == 6)

                val isStart = isRedStart || isYellowStart || isBlueStart || isGreenStart

                val isStar = (row == 2 && col == 6) ||
                        (row == 6 && col == 12) ||
                        (row == 12 && col == 8) ||
                        (row == 8 && col == 2)

                val cellColor = when {
                    isRedStart -> LudoRed
                    isYellowStart -> LudoYellow
                    isBlueStart -> LudoBlue
                    isGreenStart -> LudoGreen
                    else -> CreamTile
                }

                drawRoundedCell(
                    row = row,
                    col = col,
                    cellSize = cellSize,
                    fillColor = cellColor,
                    borderColor = if (isStart) Color.White.copy(alpha = 0.8f) else BoardBorder.copy(alpha = 0.25f),
                    path = reusablePath
                )

                val center = Offset(col * cellSize + cellSize / 2f, row * cellSize + cellSize / 2f)

                if (isRedStart) drawArrow(center, cellSize * 0.45f, 0f, Color.White, reusablePath)
                if (isYellowStart) drawArrow(center, cellSize * 0.45f, 90f, Color.White, reusablePath)
                if (isBlueStart) drawArrow(center, cellSize * 0.45f, 180f, Color.White, reusablePath)
                if (isGreenStart) drawArrow(center, cellSize * 0.45f, 270f, Color.White, reusablePath)

                if (isStar) {
                    drawStar(center, cellSize * 0.32f, Color(0xFFFFB300), reusablePath)
                }
            }

            val homeColumns = mapOf(
                PlayerColor.RED to Pair(BoardMapping.HOME_COLUMN_COORDINATES[PlayerColor.RED], LudoRed),
                PlayerColor.YELLOW to Pair(BoardMapping.HOME_COLUMN_COORDINATES[PlayerColor.YELLOW], LudoYellow),
                PlayerColor.BLUE to Pair(BoardMapping.HOME_COLUMN_COORDINATES[PlayerColor.BLUE], LudoBlue),
                PlayerColor.GREEN to Pair(BoardMapping.HOME_COLUMN_COORDINATES[PlayerColor.GREEN], LudoGreen)
            )

            for ((_, pair) in homeColumns) {
                val coords = pair.first ?: continue
                val color = pair.second
                for (coord in coords) {
                    drawRoundedCell(
                        row = coord.first,
                        col = coord.second,
                        cellSize = cellSize,
                        fillColor = color,
                        borderColor = Color.White.copy(alpha = 0.6f),
                        path = reusablePath
                    )
                }
            }

            drawClayHomeBase(
                row = 0, col = 0, cellSize = cellSize,
                mainColor = LudoRed, darkBorderColor = LudoRedDark,
                slots = BoardMapping.BASE_SLOT_COORDINATES[PlayerColor.RED] ?: emptyList(),
                path = reusablePath
            )
            drawClayHomeBase(
                row = 0, col = 9, cellSize = cellSize,
                mainColor = LudoYellow, darkBorderColor = LudoYellowDark,
                slots = BoardMapping.BASE_SLOT_COORDINATES[PlayerColor.YELLOW] ?: emptyList(),
                path = reusablePath
            )
            drawClayHomeBase(
                row = 9, col = 0, cellSize = cellSize,
                mainColor = LudoGreen, darkBorderColor = LudoGreenDark,
                slots = BoardMapping.BASE_SLOT_COORDINATES[PlayerColor.GREEN] ?: emptyList(),
                path = reusablePath
            )
            drawClayHomeBase(
                row = 9, col = 9, cellSize = cellSize,
                mainColor = LudoBlue, darkBorderColor = LudoBlueDark,
                slots = BoardMapping.BASE_SLOT_COORDINATES[PlayerColor.BLUE] ?: emptyList(),
                path = reusablePath
            )

            drawCenterTriangles(cellSize, reusablePath)
        }

        if (gameState != null) {
            val allTokens = gameState.players.flatMap { it.tokens }

            val tokensOnCell = allTokens.groupBy { token ->
                BoardMapping.getCoordinates(token.color, token.position, token.id)
            }

            for (player in gameState.players) {
                val skin = getSkinForColor(player.color)

                for (token in player.tokens) {
                    val isMovable = token in movableTokens
                    val coord = BoardMapping.getCoordinates(token.color, token.position, token.id)
                    val cellGroup = tokensOnCell[coord] ?: emptyList()
                    val indexInCell = cellGroup.indexOf(token)
                    val totalInCell = cellGroup.size

                    val (offsetX, offsetY) = if (totalInCell > 1 && token.position != -1 && token.position != 58) {
                        val angle = (2 * Math.PI * indexInCell / totalInCell).toFloat()
                        val dist = 0.18f
                        Pair(cos(angle) * dist, sin(angle) * dist)
                    } else {
                        Pair(0f, 0f)
                    }

                    AnimatedTokenPiece(
                        token = token,
                        skin = skin,
                        isMovable = isMovable,
                        pulseScale = if (isMovable) pulseScale else 1.0f,
                        tileSizeDp = tileSizeDp,
                        targetRow = coord.first.toFloat() + offsetY,
                        targetCol = coord.second.toFloat() + offsetX,
                        colorBlindMode = colorBlindMode,
                        onClick = { onTokenClick(token) }
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedTokenPiece(
    token: Token,
    skin: TokenSkin,
    isMovable: Boolean,
    pulseScale: Float,
    tileSizeDp: Dp,
    targetRow: Float,
    targetCol: Float,
    colorBlindMode: Boolean = false,
    onClick: () -> Unit
) {
    val animRow = remember { Animatable(targetRow) }
    val animCol = remember { Animatable(targetCol) }
    val animJumpY = remember { Animatable(0f) }
    val animScaleX = remember { Animatable(1f) }
    val animScaleY = remember { Animatable(1f) }
    val finishScale = remember { Animatable(if (token.position == 58) 0f else 1f) }

    var lastPos by remember { mutableIntStateOf(token.position) }

    LaunchedEffect(token.position, targetRow, targetCol) {
        if (lastPos != token.position) {
            val oldPos = lastPos
            val newPos = token.position
            lastPos = newPos

            when (newPos) {
                -1 -> {
                    coroutineScope {
                        launch { animRow.animateTo(targetRow, tween(500, easing = FastOutSlowInEasing)) }
                        launch { animCol.animateTo(targetCol, tween(500, easing = FastOutSlowInEasing)) }
                    }
                }
                58 -> {
                    coroutineScope {
                        launch { animRow.animateTo(targetRow, tween(300, easing = FastOutSlowInEasing)) }
                        launch { animCol.animateTo(targetCol, tween(300, easing = FastOutSlowInEasing)) }
                        launch { finishScale.animateTo(0f, tween(350, easing = FastOutSlowInEasing)) }
                    }
                }
                else -> {
                    val startStep = if (oldPos == -1) 0 else oldPos + 1
                    for (step in startStep..newPos) {
                        val (r, c) = BoardMapping.getCoordinates(token.color, step, token.id)

                        coroutineScope {
                            launch {
                                animJumpY.animateTo(-16f, tween(60, easing = FastOutSlowInEasing))
                                animJumpY.animateTo(0f, tween(60, easing = FastOutSlowInEasing))
                            }
                            launch {
                                animScaleX.animateTo(1.18f, tween(60))
                                animScaleX.animateTo(1.0f, tween(60))
                            }
                            launch {
                                animScaleY.animateTo(0.82f, tween(60))
                                animScaleY.animateTo(1.0f, tween(60))
                            }
                            launch {
                                animRow.animateTo(r.toFloat(), tween(120, easing = LinearEasing))
                            }
                            launch {
                                animCol.animateTo(c.toFloat(), tween(120, easing = LinearEasing))
                            }
                        }
                    }
                    animRow.snapTo(targetRow)
                    animCol.snapTo(targetCol)
                }
            }
        } else {
            animRow.snapTo(targetRow)
            animCol.snapTo(targetCol)
        }
    }

    val tokenSizeDp = tileSizeDp * 0.82f
    val posX = tileSizeDp * animCol.value + (tileSizeDp - tokenSizeDp) / 2
    val posY = tileSizeDp * animRow.value + (tileSizeDp - tokenSizeDp) / 2 + animJumpY.value.dp

    if (finishScale.value > 0.01f) {
        Box(
            modifier = Modifier
                .offset(x = posX, y = posY)
                .size(tokenSizeDp)
                .graphicsLayer {
                    scaleX = pulseScale * animScaleX.value * finishScale.value
                    scaleY = pulseScale * animScaleY.value * finishScale.value
                }
                .semantics { contentDescription = "${token.color.name} Token ${token.id + 1}" }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = isMovable,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)

                if (isMovable) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.85f),
                        radius = size.width * 0.56f,
                        center = center,
                        style = Stroke(width = 4.5f)
                    )
                    drawCircle(
                        color = token.color.color.copy(alpha = 0.45f),
                        radius = size.width * 0.62f,
                        center = center,
                        style = Stroke(width = 3f)
                    )
                }

                drawCutePawn(
                    center = center,
                    size = size.width,
                    color = token.color.color,
                    darkColor = token.color.darkColor,
                    skin = skin,
                    colorBlindMode = colorBlindMode,
                    playerColor = token.color
                )
            }
        }
    }
}

private fun DrawScope.drawRoundedCell(
    row: Int,
    col: Int,
    cellSize: Float,
    fillColor: Color,
    borderColor: Color,
    path: Path
) {
    val gap = cellSize * 0.05f
    val cornerRadius = CornerRadius(cellSize * 0.20f)
    val topLeft = Offset(col * cellSize + gap, row * cellSize + gap)
    val size = Size(cellSize - gap * 2, cellSize - gap * 2)

    val roundRect = RoundRect(
        left = topLeft.x,
        top = topLeft.y,
        right = topLeft.x + size.width,
        bottom = topLeft.y + size.height,
        cornerRadius = cornerRadius
    )

    path.reset()
    path.addRoundRect(roundRect)

    drawPath(path, fillColor)
    drawPath(path, borderColor, style = Stroke(width = 1.8f))
}

private fun DrawScope.drawClayHomeBase(
    row: Int,
    col: Int,
    cellSize: Float,
    mainColor: Color,
    darkBorderColor: Color,
    slots: List<Pair<Int, Int>>,
    path: Path
) {
    val baseTopLeft = Offset(col * cellSize, row * cellSize)
    val baseSize = Size(6 * cellSize, 6 * cellSize)
    val cornerRadius = CornerRadius(cellSize * 0.45f)

    val outerRect = RoundRect(
        left = baseTopLeft.x,
        top = baseTopLeft.y,
        right = baseTopLeft.x + baseSize.width,
        bottom = baseTopLeft.y + baseSize.height,
        cornerRadius = cornerRadius
    )

    path.reset()
    path.addRoundRect(outerRect)

    drawPath(path, mainColor)
    drawPath(path, darkBorderColor, style = Stroke(width = 4f))

    val padTopLeft = Offset((col + 1) * cellSize, (row + 1) * cellSize)
    val padSize = Size(4 * cellSize, 4 * cellSize)
    val padCornerRadius = CornerRadius(cellSize * 0.35f)

    val padRect = RoundRect(
        left = padTopLeft.x,
        top = padTopLeft.y,
        right = padTopLeft.x + padSize.width,
        bottom = padTopLeft.y + padSize.height,
        cornerRadius = padCornerRadius
    )

    path.reset()
    path.addRoundRect(padRect)

    drawPath(path, Color.White)
    drawPath(path, mainColor.copy(alpha = 0.3f), style = Stroke(width = 3f))

    val slotRadius = cellSize * 0.65f
    for (slot in slots) {
        val slotCenter = Offset(slot.second * cellSize + cellSize / 2f, slot.first * cellSize + cellSize / 2f)

        drawCircle(color = mainColor.copy(alpha = 0.2f), radius = slotRadius, center = slotCenter)
        drawCircle(color = mainColor, radius = slotRadius, center = slotCenter, style = Stroke(width = 3.5f))
    }
}

private fun DrawScope.drawCenterTriangles(cellSize: Float, path: Path) {
    val left = 6 * cellSize
    val top = 6 * cellSize
    val right = 9 * cellSize
    val bottom = 9 * cellSize
    val center = Offset(7.5f * cellSize, 7.5f * cellSize)

    path.reset()
    path.moveTo(left, top)
    path.lineTo(center.x, center.y)
    path.lineTo(left, bottom)
    path.close()
    drawPath(path, LudoRed)

    path.reset()
    path.moveTo(left, top)
    path.lineTo(center.x, center.y)
    path.lineTo(right, top)
    path.close()
    drawPath(path, LudoYellow)

    path.reset()
    path.moveTo(right, top)
    path.lineTo(center.x, center.y)
    path.lineTo(right, bottom)
    path.close()
    drawPath(path, LudoBlue)

    path.reset()
    path.moveTo(left, bottom)
    path.lineTo(center.x, center.y)
    path.lineTo(right, bottom)
    path.close()
    drawPath(path, LudoGreen)

    drawRect(
        color = BoardBorder,
        topLeft = Offset(left, top),
        size = Size(3 * cellSize, 3 * cellSize),
        style = Stroke(width = 3.5f)
    )

    drawArrow(Offset(6.4f * cellSize, 7.5f * cellSize), cellSize * 0.45f, 0f, Color.White, path)
    drawArrow(Offset(7.5f * cellSize, 6.4f * cellSize), cellSize * 0.45f, 90f, Color.White, path)
    drawArrow(Offset(8.6f * cellSize, 7.5f * cellSize), cellSize * 0.45f, 180f, Color.White, path)
    drawArrow(Offset(7.5f * cellSize, 8.6f * cellSize), cellSize * 0.45f, 270f, Color.White, path)
}

private fun DrawScope.drawArrow(center: Offset, size: Float, angleDegrees: Float, color: Color, path: Path) {
    rotate(angleDegrees, pivot = center) {
        val half = size / 2f
        path.reset()
        path.moveTo(center.x - half * 0.6f, center.y - half * 0.7f)
        path.lineTo(center.x + half * 0.8f, center.y)
        path.lineTo(center.x - half * 0.6f, center.y + half * 0.7f)
        path.lineTo(center.x - half * 0.2f, center.y)
        path.close()

        drawPath(path, color)
        drawPath(path, Color.Black.copy(alpha = 0.3f), style = Stroke(width = 2f))
    }
}

private fun DrawScope.drawStar(center: Offset, radius: Float, color: Color, path: Path) {
    path.reset()
    val numPoints = 5
    val outerRadius = radius
    val innerRadius = radius * 0.42f
    for (i in 0 until numPoints * 2) {
        val r = if (i % 2 == 0) outerRadius else innerRadius
        val angle = (i * Math.PI / numPoints) - Math.PI / 2
        val x = center.x + r * cos(angle).toFloat()
        val y = center.y + r * sin(angle).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()

    drawPath(path, color)
    drawPath(path, Color(0xFFE65100), style = Stroke(width = 2.5f))
}

@Preview(showBackground = true, widthDp = 400, heightDp = 600)
@Composable
fun LudoBoardPreview() {
    GameBackground {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LudoBoard(
                modifier = Modifier.fillMaxWidth(0.92f)
            )
        }
    }
}
