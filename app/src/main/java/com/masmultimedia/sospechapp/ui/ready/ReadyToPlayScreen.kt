package com.masmultimedia.sospechapp.ui.ready

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.masmultimedia.sospechapp.ui.components.SospechScaffold
import com.masmultimedia.sospechapp.ui.components.SospechTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadyToPlayScreen(
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    SospechScaffold(
        topBar = {
            SospechTopBar(
                title = "Listo para jugar",
                subtitle = "Que empiece el teatro",
                onBackClick = onBackToMenu
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¡Todo listo!",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Todos los jugadores conocen su rol.\n" +
                        "Ya podéis empezar a jugar hablando.",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onBackToMenu,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Volver al menú")
            }
        }
    }
}