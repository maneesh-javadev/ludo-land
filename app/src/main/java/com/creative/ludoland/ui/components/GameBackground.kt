package com.creative.ludoland.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.creative.ludoland.ui.theme.GradientBottom
import com.creative.ludoland.ui.theme.GradientTop
import com.creative.ludoland.ui.theme.LudoRed
import com.creative.ludoland.ui.theme.LudoYellow
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GameBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "background_animations")

    // Sun Rotation
    val sunRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sun_rotation"
    )

    // Cloud floating sway
    val cloudOffset by infiniteTransition.animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cloud_offset"
    )

    // Confetti floating float
    val confettiFloat by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confetti_float"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(GradientTop, GradientBottom)
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Draw Sun (top right)
            val sunCenter = Offset(width * 0.85f, height * 0.12f)
            val sunRadius = width * 0.10f

            // Sun Rays
            rotate(degrees = sunRotation, pivot = sunCenter) {
                val numRays = 8
                for (i in 0 until numRays) {
                    val angle = (i * (360f / numRays)) * (Math.PI / 180f)
                    val rayStart = Offset(
                        sunCenter.x + (sunRadius + 10f) * cos(angle).toFloat(),
                        sunCenter.y + (sunRadius + 10f) * sin(angle).toFloat()
                    )
                    val rayEnd = Offset(
                        sunCenter.x + (sunRadius + 30f) * cos(angle).toFloat(),
                        sunCenter.y + (sunRadius + 30f) * sin(angle).toFloat()
                    )
                    drawLine(
                        color = LudoYellow.copy(alpha = 0.85f),
                        start = rayStart,
                        end = rayEnd,
                        strokeWidth = 10f
                    )
                }
            }

            // Sun Body
            drawCircle(
                color = LudoYellow,
                radius = sunRadius,
                center = sunCenter
            )

            // Sun Eyes
            drawCircle(
                color = Color.Black,
                radius = sunRadius * 0.12f,
                center = Offset(sunCenter.x - sunRadius * 0.3f, sunCenter.y - sunRadius * 0.1f)
            )
            drawCircle(
                color = Color.Black,
                radius = sunRadius * 0.12f,
                center = Offset(sunCenter.x + sunRadius * 0.3f, sunCenter.y - sunRadius * 0.1f)
            )

            // Sun Smile
            val smilePath = Path().apply {
                arcTo(
                    rect = androidx.compose.ui.geometry.Rect(
                        left = sunCenter.x - sunRadius * 0.4f,
                        top = sunCenter.y - sunRadius * 0.2f,
                        right = sunCenter.x + sunRadius * 0.4f,
                        bottom = sunCenter.y + sunRadius * 0.4f
                    ),
                    startAngleDegrees = 20f,
                    sweepAngleDegrees = 140f,
                    forceMoveTo = false
                )
            }
            drawPath(
                path = smilePath,
                color = Color.Black,
                style = Stroke(width = 6f)
            )

            // 2. Draw Floating Clouds
            val cloud1Center = Offset(width * 0.2f + cloudOffset, height * 0.10f)
            drawCircle(Color.White.copy(alpha = 0.9f), width * 0.08f, cloud1Center)
            drawCircle(Color.White.copy(alpha = 0.9f), width * 0.06f, Offset(cloud1Center.x - width * 0.07f, cloud1Center.y + 10f))
            drawCircle(Color.White.copy(alpha = 0.9f), width * 0.06f, Offset(cloud1Center.x + width * 0.07f, cloud1Center.y + 10f))

            val cloud2Center = Offset(width * 0.6f - cloudOffset * 0.8f, height * 0.18f)
            drawCircle(Color.White.copy(alpha = 0.85f), width * 0.07f, cloud2Center)
            drawCircle(Color.White.copy(alpha = 0.85f), width * 0.05f, Offset(cloud2Center.x - width * 0.06f, cloud2Center.y + 8f))
            drawCircle(Color.White.copy(alpha = 0.85f), width * 0.05f, Offset(cloud2Center.x + width * 0.06f, cloud2Center.y + 8f))

            // 3. Floating Confetti Particles & Stars
            val confettiColors = listOf(
                LudoRed.copy(alpha = 0.8f),
                LudoYellow.copy(alpha = 0.8f),
                Color.White.copy(alpha = 0.8f),
                Color(0xFFE91E63).copy(alpha = 0.8f)
            )

            for (i in 0..12) {
                val seedX = (i * 0.08f) % 1f
                val seedY = ((i * 0.15f) + confettiFloat) % 1f
                val color = confettiColors[i % confettiColors.size]

                val posX = width * seedX
                val posY = height * seedY

                if (i % 2 == 0) {
                    drawCircle(
                        color = color,
                        radius = 8f + (i % 4) * 2f,
                        center = Offset(posX, posY)
                    )
                } else {
                    drawRect(
                        color = color,
                        topLeft = Offset(posX, posY),
                        size = Size(14f, 14f)
                    )
                }
            }
        }

        // Render screen content over background
        content()
    }
}
