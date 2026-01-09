package com.masmultimedia.sospechapp.core.text

import androidx.annotation.StringRes

sealed interface UiText {

    data class Dynamic(val value: String) : UiText

    data class TextResource(
        @StringRes val resId: Int,
        val args: List<Any> = emptyList(),
    ) : UiText
}

fun UiText.asString(provider: StringProvider): String {
    return when (this) {
        is UiText.Dynamic -> value
        is UiText.TextResource -> provider.getString(resId, *args.toTypedArray())
    }
}