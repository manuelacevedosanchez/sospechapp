package com.masmultimedia.sospechapp.core.text

import android.content.Context

class AndroidStringProvider(
    private val context: Context,
) : StringProvider {

    override fun getString(resId: Int, vararg args: Any): String {
        return context.getString(resId, *args)
    }

}