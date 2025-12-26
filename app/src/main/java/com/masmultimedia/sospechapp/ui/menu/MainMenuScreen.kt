package com.masmultimedia.sospechapp.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainMenuScreen(
    onNewGameClick: () -> Unit,
    onHowToPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SospechApp",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onNewGameClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Nueva partida")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onHowToPlayClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Cómo se juega")
            }
        }
    }
}