package com.masmultimedia.sospechapp.ui.rounds

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.masmultimedia.sospechapp.R

@Composable
fun RoundsScreen(
    currentRound: Int,
    totalRounds: Int,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (currentRound < totalRounds) {
                stringResource(R.string.rounds_title, currentRound, totalRounds)
            } else {
                stringResource(R.string.rounds_last_title, currentRound, totalRounds)
            },
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = if (currentRound < totalRounds) {
                stringResource(R.string.rounds_message)
            } else {
                stringResource(R.string.rounds_last_message)
            },
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (currentRound < totalRounds) {
                    stringResource(R.string.rounds_next_button)
                } else {
                    stringResource(R.string.rounds_vote_button)
                }
            )
        }
    }
}

