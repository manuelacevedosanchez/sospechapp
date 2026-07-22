package com.masmultimedia.sospechapp.words

import android.content.Context
import android.content.res.AssetManager
import com.google.common.truth.Truth.assertThat
import com.masmultimedia.sospechapp.words.data.AssetsWordsRepository
import com.masmultimedia.sospechapp.words.data.CatalogLogger
import com.masmultimedia.sospechapp.words.domain.CatalogFallbackReason
import com.masmultimedia.sospechapp.words.domain.WordsResult
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.Mockito

class AssetsWordsRepositoryTest {
    private val validJson = """
        {"version":1,"words":[
          {"text":"manzana","category":"comida","difficulty":"easy"},
          {"text":"tigre","category":"animales","difficulty":"medium"},
          {"text":"lápiz","category":"objetos","difficulty":"easy"},
          {"text":"volcán","category":"naturaleza","difficulty":"hard"}
        ]}
    """.trimIndent()

    @Test
    fun `filters by category`() = runTest {
        val result = repositoryReturning(validJson).getWords("comida", null)
        assertThat(result).isInstanceOf(WordsResult.Success::class.java)
        assertThat(result.words.map { it.text }).containsExactly("manzana")
    }

    @Test
    fun `filters by difficulty`() = runTest {
        val result = repositoryReturning(validJson).getWords(null, "hard")
        assertThat(result.words.map { it.text }).containsExactly("volcán")
    }

    @Test
    fun `filters by category and difficulty`() = runTest {
        val result = repositoryReturning(validJson).getWords("objetos", "easy")
        assertThat(result.words.map { it.text }).containsExactly("lápiz")
    }

    @Test
    fun `unknown category is empty when primary catalog loaded`() = runTest {
        assertThat(repositoryReturning(validJson).getWords("inexistente", null)).isEqualTo(WordsResult.Empty)
    }

    @Test
    fun `invalid json uses thematic fallback`() = runTest {
        val result = repositoryReturning("not-json").getWords("animales", "easy")
        assertThat(result).isInstanceOf(WordsResult.Fallback::class.java)
        assertThat((result as WordsResult.Fallback).reason).isEqualTo(CatalogFallbackReason.READ_ERROR)
        assertThat(result.words).isNotEmpty()
        assertThat(result.words.all { it.category == "animales" && it.difficulty == "easy" }).isTrue()
    }

    @Test
    fun `missing asset uses thematic fallback`() = runTest {
        val context = Mockito.mock(Context::class.java)
        val assets = Mockito.mock(AssetManager::class.java)
        Mockito.`when`(context.assets).thenReturn(assets)
        Mockito.`when`(assets.open(Mockito.anyString())).thenThrow(java.io.FileNotFoundException("missing"))

        val result = AssetsWordsRepository(context, logger = noOpLogger).getWords("comida", null)

        assertThat(result).isInstanceOf(WordsResult.Fallback::class.java)
        assertThat(result.words).hasSize(10)
    }

    @Test
    fun `empty catalog uses fallback and is not cached`() = runTest {
        val context = Mockito.mock(Context::class.java)
        val assets = Mockito.mock(AssetManager::class.java)
        Mockito.`when`(context.assets).thenReturn(assets)
        var calls = 0
        Mockito.`when`(assets.open(Mockito.anyString())).thenAnswer {
            calls++
            if (calls == 1) "{\"version\":1,\"words\":[]}".byteInputStream()
            else validJson.byteInputStream()
        }
        val repository = AssetsWordsRepository(context, logger = noOpLogger)

        assertThat(repository.getWords("comida", null)).isInstanceOf(WordsResult.Fallback::class.java)
        assertThat(repository.getWords("comida", null)).isInstanceOf(WordsResult.Success::class.java)
        assertThat(calls).isEqualTo(2)
    }

    @Test
    fun `read error is not cached permanently`() = runTest {
        val context = Mockito.mock(Context::class.java)
        val assets = Mockito.mock(AssetManager::class.java)
        Mockito.`when`(context.assets).thenReturn(assets)
        var calls = 0
        Mockito.`when`(assets.open(Mockito.anyString())).thenAnswer {
            calls++
            if (calls == 1) throw java.io.IOException("temporary") else validJson.byteInputStream()
        }
        val repository = AssetsWordsRepository(context, logger = noOpLogger)

        assertThat(repository.getWords("comida", null)).isInstanceOf(WordsResult.Fallback::class.java)
        assertThat(repository.getWords("comida", null)).isInstanceOf(WordsResult.Success::class.java)
        assertThat(calls).isEqualTo(2)
    }

    private fun repositoryReturning(json: String): AssetsWordsRepository {
        val context = Mockito.mock(Context::class.java)
        val assets = Mockito.mock(AssetManager::class.java)
        Mockito.`when`(context.assets).thenReturn(assets)
        Mockito.`when`(assets.open(Mockito.anyString())).thenAnswer { json.byteInputStream() }
        return AssetsWordsRepository(context, logger = noOpLogger)
    }

    private val noOpLogger = object : CatalogLogger {
        override fun info(message: String) = Unit
        override fun warning(message: String) = Unit
        override fun error(message: String, cause: Throwable) = Unit
    }
}
