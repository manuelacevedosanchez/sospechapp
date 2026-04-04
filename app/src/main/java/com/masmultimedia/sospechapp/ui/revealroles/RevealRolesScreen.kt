package com.masmultimedia.sospechapp.ui.revealroles

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.masmultimedia.sospechapp.R
import com.masmultimedia.sospechapp.game.GameState
import com.masmultimedia.sospechapp.game.PlayerRole
import com.masmultimedia.sospechapp.ui.components.PrimaryButton
import com.masmultimedia.sospechapp.ui.components.SospechCard
import com.masmultimedia.sospechapp.ui.components.SospechScaffold
import com.masmultimedia.sospechapp.ui.components.SospechTopBar

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun revealRolesScreen(
    state: GameState,
    onRevealRole: () -> Unit,
    onHideAndNext: () -> Unit,
    modifier: Modifier = Modifier
) {

    val playerNumber = (state.currentPlayerIndex + 1).coerceAtLeast(1)
    val totalPlayers = state.totalPlayers.coerceAtLeast(1)

    SospechScaffold(
        topBar = {
            SospechTopBar(
                title = stringResource(R.string.reveal_roles_title),
                subtitle = stringResource(R.string.reveal_roles_subtitle, playerNumber, totalPlayers)
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
                SospechCard(title = stringResource(R.string.reveal_roles_no_game_title)) {
                    Text(
                        text = stringResource(R.string.reveal_roles_no_game_desc),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                    )
                }
                return@SospechScaffold
            }

            revealRolesContent(
                isRoleVisible = state.isRoleVisible,
                playerIndex = state.currentPlayerIndex,
                state = state,
                onRevealRole = onRevealRole,
                onHideAndNext = onHideAndNext
            )
        }
    }
}

@Composable
private fun ColumnScope.revealRolesContent(
    isRoleVisible: Boolean,
    playerIndex: Int,
    state: GameState,
    onRevealRole: () -> Unit,
    onHideAndNext: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val playerNumber = playerIndex + 1

    if (!isRoleVisible) {
        SospechCard(title = stringResource(R.string.reveal_roles_turn_title, playerNumber)) {
            Text(
                text = stringResource(R.string.reveal_roles_turn_desc),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.reveal_roles_tip),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.weight(1f))   // ✅ now it works

        PrimaryButton(
            text = stringResource(R.string.reveal_roles_show_role),
            onClick = {
                if (state.settings.hapticsEnabled) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
                onRevealRole()
            },
            modifier = Modifier.fillMaxWidth()
        )
    } else {
        val role = state.roles.getOrNull(playerIndex) ?: PlayerRole.UNKNOWN

        roleCard(role = role, word = state.currentWord)

        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.weight(1f))   // ✅ now it works

        PrimaryButton(
            text = stringResource(R.string.reveal_roles_hide_and_next),
            onClick = {
                if (state.settings.hapticsEnabled) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
                onHideAndNext()
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun roleCard(
    role: PlayerRole,
    word: String?
) {
    val isImpostor = role == PlayerRole.IMPOSTOR
    val title = when (role) {
        PlayerRole.IMPOSTOR -> stringResource(R.string.reveal_roles_impostor)
        PlayerRole.CITIZEN -> stringResource(R.string.reveal_roles_citizen)
        PlayerRole.UNKNOWN -> stringResource(R.string.reveal_roles_unknown)
    }

    SospechCard(title = title) {
        when (role) {
            PlayerRole.IMPOSTOR -> {
                Text(
                    text = stringResource(R.string.reveal_roles_impostor_desc),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }

            PlayerRole.CITIZEN -> {
                Text(
                    text = stringResource(R.string.reveal_roles_word_label),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = word.orEmpty().ifBlank { stringResource(R.string.reveal_roles_word_dash) },
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            PlayerRole.UNKNOWN -> {
                Text(
                    text = stringResource(R.string.reveal_roles_unknown_desc),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                )
            }
        }

        if (isImpostor) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.reveal_roles_impostor_tip),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}