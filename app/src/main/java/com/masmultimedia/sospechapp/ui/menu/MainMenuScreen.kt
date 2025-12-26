package com.masmultimedia.sospechapp.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.masmultimedia.sospechapp.ui.components.PrimaryButton
import com.masmultimedia.sospechapp.ui.components.SecondaryButton
import com.masmultimedia.sospechapp.ui.components.SospechCard
import com.masmultimedia.sospechapp.ui.components.SospechScaffold

@Composable
fun MainMenuScreen(
    onNewGameClick: () -> Unit,
    onHowToPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SospechScaffold { innerPadding ->
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
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(18.dp))

            SospechCard(
                title = "Modo fiesta",
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Pasa el móvil, memoriza tu rol... y que empiece el teatro.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            PrimaryButton(
                text = "Nueva partida",
                onClick = onNewGameClick,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            SecondaryButton(
                text = "Cómo se juega",
                onClick = onHowToPlayClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
