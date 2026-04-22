package com.masmultimedia.sospechapp.game

import android.app.Application
import com.google.common.truth.Truth.assertThat
import com.masmultimedia.sospechapp.words.data.prefs.CategoryHistoryPrefs
import com.masmultimedia.sospechapp.words.domain.WordsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

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
        stringProvider = mock()
        whenever(stringProvider.getString(any())).thenReturn("Invalid players")
        wordsRepository = mock()
        categoryHistoryPrefs = mock()
        runBlocking {
            whenever(wordsRepository.syncIfNeeded()).thenReturn(Unit)
            whenever(wordsRepository.getRandomWord()).thenReturn("TestWord")
            whenever(wordsRepository.getRandomWord(any(), any())).thenReturn("TestWord")
            whenever(categoryHistoryPrefs.getLastCategory()).thenReturn(null)
            whenever(categoryHistoryPrefs.getRecentWords()).thenReturn(emptyList())
            whenever(categoryHistoryPrefs.addRecentWord(any())).thenReturn(Unit)
            whenever(categoryHistoryPrefs.setLastCategory(any())).thenReturn(Unit)
        }
        val application = mock<Application>()
        viewModel =
            GameViewModel(application, stringProvider, wordsRepository, categoryHistoryPrefs, testDispatcher)
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
        testScheduler.runCurrent()
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
        org.mockito.kotlin.verify(categoryHistoryPrefs).clearHistory()
    }
}
