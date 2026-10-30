package com.creative.ludoland.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.creative.ludoland.ui.theme.LudoBlue
import com.creative.ludoland.ui.theme.LudoGreen
import com.creative.ludoland.ui.theme.LudoRed
import com.creative.ludoland.ui.theme.LudoYellow

@Composable
fun WinConfettiEffect(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "win_confetti")

    val confettiProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confetti_progress"
    )

    val colors = listOf(LudoRed, LudoYellow, LudoGreen, LudoBlue, Color.White, Color(0xFFE91E63))

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        for (i in 0..40) {
            val seedX = (i * 0.07f + i * 0.03f) % 1f
            val seedY = (i * 0.12f + confettiProgress) % 1f
            val color = colors[i % colors.size]

            val x = w * seedX
            val y = h * seedY

            if (i % 2 == 0) {
                drawCircle(
                    color = color,
                    radius = 10f + (i % 5) * 2f,
                    center = Offset(x, y)
                )
            } else {
                drawRect(
                    color = color,
                    topLeft = Offset(x, y),
                    size = Size(18f, 18f)
                )
            }
        }
    }
}
