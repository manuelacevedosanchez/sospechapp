package com.masmultimedia.sospechapp.words.data.repository

import com.masmultimedia.sospechapp.words.data.local.WordEntity
import com.masmultimedia.sospechapp.words.data.local.WordsDao
import com.masmultimedia.sospechapp.words.data.prefs.WordsPrefs
import com.masmultimedia.sospechapp.words.data.remote.WordsApi
import com.masmultimedia.sospechapp.words.domain.WordsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultWordsRepository(
    private val dao: WordsDao,
    private val prefs: WordsPrefs,
    private val api: WordsApi?,
    private val fallBacks: List<String> = listOf("fallo", "error", "problema"),
) : WordsRepository {

    override suspend fun syncIfNeeded() {

        withContext(Dispatchers.IO) {
            // If there are no api (V1), nothing to sync
            val safeApy = api ?: return@withContext

            runCatching {
                val localVersion = prefs.getLocalVersion()
                val remote = safeApy.getWords()

                if (remote.version > localVersion) {
                    // Total replacement (simple and robust)
                    dao.deleteAll()
                    dao.insertAll(remote.words.map { dto ->
                        WordEntity(
                            text = dto.text.trim(),
                            category = dto.category,
                            difficulty = dto.difficulty,
                        )
                    })
                    prefs.setLocalVersion(remote.version)
                }
            }
            // If fails, keep existing data
        }
    }

    override suspend fun getRandomWord(): String = withContext(Dispatchers.IO) {
        // If no words in BD, use fallbacks
        dao.getRandomWord() ?: fallBacks.random()
    }
}