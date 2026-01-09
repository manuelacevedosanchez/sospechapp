package com.masmultimedia.sospechapp.ui.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun SospechScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHostState: SnackbarHostState? = null,
    snackbarHost: @Composable (SnackbarHostState) -> Unit = { SnackbarHost(hostState = it) },
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    // For further customization of the background
    backgroundDecoration: @Composable BoxScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val hostState = snackbarHostState ?: LocalSospechSnackbarHostState.current

    SospechBackground(modifier = modifier) {
        backgroundDecoration()

        Scaffold(
            topBar = topBar,
            bottomBar = bottomBar,
            snackbarHost = { snackbarHost(hostState) },
            floatingActionButton = floatingActionButton,
            floatingActionButtonPosition = floatingActionButtonPosition,
            containerColor = Color.Transparent,
            // By now, the background is handled by SospechBackground
            contentWindowInsets = contentWindowInsets
        ) { innerPadding ->
            content(innerPadding)
        }
    }
}