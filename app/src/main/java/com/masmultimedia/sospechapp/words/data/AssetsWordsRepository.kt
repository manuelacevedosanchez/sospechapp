package com.masmultimedia.sospechapp.words.data

import android.content.Context
import android.util.Log
import androidx.annotation.Keep
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.masmultimedia.sospechapp.words.domain.CatalogFallbackReason
import com.masmultimedia.sospechapp.words.domain.Word
import com.masmultimedia.sospechapp.words.domain.WordsRepository
import com.masmultimedia.sospechapp.words.domain.WordsResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
import java.text.Normalizer
import java.util.Locale

@Keep
internal data class WordsAssetRoot(
    @SerializedName("version") val version: Int,
    @SerializedName("words") val words: List<WordAssetDto>,
)

@Keep
internal data class WordAssetDto(
    @SerializedName("text") val text: String?,
    @SerializedName("category") val category: String?,
    @SerializedName("difficulty") val difficulty: String?,
)

interface CatalogLogger {
    fun info(message: String)
    fun warning(message: String)
    fun error(message: String, cause: Throwable)
}

private object AndroidCatalogLogger : CatalogLogger {
    override fun info(message: String) = Log.i(TAG, message).let { Unit }
    override fun warning(message: String) = Log.w(TAG, message).let { Unit }
    override fun error(message: String, cause: Throwable) = Log.e(TAG, message, cause).let { Unit }

    private const val TAG = "AssetsWordsRepository"
}

class AssetsWordsRepository(
    private val context: Context,
    private val assetFileName: String = "words_seed.json",
    private val logger: CatalogLogger = AndroidCatalogLogger,
) : WordsRepository {
    @Volatile
    private var cachedWords: List<Word>? = null

    override suspend fun syncIfNeeded() = Unit

    override suspend fun getWords(category: String?, difficulty: String?): WordsResult =
        withContext(Dispatchers.IO) {
            val catalog = cachedWords?.let { CatalogLoad.Success(it) } ?: loadCatalog()
            when (catalog) {
                is CatalogLoad.Success -> {
                    cachedWords = catalog.words
                    catalog.words.filtered(category, difficulty)
                        .takeIf { it.isNotEmpty() }
                        ?.let(WordsResult::Success)
                        ?: WordsResult.Empty
                }

                CatalogLoad.Empty -> fallbackResult(category, difficulty, CatalogFallbackReason.EMPTY_CATALOG)
                is CatalogLoad.Failure -> {
                    logger.error("Unable to load word catalog; using bundled fallback", catalog.cause)
                    fallbackResult(category, difficulty, CatalogFallbackReason.READ_ERROR)
                }
            }
        }

    private fun loadCatalog(): CatalogLoad = try {
        val json = context.assets.open(assetFileName).bufferedReader().use { it.readText() }
        val root = Gson().fromJson(json, WordsAssetRoot::class.java)
            ?: return CatalogLoad.Failure(IllegalStateException("Catalog root is null"))
        val words = root.words.mapNotNull { dto ->
            val text = dto.text?.trim().orEmpty()
            val category = dto.category?.trim()?.lowercase(Locale.ROOT).orEmpty()
            val difficulty = dto.difficulty?.trim()?.lowercase(Locale.ROOT).orEmpty()
            if (text.isBlank() || category.isBlank() || difficulty.isBlank()) null else Word(
                id = stableId(category, difficulty, text),
                text = text,
                category = category,
                difficulty = difficulty,
            )
        }
        if (words.isEmpty()) {
            logger.warning("Word catalog loaded successfully but contains no valid entries")
            CatalogLoad.Empty
        } else {
            logger.info("Word catalog loaded: ${words.size} entries, version ${root.version}")
            CatalogLoad.Success(words)
        }
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (error: Exception) {
        CatalogLoad.Failure(error)
    }

    private fun fallbackResult(
        category: String?,
        difficulty: String?,
        reason: CatalogFallbackReason,
    ): WordsResult {
        val words = fallbackWords.filtered(category, difficulty)
        logger.warning("Fallback word catalog selected: reason=$reason, entries=${words.size}")
        return if (words.isEmpty()) WordsResult.Empty else WordsResult.Fallback(words, reason)
    }

    private sealed interface CatalogLoad {
        data class Success(val words: List<Word>) : CatalogLoad
        data object Empty : CatalogLoad
        data class Failure(val cause: Throwable) : CatalogLoad
    }

}

internal fun List<Word>.filtered(category: String?, difficulty: String?): List<Word> {
    val normalizedCategory = category?.trim()?.lowercase(Locale.ROOT)
    val normalizedDifficulty = difficulty?.trim()?.lowercase(Locale.ROOT)
    return filter { word ->
        (normalizedCategory == null || word.category == normalizedCategory) &&
            (normalizedDifficulty == null || word.difficulty == normalizedDifficulty)
    }
}

internal fun stableId(category: String, difficulty: String, text: String): String {
    val normalizedText = Normalizer.normalize(text.trim().lowercase(Locale.ROOT), Normalizer.Form.NFD)
        .replace("\\p{M}+".toRegex(), "")
        .replace("[^a-z0-9]+".toRegex(), "-")
        .trim('-')
    return "asset:$category:$difficulty:$normalizedText"
}
