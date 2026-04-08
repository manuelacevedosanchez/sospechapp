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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.masmultimedia.sospechapp.BuildConfig
import com.masmultimedia.sospechapp.R
import com.masmultimedia.sospechapp.ui.components.BannerAd
import com.masmultimedia.sospechapp.ui.components.SospechScaffold
import com.masmultimedia.sospechapp.ui.components.SospechTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadyToPlayScreen(
    onBackToMenu: () -> Unit,
    showAds: Boolean,
    modifier: Modifier = Modifier
) {
    SospechScaffold(
        topBar = {
            SospechTopBar(
                title = stringResource(R.string.ready_title),
                subtitle = stringResource(R.string.ready_subtitle),
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
                text = stringResource(R.string.ready_all_set),
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = stringResource(R.string.ready_instructions),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.weight(1f))

            if (showAds) {
                BannerAd(
                    adUnitId = BuildConfig.ADMOB_BANNER_READY_AD_UNIT_ID,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = onBackToMenu,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.ready_back_to_menu))
            }
        }
    }
}