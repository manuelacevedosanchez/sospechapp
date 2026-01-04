package com.masmultimedia.sospechapp.ui.splash

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.masmultimedia.sospechapp.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onTimeout: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        delay(1300)
        onTimeout()
    }

    val scale = androidx.compose.animation.core.rememberInfiniteTransition(label = "pulse")
        .animateFloat(
            initialValue = 0.98f,
            targetValue = 1.02f,
            animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                animation = androidx.compose.animation.core.tween(900),
                repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
            ),
            label = "scale"
        ).value

    com.masmultimedia.sospechapp.ui.components.SospechBackground {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                val iconBob = rememberInfiniteTransition(label = "icon_bob")
                    .animateFloat(
                        initialValue = -2f,
                        targetValue = 2f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(900),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "icon_rot"
                    ).value

                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier
                        .graphicsLayer {
                            rotationZ = iconBob
                            scaleX = 1.05f
                            scaleY = 1.05f
                        }
                )
                Spacer(Modifier.height(12.dp))

                Text(
                    text = "SospechApp",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "El impostor está entre vosotros",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
                )
            }
        }
    }
}