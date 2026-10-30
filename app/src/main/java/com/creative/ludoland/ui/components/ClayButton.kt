package com.creative.ludoland.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creative.ludoland.ui.theme.ClayShadow
import com.creative.ludoland.ui.theme.LudoBlue

@Composable
fun ClayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
    color: Color = LudoBlue,
    darkBorderColor: Color? = null,
    textColor: Color = Color.White,
    cornerRadius: Dp = 18.dp,
    enabled: Boolean = true,
    content: (@Composable RowScope.() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "clay_button_scale"
    )

    val offsetY by animateFloatAsState(
        targetValue = if (isPressed && enabled) 4f else 0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "clay_button_offset"
    )

    val computedBorderColor = darkBorderColor ?: color.copy(
        red = (color.red * 0.65f).coerceIn(0f, 1f),
        green = (color.green * 0.65f).coerceIn(0f, 1f),
        blue = (color.blue * 0.65f).coerceIn(0f, 1f)
    )

    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationY = offsetY.dp.toPx()
            }
            .shadow(
                elevation = if (isPressed) 2.dp else 8.dp,
                shape = shape,
                ambientColor = ClayShadow,
                spotColor = ClayShadow
            )
            .border(
                width = 3.5.dp,
                color = computedBorderColor,
                shape = shape
            )
            .clip(shape)
            .background(color = if (enabled) color else color.copy(alpha = 0.5f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .drawWithContent {
                drawContent()
                // Top glossy highlight overlay
                drawRect(
                    color = Color.White.copy(alpha = 0.22f),
                    size = size.copy(height = size.height * 0.42f)
                )
            }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (content != null) {
                content()
            } else if (text != null) {
                Text(
                    text = text,
                    color = textColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
