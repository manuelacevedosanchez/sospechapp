package com.masmultimedia.sospechapp.game

import android.app.Application
import com.google.common.truth.Truth.assertThat
import com.masmultimedia.sospechapp.words.data.prefs.CategoryHistoryPrefs
import com.masmultimedia.sospechapp.words.domain.WordsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {
    private lateinit var viewModel: GameViewModel
    private lateinit var stringProvider: StringProvider
    private lateinit var wordsRepository: WordsRepository
    private lateinit var categoryHistoryPrefs: CategoryHistoryPrefs
    private val testDispatcher = StandardTestDispatcher()

    @Before
    @OptIn(ExperimentalCoroutinesApi::class)
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        stringProvider = mockk()
        coEvery { stringProvider.getString(any()) } returns "Invalid players"
        val assetsWordsRepository =
            mockk<com.masmultimedia.sospechapp.words.data.AssetsWordsRepository>()
        coEvery { assetsWordsRepository.getRandomWord() } returns "TestWord"
        coEvery { assetsWordsRepository.getRandomWord(any(), any()) } returns "TestWord"
        assetsWordsRepository.cachedWords = listOf(
            com.masmultimedia.sospechapp.words.data.AssetsWordsRepository.WordAsset(
                text = "TestWord",
                category = "comida",
                difficulty = "easy"
            )
        )
        wordsRepository = assetsWordsRepository
        categoryHistoryPrefs = mockk(relaxed = true)
        coEvery { wordsRepository.syncIfNeeded() } returns Unit
        coEvery { categoryHistoryPrefs.getLastCategory() } returns null
        coEvery { categoryHistoryPrefs.getRecentWords() } returns emptyList()
        coEvery { categoryHistoryPrefs.addRecentWord(any()) } returns Unit
        coEvery { categoryHistoryPrefs.setLastCategory(any()) } returns Unit
        val application = mockk<Application>()
        viewModel = GameViewModel(
            application,
            stringProvider,
            wordsRepository,
            categoryHistoryPrefs,
            testDispatcher
        )
    }

    @After
    @OptIn(ExperimentalCoroutinesApi::class)
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `startGame with invalid players shows error`() = runTest {
        viewModel.onAction(
            GameAction.StartGame(
                totalPlayers = 2,
                impostors = 1,
                wordInput = null,
                category = null,
                difficulty = null
            )
        )
        val state = viewModel.uiState.value
        assertThat(state.errorMessage).isEqualTo("Invalid players")
    }

    @Test
    fun `startGame with valid players does not show error`() = runTest {
        viewModel.onAction(
            GameAction.StartGame(
                totalPlayers = 5,
                impostors = 1,
                wordInput = "",
                category = null,
                difficulty = null
            )
        )
        advanceUntilIdle() // Ensure all coroutines complete
        val state = viewModel.uiState.value
        println("Valor de currentWord: '${state.currentWord}'")
        assertThat(state.errorMessage).isNull()
        assertThat(state.isGameStarted).isTrue()
        assertThat(state.currentWord).isNotNull()
        assertThat(state.currentWord).isNotEmpty()
    }

    @Test
    fun `clearHistory calls prefs clearHistory`() = runTest {
        viewModel.clearHistory()
        testScheduler.runCurrent()
        coVerify { categoryHistoryPrefs.clearHistory() }
    }
}
