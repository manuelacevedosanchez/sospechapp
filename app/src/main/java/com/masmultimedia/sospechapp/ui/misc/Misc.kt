package com.masmultimedia.sospechapp.ui.misc

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.masmultimedia.sospechapp.BuildConfig
import com.masmultimedia.sospechapp.R
import com.masmultimedia.sospechapp.ui.components.SospechCard
import com.masmultimedia.sospechapp.ui.components.SospechScaffold
import com.masmultimedia.sospechapp.ui.components.SospechTopBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiscScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    val privacyPolicyUrl = BuildConfig.PRIVACY_POLICY_URL
    val context = LocalContext.current
    val email = stringResource(R.string.misc_contact_email)
    val subject = stringResource(R.string.misc_contact_email_subject)

    SospechScaffold(
        topBar = {
            SospechTopBar(
                title = stringResource(R.string.misc_title),
                subtitle = stringResource(R.string.misc_subtitle),
                onBackClick = onBackClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            SospechCard(title = stringResource(R.string.misc_about)) {
                Text(
                    text =
                        stringResource(
                            R.string.misc_version,
                            BuildConfig.VERSION_NAME,
                            BuildConfig.VERSION_CODE,
                        ),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = stringResource(R.string.misc_party_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            SospechCard(title = stringResource(R.string.misc_social)) {
                LinkRow(
                    text = stringResource(R.string.misc_github),
                    onClick = { /* No action, repo pendiente de añadir */ },
                )
                LinkRow(
                    text = stringResource(R.string.misc_privacy),
                    onClick = {
                        if (privacyPolicyUrl.isNotBlank()) {
                            uriHandler.openUri(privacyPolicyUrl)
                        }
                    },
                )
                LinkRow(
                    text = stringResource(R.string.misc_contact),
                    onClick = {
                        val intent =
                            Intent(Intent.ACTION_SENDTO).apply {
                                data = "mailto:$email".toUri()
                                putExtra(Intent.EXTRA_SUBJECT, subject)
                            }
                        context.startActivity(intent)
                    },
                )
            }

            SospechCard(title = stringResource(R.string.misc_credits)) {
                LinkRow(
                    text = stringResource(R.string.misc_credits_linkedin_btn),
                    onClick = { uriHandler.openUri("https://www.linkedin.com/in/manuelacevedo/") },
                )
            }

            SospechCard(title = stringResource(R.string.misc_acknowledgements)) {
                Text(
                    text = stringResource(R.string.misc_acknowledgements_list),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                )
            }
        }
    }
}

@Composable
private fun LinkRow(
    text: String,
    onClick: () -> Unit,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 10.dp),
    )
}
