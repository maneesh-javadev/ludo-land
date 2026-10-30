package com.creative.ludoland.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.creative.ludoland.ui.theme.ClayShadow
import com.creative.ludoland.ui.theme.LudoRed

@Composable
fun DiceView(
    diceValue: Int?,
    isRolling: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp = 72.dp
) {
    val rotX by animateFloatAsState(
        targetValue = if (isRolling) 720f else 0f,
        animationSpec = tween(550, easing = FastOutSlowInEasing),
        label = "dice_rot_x"
    )

    val rotY by animateFloatAsState(
        targetValue = if (isRolling) 720f else 0f,
        animationSpec = tween(550, easing = FastOutSlowInEasing),
        label = "dice_rot_y"
    )

    val rotZ by animateFloatAsState(
        targetValue = if (isRolling) 360f else 0f,
        animationSpec = tween(550, easing = FastOutSlowInEasing),
        label = "dice_rot_z"
    )

    val scale by animateFloatAsState(
        targetValue = if (isRolling) 1.25f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
        label = "dice_scale"
    )

    val shape = RoundedCornerShape(18.dp)

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                rotationX = rotX
                rotationY = rotY
                rotationZ = rotZ
                scaleX = scale
                scaleY = scale
                cameraDistance = 12f * density
            }
            .shadow(
                elevation = if (isRolling) 12.dp else 8.dp,
                shape = shape,
                ambientColor = ClayShadow,
                spotColor = ClayShadow
            )
            .border(width = 3.5.dp, color = Color(0xFFD7CCC8), shape = shape)
            .clip(shape)
            .background(Color.White)
            .clickable(enabled = enabled && !isRolling, onClick = onClick)
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        val valToDraw = diceValue ?: 1

        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val dotRadius = w * 0.11f

            val center = Offset(w * 0.5f, h * 0.5f)
            val topLeft = Offset(w * 0.25f, h * 0.25f)
            val topRight = Offset(w * 0.75f, h * 0.25f)
            val bottomLeft = Offset(w * 0.25f, h * 0.75f)
            val bottomRight = Offset(w * 0.75f, h * 0.75f)
            val centerLeft = Offset(w * 0.25f, h * 0.5f)
            val centerRight = Offset(w * 0.75f, h * 0.5f)

            val dotColor = if (valToDraw == 1 || valToDraw == 6) LudoRed else Color(0xFF212121)

            when (valToDraw) {
                1 -> {
                    drawCircle(dotColor, dotRadius, center)
                }
                2 -> {
                    drawCircle(dotColor, dotRadius, topLeft)
                    drawCircle(dotColor, dotRadius, bottomRight)
                }
                3 -> {
                    drawCircle(dotColor, dotRadius, topLeft)
                    drawCircle(dotColor, dotRadius, center)
                    drawCircle(dotColor, dotRadius, bottomRight)
                }
                4 -> {
                    drawCircle(dotColor, dotRadius, topLeft)
                    drawCircle(dotColor, dotRadius, topRight)
                    drawCircle(dotColor, dotRadius, bottomLeft)
                    drawCircle(dotColor, dotRadius, bottomRight)
                }
                5 -> {
                    drawCircle(dotColor, dotRadius, topLeft)
                    drawCircle(dotColor, dotRadius, topRight)
                    drawCircle(dotColor, dotRadius, center)
                    drawCircle(dotColor, dotRadius, bottomLeft)
                    drawCircle(dotColor, dotRadius, bottomRight)
                }
                6 -> {
                    drawCircle(dotColor, dotRadius, topLeft)
                    drawCircle(dotColor, dotRadius, topRight)
                    drawCircle(dotColor, dotRadius, centerLeft)
                    drawCircle(dotColor, dotRadius, centerRight)
                    drawCircle(dotColor, dotRadius, bottomLeft)
                    drawCircle(dotColor, dotRadius, bottomRight)
                }
            }
        }
    }
}
