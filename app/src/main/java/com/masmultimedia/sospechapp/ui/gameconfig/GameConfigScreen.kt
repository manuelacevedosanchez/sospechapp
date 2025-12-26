package com.masmultimedia.sospechapp.ui.gameconfig

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GameConfigScreen(
    onBackClick: () -> Unit,
    onStartGame: (totalPlayers: Int, impostors: Int, word: String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var totalPlayers by remember { mutableIntStateOf(5) }
    var impostors by remember { mutableIntStateOf(1) }
    var wordInput by remember { mutableStateOf("") }

    // Basic rules: minimum 3 players, at least 1 impostor.
    val safeTotalPlayers = totalPlayers.coerceAtLeast(3)
    val safeImpostors = impostors.coerceIn(1, safeTotalPlayers - 1)

    val isStartEnabled =
        safeTotalPlayers >= 3 &&
                safeImpostors in 1..(safeTotalPlayers - 1)

    Scaffold { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Nueva partida",
                style = MaterialTheme.typography.headlineMedium
            )

            // Player count selector
            Column {
                Text(text = "Número de jugadores")
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { if (totalPlayers > 3) totalPlayers-- },
                    ) {
                        Text(text = "-")
                    }

                    Text(
                        text = totalPlayers.toString(),
                        style = MaterialTheme.typography.headlineSmall
                    )

                    OutlinedButton(
                        onClick = { totalPlayers++ },
                    ) {
                        Text(text = "+")
                    }
                }
            }

            // Impostor count selector
            Column {
                Text(text = "Número de impostores")
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { if (impostors > 1) impostors-- },
                    ) {
                        Text(text = "-")
                    }

                    Text(
                        text = impostors.toString(),
                        style = MaterialTheme.typography.headlineSmall
                    )

                    OutlinedButton(
                        onClick = { if (impostors < safeTotalPlayers - 1) impostors++ },
                    ) {
                        Text(text = "+")
                    }
                }
            }

            OutlinedTextField(
                value = wordInput,
                onValueChange = { wordInput = it },
                label = { Text("Palabra (vacío = aleatoria)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val cleanedWord: String? = wordInput.trim().ifBlank { null }
                    onStartGame(safeTotalPlayers, safeImpostors, cleanedWord)
                },
                enabled = isStartEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Empezar")
            }

            TextButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Volver al menú")
            }
        }
    }
}