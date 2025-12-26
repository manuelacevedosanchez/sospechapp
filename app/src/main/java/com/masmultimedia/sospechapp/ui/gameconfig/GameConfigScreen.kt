package com.masmultimedia.sospechapp.ui.gameconfig

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun GameConfigScreen(
    onBackClick: () -> Unit,
    onStartGame: (totalPlayers: Int, impostors: Int, word: String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var totalPlayers by remember { mutableIntStateOf(5) }
    var impostors by remember { mutableIntStateOf(1) }
    var wordInput by remember { mutableStateOf("") }

    val safeTotalPlayers = totalPlayers.coerceAtLeast(3)
    val safeImpostors = impostors.coerceIn(1, safeTotalPlayers - 1)

    val isStartEnabled =
        safeTotalPlayers >= 3 && safeImpostors in 1..(safeTotalPlayers - 1)

    SospechScaffold(
        topBar = {
            SospechTopBar(
                title = "Nueva partida",
                subtitle = "Configura la ronda",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SospechCard(title = "Jugadores") {
                StepperRow(
                    value = totalPlayers,
                    onMinus = { if (totalPlayers > 3) totalPlayers-- },
                    onPlus = { totalPlayers++ }
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Mínimo 3",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            SospechCard(title = "Impostores") {
                StepperRow(
                    value = impostors,
                    onMinus = { if (impostors > 1) impostors-- },
                    onPlus = { if (impostors < safeTotalPlayers - 1) impostors++ }
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Entre 1 y ${safeTotalPlayers - 1}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            SospechCard(title = "Palabra") {
                OutlinedTextField(
                    value = wordInput,
                    onValueChange = { wordInput = it },
                    label = { Text("Vacío = aleatoria") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = "Empezar",
                onClick = {
                    val cleanedWord: String? = wordInput.trim().ifBlank { null }
                    onStartGame(safeTotalPlayers, safeImpostors, cleanedWord)
                },
                enabled = isStartEnabled,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            SecondaryButton(
                text = "Volver",
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun StepperRow(
    value: Int,
    onMinus: () -> Unit,
    onPlus: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(onClick = onMinus) { Text("-") }

        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedButton(onClick = onPlus) { Text("+") }
    }
}