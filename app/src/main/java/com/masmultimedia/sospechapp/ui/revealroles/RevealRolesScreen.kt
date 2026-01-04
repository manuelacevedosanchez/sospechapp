package com.masmultimedia.sospechapp.ui.revealroles

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.masmultimedia.sospechapp.game.GameState
import com.masmultimedia.sospechapp.game.PlayerRole
import com.masmultimedia.sospechapp.ui.components.PrimaryButton
import com.masmultimedia.sospechapp.ui.components.SospechCard
import com.masmultimedia.sospechapp.ui.components.SospechScaffold
import com.masmultimedia.sospechapp.ui.components.SospechTopBar

private data class RevealKey(
    val isRoleVisible: Boolean,
    val playerIndex: Int
)

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RevealRolesScreen(
    state: GameState,
    onRevealRole: () -> Unit,
    onHideAndNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    val playerNumber = (state.currentPlayerIndex + 1).coerceAtLeast(1)
    val totalPlayers = state.totalPlayers.coerceAtLeast(1)

    SospechScaffold(
        topBar = {
            SospechTopBar(
                title = "Revelar roles",
                subtitle = "Jugador $playerNumber de $totalPlayers"
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
            if (!state.isGameStarted || state.roles.isEmpty()) {
                SospechCard(title = "Sin partida") {
                    Text(
                        text = "No hay partida activa. Vuelve al menú y crea una nueva.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                    )
                }
                return@SospechScaffold
            }

            val target = RevealKey(
                isRoleVisible = state.isRoleVisible,
                playerIndex = state.currentPlayerIndex
            )

            AnimatedContent(
                targetState = target,
                transitionSpec = {
                    (fadeIn(tween(180)) + scaleIn(initialScale = 0.96f, animationSpec = tween(180))) togetherWith
                            (fadeOut(tween(140)) + scaleOut(targetScale = 0.98f, animationSpec = tween(140)))
                },
                label = "reveal_role_transition"
            ) { key ->
                val playerNumber = key.playerIndex + 1

                if (!key.isRoleVisible) {
                    SospechCard(title = "Turno del jugador $playerNumber") {
                        Text(
                            text = "Pasa el móvil a esa persona y pulsa para ver su rol.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Consejo: que nadie mire la pantalla 👀",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Spacer(modifier = Modifier.weight(1f))

                    PrimaryButton(
                        text = "Ver rol",
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onRevealRole()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    val role = state.roles.getOrNull(key.playerIndex) ?: PlayerRole.UNKNOWN

                    RoleCard(role = role, word = state.currentWord)

                    Spacer(modifier = Modifier.height(16.dp))
                    Spacer(modifier = Modifier.weight(1f))

                    PrimaryButton(
                        text = "Ocultar y pasar el móvil",
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onHideAndNext()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun RoleCard(
    role: PlayerRole,
    word: String?
) {
    val isImpostor = role == PlayerRole.IMPOSTOR
    val title = when (role) {
        PlayerRole.IMPOSTOR -> "Eres el IMPOSTOR"
        PlayerRole.CITIZEN -> "Eres CIUDADANO"
        PlayerRole.UNKNOWN -> "Rol no disponible"
    }

    SospechCard(title = title) {
        when (role) {
            PlayerRole.IMPOSTOR -> {
                Text(
                    text = "No conoces la palabra.\nEscucha, improvisa… y no te delates.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }

            PlayerRole.CITIZEN -> {
                Text(
                    text = "La palabra es:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = word.orEmpty().ifBlank { "—" },
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            PlayerRole.UNKNOWN -> {
                Text(
                    text = "No se ha podido determinar tu rol. Vuelve a crear la partida.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                )
            }
        }

        if (isImpostor) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Tip: haz preguntas, copia estilos… y evita detalles concretos.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}