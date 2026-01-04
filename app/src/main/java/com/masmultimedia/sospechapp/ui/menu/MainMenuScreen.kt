package com.masmultimedia.sospechapp.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
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
        }
    }
}