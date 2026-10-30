package com.creative.ludoland.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.creative.ludoland.game.PlayerColor
import kotlin.math.cos
import kotlin.math.sin

interface TokenSkin {
    fun DrawScope.drawHeadgear(center: Offset, size: Float, color: Color)
}

object RedPirateSkin : TokenSkin {
    override fun DrawScope.drawHeadgear(center: Offset, size: Float, color: Color) {
        val hatWidth = size * 0.85f
        val hatTop = center.y - size * 0.48f

        val hatPath = Path().apply {
            moveTo(center.x - hatWidth / 2f, center.y - size * 0.22f)
            quadraticTo(center.x, hatTop, center.x + hatWidth / 2f, center.y - size * 0.22f)
            quadraticTo(center.x, center.y - size * 0.15f, center.x - hatWidth / 2f, center.y - size * 0.22f)
            close()
        }
        drawPath(hatPath, Color(0xFF212121))
        drawPath(hatPath, Color.White, style = Stroke(width = 1.5f))
        drawCircle(Color.White, size * 0.08f, Offset(center.x, center.y - size * 0.32f))
    }
}

object YellowCrownSkin : TokenSkin {
    override fun DrawScope.drawHeadgear(center: Offset, size: Float, color: Color) {
        val crownWidth = size * 0.65f
        val crownHeight = size * 0.35f
        val topY = center.y - size * 0.52f
        val baseY = center.y - size * 0.22f

        val crownPath = Path().apply {
            moveTo(center.x - crownWidth / 2f, baseY)
            lineTo(center.x - crownWidth / 2f, topY)
            lineTo(center.x - crownWidth / 4f, baseY - crownHeight * 0.3f)
            lineTo(center.x, topY - crownHeight * 0.1f)
            lineTo(center.x + crownWidth / 4f, baseY - crownHeight * 0.3f)
            lineTo(center.x + crownWidth / 2f, topY)
            lineTo(center.x + crownWidth / 2f, baseY)
            close()
        }
        drawPath(crownPath, Color(0xFFFFD54F))
        drawPath(crownPath, Color(0xFFE65100), style = Stroke(width = 2f))
    }
}

object GreenDinoSkin : TokenSkin {
    override fun DrawScope.drawHeadgear(center: Offset, size: Float, color: Color) {
        val spikeRadius = size * 0.08f
        val topY = center.y - size * 0.32f

        drawCircle(Color(0xFF2E7D32), spikeRadius, Offset(center.x - size * 0.18f, topY - size * 0.02f))
        drawCircle(Color(0xFF2E7D32), spikeRadius * 1.25f, Offset(center.x, topY - size * 0.08f))
        drawCircle(Color(0xFF2E7D32), spikeRadius, Offset(center.x + size * 0.18f, topY - size * 0.02f))
    }
}

object BlueAstronautSkin : TokenSkin {
    override fun DrawScope.drawHeadgear(center: Offset, size: Float, color: Color) {
        val visorRadius = size * 0.38f
        val visorCenter = Offset(center.x, center.y - size * 0.06f)

        drawCircle(Color(0xFFE1F5FE).copy(alpha = 0.55f), visorRadius, visorCenter)
        drawCircle(Color(0xFF0288D1), visorRadius, visorCenter, style = Stroke(width = 2.5f))
        drawCircle(Color.White.copy(alpha = 0.85f), size * 0.08f, Offset(visorCenter.x - size * 0.12f, visorCenter.y - size * 0.12f))
    }
}

fun getSkinForColor(color: PlayerColor): TokenSkin {
    return when (color) {
        PlayerColor.RED -> RedPirateSkin
        PlayerColor.YELLOW -> YellowCrownSkin
        PlayerColor.GREEN -> GreenDinoSkin
        PlayerColor.BLUE -> BlueAstronautSkin
    }
}

fun DrawScope.drawCutePawn(
    center: Offset,
    size: Float,
    color: Color,
    darkColor: Color,
    skin: TokenSkin,
    colorBlindMode: Boolean = false,
    playerColor: PlayerColor = PlayerColor.RED
) {
    val radius = size / 2f

    // 1. Shadow Base
    drawCircle(Color.Black.copy(alpha = 0.25f), radius * 0.95f, Offset(center.x, center.y + radius * 0.15f))

    // 2. Round Colored Base Ring
    drawCircle(darkColor, radius * 0.85f, center)
    drawCircle(color, radius * 0.72f, center)

    // 3. Body / Torso Taper
    val bodyPath = Path().apply {
        moveTo(center.x - radius * 0.45f, center.y + radius * 0.4f)
        lineTo(center.x - radius * 0.25f, center.y - radius * 0.1f)
        lineTo(center.x + radius * 0.25f, center.y - radius * 0.1f)
        lineTo(center.x + radius * 0.45f, center.y + radius * 0.4f)
        close()
    }
    drawPath(bodyPath, color)
    drawPath(bodyPath, darkColor, style = Stroke(width = 2f))

    // 4. Head Sphere
    val headCenter = Offset(center.x, center.y - radius * 0.12f)
    val headRadius = radius * 0.42f
    drawCircle(color, headRadius, headCenter)
    drawCircle(darkColor, headRadius, headCenter, style = Stroke(width = 2f))

    // 5. Cute Face (Eyes + Smile)
    val eyeRadius = radius * 0.06f
    val leftEyeCenter = Offset(headCenter.x - headRadius * 0.35f, headCenter.y - headRadius * 0.1f)
    val rightEyeCenter = Offset(headCenter.x + headRadius * 0.35f, headCenter.y - headRadius * 0.1f)

    drawCircle(Color(0xFF212121), eyeRadius, leftEyeCenter)
    drawCircle(Color(0xFF212121), eyeRadius, rightEyeCenter)
    drawCircle(Color.White, eyeRadius * 0.4f, Offset(leftEyeCenter.x - eyeRadius * 0.3f, leftEyeCenter.y - eyeRadius * 0.3f))
    drawCircle(Color.White, eyeRadius * 0.4f, Offset(rightEyeCenter.x - eyeRadius * 0.3f, rightEyeCenter.y - eyeRadius * 0.3f))

    val smilePath = Path().apply {
        arcTo(
            rect = Rect(
                left = headCenter.x - headRadius * 0.35f,
                top = headCenter.y - headRadius * 0.2f,
                right = headCenter.x + headRadius * 0.35f,
                bottom = headCenter.y + headRadius * 0.35f
            ),
            startAngleDegrees = 20f,
            sweepAngleDegrees = 140f,
            forceMoveTo = false
        )
    }
    drawPath(smilePath, Color(0xFF212121), style = Stroke(width = 2f))

    // 6. Color-Blind Mode Symbol Overlay
    if (colorBlindMode) {
        val symCenter = Offset(center.x, center.y + radius * 0.2f)
        val symSize = radius * 0.22f

        when (playerColor) {
            PlayerColor.RED -> {
                // Star
                val starPath = Path().apply {
                    val numPoints = 5
                    for (i in 0 until numPoints * 2) {
                        val r = if (i % 2 == 0) symSize else symSize * 0.45f
                        val angle = (i * Math.PI / numPoints) - Math.PI / 2
                        val x = symCenter.x + r * cos(angle).toFloat()
                        val y = symCenter.y + r * sin(angle).toFloat()
                        if (i == 0) moveTo(x, y) else lineTo(x, y)
                    }
                    close()
                }
                drawPath(starPath, Color.White)
            }
            PlayerColor.YELLOW -> {
                // Triangle
                val triPath = Path().apply {
                    moveTo(symCenter.x, symCenter.y - symSize)
                    lineTo(symCenter.x + symSize, symCenter.y + symSize)
                    lineTo(symCenter.x - symSize, symCenter.y + symSize)
                    close()
                }
                drawPath(triPath, Color.White)
            }
            PlayerColor.GREEN -> {
                // Circle
                drawCircle(Color.White, symSize, symCenter)
            }
            PlayerColor.BLUE -> {
                // Square
                drawRect(
                    color = Color.White,
                    topLeft = Offset(symCenter.x - symSize, symCenter.y - symSize),
                    size = Size(symSize * 2, symSize * 2)
                )
            }
        }
    }

    // 7. Draw Headgear
    with(skin) {
        drawHeadgear(center, size, color)
    }
}
