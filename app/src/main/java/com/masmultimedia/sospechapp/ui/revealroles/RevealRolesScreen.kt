package com.masmultimedia.sospechapp.ui.revealroles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.masmultimedia.sospechapp.game.GameState
import com.masmultimedia.sospechapp.game.PlayerRole

@Composable
fun RevealRolesScreen(
    state: GameState,
    onRevealRole: () -> Unit,
    onHideAndNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold { innerPadding ->
        if (!state.isGameStarted || state.roles.isEmpty()) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "No hay partida activa.")
            }
        } else {
            val playerNumber = state.currentPlayerIndex + 1
            val totalPlayers = state.totalPlayers

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Jugador $playerNumber de $totalPlayers",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (!state.isRoleVisible) {
                    Text(
                        text = "Pasa el móvil al jugador $playerNumber y pulsa para ver su rol.",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = onRevealRole,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Ver rol")
                    }
                } else {
                    val role = state.roles.getOrNull(state.currentPlayerIndex)

                    when (role) {
                        PlayerRole.IMPOSTOR -> {
                            Text(
                                text = "Eres el impostor.\n\nNo conoces la palabra.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        PlayerRole.CITIZEN -> {
                            Text(
                                text = "Eres ciudadano.\n\nLa palabra es:",
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Text(
                                text = state.currentWord.orEmpty(),
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }

                        null -> {
                            Text(
                                text = "No se ha podido cargar tu rol.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        PlayerRole.UNKNOWN -> TODO()
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = onHideAndNext,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Ocultar y pasar el móvil")
                    }
                }
            }
        }
    }
}