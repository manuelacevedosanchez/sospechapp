package com.masmultimedia.sospechapp.core.text

import androidx.annotation.StringRes

interface StringProvider {

    fun getString(@StringRes resId: Int, vararg args: Any): String

}