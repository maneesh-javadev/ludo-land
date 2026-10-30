package com.creative.ludoland.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creative.ludoland.ui.components.ClayButton
import com.creative.ludoland.ui.components.ClayCard
import com.creative.ludoland.ui.components.DiceView
import com.creative.ludoland.ui.components.GameBackground
import com.creative.ludoland.ui.theme.LudoRed
import com.creative.ludoland.ui.theme.LudoYellow
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "splash_animations")

    val diceBounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dice_bounce"
    )

    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_scale"
    )

    LaunchedEffect(Unit) {
        delay(2500)
        onNavigateToHome()
    }

    GameBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            BoxLogo(scale = logoScale)

            Spacer(modifier = Modifier.height(40.dp))

            DiceView(
                diceValue = 6,
                isRolling = false,
                onClick = {},
                size = 88.dp,
                modifier = Modifier.graphicsLayer {
                    translationY = diceBounce
                }
            )

            Spacer(modifier = Modifier.height(48.dp))

            ClayButton(
                onClick = onNavigateToHome,
                text = "START GAME 🎲",
                color = LudoYellow,
                textColor = Color(0xFF3E2723),
                cornerRadius = 24.dp
            )
        }
    }
}

@Composable
private fun BoxLogo(scale: Float) {
    ClayCard(
        backgroundColor = LudoRed,
        borderColor = Color(0xFFB71C1C),
        cornerRadius = 28.dp,
        modifier = Modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "LUDO LAND",
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 2.sp
            )
            Text(
                text = "Classic Board Fun",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = LudoYellow
            )
        }
    }
}
