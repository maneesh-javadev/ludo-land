package com.creative.ludoland.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creative.ludoland.game.Player
import com.creative.ludoland.ui.theme.ClayShadow
import com.creative.ludoland.ui.theme.CreamTile
import com.creative.ludoland.ui.theme.color
import com.creative.ludoland.ui.theme.darkColor

@Composable
fun PlayerCard(
    player: Player,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val alphaAnim by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.55f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "player_card_alpha"
    )

    val cardShape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .alpha(alphaAnim)
            .shadow(
                elevation = if (isActive) 8.dp else 2.dp,
                shape = cardShape,
                ambientColor = ClayShadow,
                spotColor = ClayShadow
            )
            .border(
                width = if (isActive) 3.dp else 1.5.dp,
                color = if (isActive) player.color.color else Color.LightGray,
                shape = cardShape
            )
            .clip(cardShape)
            .background(CreamTile)
            .padding(vertical = 8.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // "YOUR TURN" Badge
            if (isActive) {
                Box(
                    modifier = Modifier
                        .offset(y = (-4).dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(player.color.color)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "YOUR TURN",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Circular Avatar with Colored Ring
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .shadow(4.dp, CircleShape)
                    .border(2.5.dp, player.color.color, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val r = size.width * 0.35f
                    // Avatar inner icon
                    drawCircle(player.color.color, r, center)
                    drawCircle(player.color.darkColor, r * 0.5f, center)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Player Name Label
            Text(
                text = player.name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                maxLines = 1
            )
        }
    }
}
