package com.masmultimedia.sospechapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun BannerAd(
    adUnitId: String,
    modifier: Modifier = Modifier
) {
    if (adUnitId.isBlank()) return

    val context = LocalContext.current
    val adView = remember(context, adUnitId) {
        AdView(context).apply {
            setAdSize(AdSize.BANNER)
            setAdUnitId(adUnitId)
        }
    }

    DisposableEffect(adView) {
        onDispose { adView.destroy() }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            adView.apply {
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
