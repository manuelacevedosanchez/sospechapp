package com.masmultimedia.sospechapp.core.text

import androidx.compose.runtime.staticCompositionLocalOf

val LocalStringProvider = staticCompositionLocalOf<StringProvider> {
    error("No StringProvider provided")
}