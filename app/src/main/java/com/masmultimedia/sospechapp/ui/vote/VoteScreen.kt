package com.masmultimedia.sospechapp.ui.vote

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.masmultimedia.sospechapp.R

@Composable
fun VoteScreen(
    impostorIndices: List<Int>,
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    var revealed by rememberSaveable { mutableStateOf(false) }
    val impostorCount = impostorIndices.size
    val playerLabels = impostorIndices.map { index ->
        stringResource(R.string.vote_player_number, index + 1)
    }
    val playerList = when (playerLabels.size) {
        0 -> stringResource(R.string.vote_no_impostors)
        1 -> playerLabels.first()
        else -> playerLabels.dropLast(1)
            .joinToString(stringResource(R.string.vote_list_separator)) +
            stringResource(R.string.vote_list_final_separator) +
            playerLabels.last()
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!revealed) {
            Text(
                text = stringResource(R.string.vote_instructions),
                style = MaterialTheme.typography.headlineSmall
            )
            Button(
                onClick = { revealed = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
            ) {
                Text(pluralStringResource(R.plurals.vote_reveal_button, impostorCount))
            }
        } else {
            Text(
                text = pluralStringResource(R.plurals.vote_result_title, impostorCount),
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = playerList,
                style = MaterialTheme.typography.headlineSmall
            )
            Button(
                onClick = onBackToMenu,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
            ) {
                Text(stringResource(R.string.vote_back_to_menu))
            }
        }
    }
}
