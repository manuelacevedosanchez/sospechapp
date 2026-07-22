package com.masmultimedia.sospechapp.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.masmultimedia.sospechapp.BuildConfig
import com.masmultimedia.sospechapp.R
import com.masmultimedia.sospechapp.ui.components.BannerAd
import com.masmultimedia.sospechapp.ui.components.PrimaryButton
import com.masmultimedia.sospechapp.ui.components.SecondaryButton
import com.masmultimedia.sospechapp.ui.components.SospechCard
import com.masmultimedia.sospechapp.ui.components.SospechScaffold
import com.masmultimedia.sospechapp.ui.components.SospechTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    onNewGameClick: () -> Unit,
    onHowToPlayClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onMiscClick: () -> Unit,
    showAds: Boolean,
    modifier: Modifier = Modifier
) {
    SospechScaffold(
        topBar = {
            SospechTopBar(
                title = stringResource(R.string.splash_title),
                subtitle = stringResource(R.string.splash_subtitle)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SospechCard(title = stringResource(R.string.main_get_ready)) {
                Text(
                    text = stringResource(R.string.main_party_desc),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            PrimaryButton(
                text = stringResource(R.string.main_new_game),
                leadingIcon = Icons.Filled.PlayArrow,
                onClick = onNewGameClick,
                modifier = Modifier.fillMaxWidth()
            )

            SecondaryButton(
                text = stringResource(R.string.main_how_to_play),
                leadingIcon = Icons.Filled.Info,
                onClick = onHowToPlayClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(min = 120.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            SecondaryButton(
                text = stringResource(R.string.main_settings),
                leadingIcon = Icons.Filled.Settings,
                onClick = onSettingsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(min = 120.dp)
            )

            SecondaryButton(
                text = stringResource(R.string.main_misc),
                leadingIcon = Icons.Filled.Info,
                onClick = onMiscClick,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (showAds) {
                BannerAd(
                    adUnitId = BuildConfig.ADMOB_BANNER_AD_UNIT_ID,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
