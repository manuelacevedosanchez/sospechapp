package com.masmultimedia.sospechapp.ui.misc

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.masmultimedia.sospechapp.BuildConfig
import com.masmultimedia.sospechapp.ui.components.SospechCard
import com.masmultimedia.sospechapp.ui.components.SospechScaffold
import com.masmultimedia.sospechapp.ui.components.SospechTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiscScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    val privacyPolicyUrl = BuildConfig.PRIVACY_POLICY_URL

    SospechScaffold(
        topBar = {
            SospechTopBar(
                title = "Miscelánea",
                subtitle = "Info y enlaces",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SospechCard(title = "Sobre la app") {
                Text(
                    text = "SospechApp · v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Party game de deducción social: la app solo reparte roles y palabra. El show lo ponéis vosotros 😄",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            SospechCard(title = "Redes / Proyecto") {
                LinkRow(
                    text = "GitHub: SospechApp (repo)",
                    onClick = { uriHandler.openUri("https://github.com/manuelasan/SospechApp") }
                )
                LinkRow(
                    text = "Política de privacidad",
                    onClick = {
                        if (privacyPolicyUrl.isNotBlank()) {
                            uriHandler.openUri(privacyPolicyUrl)
                        }
                    }
                )
            }

            SospechCard(title = "Créditos") {
                Text(
                    text = "Hecha con Kotlin + Jetpack Compose.\nIdea: Manu. Arte: tu futuro yo diseñador 😄",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun LinkRow(
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp)
    )
}