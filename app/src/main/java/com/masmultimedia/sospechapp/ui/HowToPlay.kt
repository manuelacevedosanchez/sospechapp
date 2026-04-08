package com.masmultimedia.sospechapp.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masmultimedia.sospechapp.R
import com.masmultimedia.sospechapp.ui.components.SospechCard
import com.masmultimedia.sospechapp.ui.components.SospechScaffold
import com.masmultimedia.sospechapp.ui.components.SospechTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HowToPlayScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    SospechScaffold(
        topBar = {
            SospechTopBar(
                title = stringResource(R.string.how_to_play_title),
                subtitle = stringResource(R.string.how_to_play_subtitle),
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        // All content and overlays inside the safe area
        val infiniteTransition = rememberInfiniteTransition()
        val arrowAlpha = infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1600, easing = LinearEasing), // Slower blink
                repeatMode = RepeatMode.Reverse
            )
        )
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main scrollable content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SospechCard {
                    Text(
                        text = stringResource(R.string.how_to_play_steps),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = onBackClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text(text = stringResource(R.string.how_to_play_back_to_menu))
                }
            }
            // Overlays: now inside the safe area
            val maxScroll = scrollState.maxValue
            if (scrollState.value > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .height(36.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.30f), Color.Transparent)
                            )
                        )
                )
            }
            if (maxScroll > 0 && scrollState.value < maxScroll) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(80.dp) // Increased height for more visible gradient
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.55f) // Stronger opacity for visibility
                                )
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center // Center content horizontally and vertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "SCROLL",
                            color = Color.White,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(bottom = 1.dp) // Small padding to bring text closer to arrow
                        )
                        Image(
                            painter = painterResource(id = android.R.drawable.arrow_down_float),
                            contentDescription = "Scroll down",
                            modifier = Modifier
                                .size(40.dp)
                                .alpha(arrowAlpha.value)
                        )
                    }
                }
            }
        }
    }
}