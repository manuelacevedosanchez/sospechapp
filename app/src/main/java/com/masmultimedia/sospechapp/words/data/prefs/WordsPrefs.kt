package com.masmultimedia.sospechapp.words.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

internal val Context.wordsDataStore by preferencesDataStore(name = "words_prefs")

class WordsPrefs(private val context: Context) {

    private val VERSION_KEY = intPreferencesKey("words_data_version")

    suspend fun getLocalVersion(): Int {
        val prefs = context.wordsDataStore.data.first()
        return prefs[VERSION_KEY] ?: 0
    }

    suspend fun setLocalVersion(version: Int) {
        context.wordsDataStore.edit { prefs ->
            prefs[VERSION_KEY] = version
        }
    }

}
