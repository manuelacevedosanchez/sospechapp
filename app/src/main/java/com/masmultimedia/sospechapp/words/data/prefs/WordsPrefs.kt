package com.masmultimedia.sospechapp.words.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.datastore by preferencesDataStore(name = "words_prefs")

class WordsPrefs(private val context: Context) {

    private val VERSION_KEY = intPreferencesKey("words_data_version")

    suspend fun getLocalVersion(): Int {
        val prefs = context.datastore.data.first()
        return prefs[VERSION_KEY] ?: 0
    }

    suspend fun setLocalVersion(version: Int) {
        context.datastore.edit { prefs ->
            prefs[VERSION_KEY] = version
        }
    }

}