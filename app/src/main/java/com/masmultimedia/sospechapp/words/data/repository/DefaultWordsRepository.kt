package com.masmultimedia.sospechapp.words.data.repository

import com.masmultimedia.sospechapp.words.data.local.WordEntity
import com.masmultimedia.sospechapp.words.data.local.WordsDao
import com.masmultimedia.sospechapp.words.data.prefs.WordsPrefs
import com.masmultimedia.sospechapp.words.data.remote.WordsApi
import com.masmultimedia.sospechapp.words.domain.WordsRepository
import com.masmultimedia.sospechapp.words.domain.Word
import com.masmultimedia.sospechapp.words.domain.WordsResult
import com.masmultimedia.sospechapp.words.domain.CatalogFallbackReason
import com.masmultimedia.sospechapp.words.data.fallbackWords
import com.masmultimedia.sospechapp.words.data.filtered
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext

class DefaultWordsRepository(
    private val dao: WordsDao,
    private val prefs: WordsPrefs,
    private val api: WordsApi?,
) : WordsRepository {

    override suspend fun syncIfNeeded() {

        withContext(Dispatchers.IO) {
            // If there are no api (V1), nothing to sync
            val safeApy = api ?: return@withContext

            try {
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
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                // If synchronization fails, keep the existing local data.
            }
        }
    }

    override suspend fun getWords(category: String?, difficulty: String?): WordsResult = withContext(Dispatchers.IO) {
        val words = dao.getWordsFiltered(category, difficulty).map { entity ->
            Word(
                id = "db:${entity.id}",
                text = entity.text,
                category = entity.category.orEmpty(),
                difficulty = entity.difficulty.orEmpty(),
            )
        }
        if (words.isEmpty()) {
            val fallback = fallbackWords.filtered(category, difficulty)
            if (fallback.isEmpty()) WordsResult.Empty
            else WordsResult.Fallback(fallback, CatalogFallbackReason.EMPTY_CATALOG)
        } else {
            WordsResult.Success(words)
        }
    }
}
