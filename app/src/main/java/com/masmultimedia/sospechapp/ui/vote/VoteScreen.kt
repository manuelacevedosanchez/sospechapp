package com.masmultimedia.sospechapp.ui.vote

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VoteScreen(
    impostorIndices: List<Int>,
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    var revealed by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!revealed) {
            Text(
                text = "It's time to vote! Discuss and choose who you think is the impostor.",
                style = MaterialTheme.typography.headlineSmall
            )
            Button(
                onClick = { revealed = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
            ) {
                Text("Reveal the impostor(s)")
            }
        } else {
            Text(
                text = if (impostorIndices.size == 1) {
                    "The impostor was player ${impostorIndices.first() + 1}!"
                } else {
                    "The impostors were: " + impostorIndices.joinToString { "Player ${it + 1}" }
                },
                style = MaterialTheme.typography.headlineMedium
            )
            Button(
                onClick = onBackToMenu,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
            ) {
                Text("Back to menu")
            }
        }
    }
}

