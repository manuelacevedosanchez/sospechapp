package com.masmultimedia.sospechapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HowToPlayScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Cómo se juega",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "1. Reúne a tus amigos y abrid SospechApp.\n\n" +
                        "2. Configurad la partida (jugadores e impostores) y, si queréis, escribid una palabra. Si la dejáis vacía, se usará una aleatoria.\n\n" +
                        "3. Pasad el móvil para que cada jugador vea su rol: ciudadanos verán la palabra, el impostor no.\n\n" +
                        "4. Hablando, cada jugador describe la palabra sin decirla directamente, mientras el impostor intenta pasar desapercibido.\n\n" +
                        "5. Al final, votad quién creéis que es el impostor. Podéis darle las vueltas que queráis a las reglas para adaptarlas a vuestro grupo.\n\n" +
                        "6. ¡Divertíos y jugad varias rondas!"
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Volver al menú")
            }
        }
    }
}