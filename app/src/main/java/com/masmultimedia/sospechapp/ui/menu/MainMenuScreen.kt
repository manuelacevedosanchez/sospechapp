package com.masmultimedia.sospechapp.ui.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.masmultimedia.sospechapp.R
import com.masmultimedia.sospechapp.ui.components.PrimaryButton
import com.masmultimedia.sospechapp.ui.components.SecondaryButton
import com.masmultimedia.sospechapp.ui.components.SospechCard
import com.masmultimedia.sospechapp.ui.components.SospechScaffold
import com.masmultimedia.sospechapp.ui.components.SospechTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    onNewGameClick: () -> Unit,
    onHowToPlayClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onMiscClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SospechScaffold(
        topBar = {
            SospechTopBar(
                title = "SospechApp",
                subtitle = "El impostor está entre vosotros"
            )
        },

        backgroundDecoration = {
            Image(
                painter = painterResource(R.drawable.main_menu_screen_background_01),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                modifier = Modifier.matchParentSize()
            )

            // Vertical gradient for better text visibility
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0f to Color.Black.copy(alpha = 0.70f),
                                0.40f to Color.Black.copy(alpha = 0.35f),
                                1f to Color.Black.copy(alpha = 0.82f)
                            )
                        )
                    )
            )

            // Soft radial gradient for better text visibility
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.45f)
                            ),
                            radius = 900f
                        )
                    )
            )
        }

    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SospechCard(title = "Modo fiesta") {
                    Text(
                        text = "Pasa el móvil, memoriza tu rol... y que empiece el teatro.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                PrimaryButton(
                    text = "Nueva partida",
                    leadingIcon = Icons.Filled.PlayArrow,
                    onClick = onNewGameClick,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SecondaryButton(
                        text = "Cómo se juega",
                        leadingIcon = Icons.Filled.Info,
                        onClick = onHowToPlayClick,
                        modifier = Modifier.weight(1f)
                    )
                    SecondaryButton(
                        text = "Ajustes",
                        leadingIcon = Icons.Filled.Settings,
                        onClick = onSettingsClick,
                        modifier = Modifier.weight(1f)
                    )
                }

                SecondaryButton(
                    text = "Miscelánea",
                    leadingIcon = Icons.Filled.Info,
                    onClick = onMiscClick,
                    modifier = Modifier.fillMaxWidth()
                )

                // Extra space at the bottom
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

}