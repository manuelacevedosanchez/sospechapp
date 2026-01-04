package com.masmultimedia.sospechapp.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.masmultimedia.sospechapp.game.GameAction
import com.masmultimedia.sospechapp.game.GameState
import com.masmultimedia.sospechapp.ui.components.SospechCard
import com.masmultimedia.sospechapp.ui.components.SospechScaffold
import com.masmultimedia.sospechapp.ui.components.SospechTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: GameState,
    onBackClick: () -> Unit,
    onAction: (GameAction) -> Unit,
    modifier: Modifier = Modifier
) {
    SospechScaffold(
        topBar = {
            SospechTopBar(
                title = "Ajustes",
                subtitle = "Personaliza la experiencia",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SospechCard(title = "Experiencia") {
                SettingRow(
                    title = "Vibración (haptic)",
                    subtitle = "Respuesta suave al pulsar botones",
                    checked = state.settings.hapticsEnabled,
                    onCheckedChange = { onAction(GameAction.SetHapticsEnabled(it)) }
                )

                Spacer(Modifier.padding(top = 8.dp))

                SettingRow(
                    title = "Animaciones",
                    subtitle = "Transiciones al revelar/ocultar rol",
                    checked = state.settings.animationsEnabled,
                    onCheckedChange = { onAction(GameAction.SetAnimationsEnabled(it)) }
                )

                Spacer(Modifier.padding(top = 8.dp))

                SettingRow(
                    title = "Mantener pantalla encendida",
                    subtitle = "Evita que se apague en medio de la partida",
                    checked = state.settings.keepScreenOn,
                    onCheckedChange = { onAction(GameAction.SetKeepScreenOn(it)) }
                )
            }

            SospechCard(title = "Nota") {
                Text(
                    text = "Versión de prueba. Algunos ajustes podrían no estar disponibles o no funcionar correctamente.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun SettingRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}