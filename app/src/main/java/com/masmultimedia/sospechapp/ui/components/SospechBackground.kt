package com.masmultimedia.sospechapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.masmultimedia.sospechapp.R

@Composable
fun SospechBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.background_main),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Si quieres superponer un degradado, descomenta lo siguiente:
        // Box(
        //     modifier = Modifier
        //         .fillMaxSize()
        //         .background(
        //             Brush.radialGradient(
        //                 colors = listOf(
        //                     MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        //                     MaterialTheme.colorScheme.background.copy(alpha = 0.1f)
        //                 )
        //             )
        //         )
        // )
        content()
    }
}