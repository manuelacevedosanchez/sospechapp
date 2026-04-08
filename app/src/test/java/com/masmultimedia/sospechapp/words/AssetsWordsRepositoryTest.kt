package com.masmultimedia.sospechapp.words

import android.content.Context
import android.content.res.AssetManager
import com.masmultimedia.sospechapp.words.data.AssetsWordsRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class AssetsWordsRepositoryTest {
    private lateinit var context: Context
    private lateinit var repository: AssetsWordsRepository

    private val json = """
        {
          "words": [
            {"text": "manzana", "category": "comida", "difficulty": "easy"},
            {"text": "tigre", "category": "animales", "difficulty": "medium"},
            {"text": "lápiz", "category": "objetos", "difficulty": "easy"},
            {"text": "volcán", "category": "naturaleza", "difficulty": "hard"}
          ]
        }
    """.trimIndent()

    @Before
    fun setUp() {
        context = Mockito.mock(Context::class.java)
        val assetManager = Mockito.mock(AssetManager::class.java)
        Mockito.`when`(context.assets).thenReturn(assetManager)
        Mockito.`when`(assetManager.open(Mockito.anyString())).thenReturn(json.byteInputStream())
        repository = AssetsWordsRepository(context, assetFileName = "words_seed.json")
    }

    @Test
    fun `getRandomWord sin filtro devuelve cualquiera`() = runBlocking {
        val word = repository.getRandomWord(null, null)
        println("[TEST] Palabra sin filtro: $word")
        val validWords = setOf("manzana", "tigre", "lápiz", "volcán")
        assert(validWords.contains(word))
    }

    @Test
    fun `getRandomWord filtra por categoria`() = runBlocking {
        val word = repository.getRandomWord("comida", null)
        println("[TEST] Palabra por categoria: $word")
        val validWords = setOf("manzana")
        assert(validWords.contains(word))
    }

    @Test
    fun `getRandomWord filtra por dificultad`() = runBlocking {
        val word = repository.getRandomWord(null, "hard")
        println("[TEST] Palabra por dificultad: $word")
        val validWords = setOf("volcán")
        assert(validWords.contains(word))
    }

    @Test
    fun `getRandomWord filtra por categoria y dificultad`() = runBlocking {
        val word = repository.getRandomWord("objetos", "easy")
        println("[TEST] Palabra por categoria y dificultad: $word")
        val validWords = setOf("lápiz")
        assert(validWords.contains(word))
    }

    @Test
    fun `getRandomWord fallback si no hay coincidencias`() = runBlocking {
        val word = repository.getRandomWord("lugares", "easy")
        println("[TEST] Palabra fallback: $word")
        val validWords = setOf("manzana", "tigre", "lápiz", "volcán")
        assert(validWords.contains(word))
    }
}
