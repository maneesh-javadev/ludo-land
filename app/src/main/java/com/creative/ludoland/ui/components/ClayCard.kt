package com.creative.ludoland.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.creative.ludoland.ui.theme.ClayBorder
import com.creative.ludoland.ui.theme.ClayShadow
import com.creative.ludoland.ui.theme.CreamTile

@Composable
fun ClayCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = CreamTile,
    borderColor: Color = ClayBorder,
    cornerRadius: Dp = 24.dp,
    elevation: Dp = 10.dp,
    contentPadding: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = ClayShadow,
                spotColor = ClayShadow
            )
            .border(
                width = 3.5.dp,
                color = borderColor,
                shape = shape
            )
            .clip(shape)
            .background(backgroundColor)
            .drawWithContent {
                drawContent()
                // Top glossy highlight overlay
                drawRect(
                    color = Color.White.copy(alpha = 0.25f),
                    size = size.copy(height = size.height * 0.35f)
                )
            }
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
        content = content
    )
}
