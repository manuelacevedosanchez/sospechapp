package com.masmultimedia.sospechapp.game

import android.app.Application
import com.google.common.truth.Truth.assertThat
import com.masmultimedia.sospechapp.words.data.prefs.CategoryHistoryPrefs
import com.masmultimedia.sospechapp.words.domain.Word
import com.masmultimedia.sospechapp.words.domain.WordsRepository
import com.masmultimedia.sospechapp.words.domain.WordsResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.Dispatchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: WordsRepository
    private lateinit var history: CategoryHistoryPrefs
    private lateinit var viewModel: GameViewModel

    private val words = listOf(
        word("comida", "easy", "pizza"),
        word("comida", "easy", "paella"),
        word("comida", "easy", "sushi"),
        word("comida", "easy", "taco"),
        word("animales", "easy", "perro"),
        word("objetos", "easy", "llave"),
        word("lugares", "easy", "museo"),
        word("naturaleza", "easy", "bosque"),
        word("personajes", "easy", "pirata"),
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = mockk()
        history = mockk(relaxed = true)
        coEvery { repository.syncIfNeeded() } returns Unit
        coEvery { repository.getWords(any(), any()) } answers {
            val category = firstArg<String?>()
            val difficulty = secondArg<String?>()
            WordsResult.Success(words.filter {
                (category == null || it.category == category) &&
                    (difficulty == null || it.difficulty == difficulty)
            })
        }
        coEvery { history.getUsedWordIds(any()) } returns emptySet()
        viewModel = createViewModel(Random(7))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invalid players are rejected`() = runTest {
        viewModel.onAction(startAction(players = 2))
        assertThat(viewModel.uiState.value.errorMessage).isEqualTo("Invalid players")
        assertThat(viewModel.uiState.value.isGameStarted).isFalse()
    }

    @Test
    fun `invalid impostors and rounds are rejected`() = runTest {
        listOf(
            startAction(impostors = 0),
            startAction(impostors = 5),
            startAction(impostors = 6),
            startAction(rounds = 0),
        ).forEach { action ->
            viewModel.onAction(action)
            assertThat(viewModel.uiState.value.isGameStarted).isFalse()
        }
    }

    @Test
    fun `valid game starts with catalog word and roles`() = runTest {
        viewModel.onAction(startAction())
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isGameStarted).isTrue()
        assertThat(state.phase).isEqualTo(GamePhase.REVEALING_ROLES)
        assertThat(state.currentWord).isNotEmpty()
        assertThat(state.roles).hasSize(5)
        assertThat(state.roles.count { it == PlayerRole.IMPOSTOR }).isEqualTo(1)
    }

    @Test
    fun `empty custom mode is rejected`() = runTest {
        viewModel.onAction(startAction(useCustom = true, customWord = "  "))
        assertThat(viewModel.uiState.value.customWordError).isTrue()
        assertThat(viewModel.uiState.value.isGameStarted).isFalse()
    }

    @Test
    fun `valid custom word bypasses catalog and history`() = runTest {
        viewModel.onAction(startAction(useCustom = true, customWord = "  volcán  "))
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.currentWord).isEqualTo("volcán")
        coVerify(exactly = 0) { repository.getWords(any(), any()) }
        coVerify(exactly = 0) { repository.syncIfNeeded() }
        coVerify(exactly = 0) { history.addUsedWordId(any(), any()) }
    }

    @Test
    fun `all categories choose category before word`() = runTest {
        val counts = mutableMapOf<String, Int>()
        repeat(300) {
            viewModel.onAction(startAction())
            advanceUntilIdle()
            val selected = words.first { it.text == viewModel.uiState.value.currentWord }.category
            counts[selected] = counts.getOrDefault(selected, 0) + 1
            viewModel.onAction(GameAction.ResetGame)
        }

        assertThat(counts.keys).containsExactly("comida", "animales", "objetos", "lugares", "naturaleza", "personajes")
        assertThat(counts.values.max() - counts.values.min()).isLessThan(35)
    }

    @Test
    fun `selection respects difficulty`() = runTest {
        viewModel.onAction(startAction(difficulty = "easy"))
        advanceUntilIdle()
        assertThat(words.first { it.text == viewModel.uiState.value.currentWord }.difficulty).isEqualTo("easy")
    }

    @Test
    fun `fallback usage is exposed in state`() = runTest {
        coEvery { repository.getWords(any(), any()) } returns WordsResult.Fallback(
            listOf(word("comida", "easy", "pizza")),
            com.masmultimedia.sospechapp.words.domain.CatalogFallbackReason.READ_ERROR,
        )
        viewModel.onAction(startAction(category = "comida"))
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.isUsingFallback).isTrue()
    }

    @Test
    fun `history avoids repeats until 75 percent then resets`() = runTest {
        val used = mutableSetOf<String>()
        coEvery { history.getUsedWordIds(any()) } answers { used.toSet() }
        coEvery { history.addUsedWordId(any(), any()) } answers { used += secondArg<String>() }
        coEvery { history.clearWordHistory(any()) } answers { used.clear() }

        val selected = buildList {
            repeat(3) {
                viewModel.onAction(startAction(category = "comida"))
                advanceUntilIdle()
                add(viewModel.uiState.value.currentWord)
                viewModel.onAction(GameAction.ResetGame)
            }
        }
        assertThat(selected.toSet()).hasSize(3)

        viewModel.onAction(startAction(category = "comida"))
        advanceUntilIdle()
        coVerify(exactly = 1) { history.clearWordHistory(any()) }
    }

    @Test
    fun `one round is shown before vote`() = runTest {
        startReadyGame(rounds = 1)
        val roundNavigation = async(UnconfinedTestDispatcher(testScheduler)) { viewModel.effect.first() }
        viewModel.onAction(GameAction.StartRounds)
        assertThat(roundNavigation.await()).isEqualTo(GameEffect.NavigateToRound)
        assertThat(viewModel.uiState.value.currentRound).isEqualTo(1)

        val voteNavigation = async(UnconfinedTestDispatcher(testScheduler)) { viewModel.effect.first() }
        viewModel.onAction(GameAction.FinishRound)
        assertThat(voteNavigation.await()).isEqualTo(GameEffect.NavigateToVote)
    }

    @Test
    fun `multiple rounds advance exactly once per action`() = runTest {
        startReadyGame(rounds = 3)
        viewModel.onAction(GameAction.StartRounds)
        viewModel.onAction(GameAction.FinishRound)
        assertThat(viewModel.uiState.value.currentRound).isEqualTo(2)
        advanceUntilIdle()
        viewModel.onAction(GameAction.FinishRound)
        assertThat(viewModel.uiState.value.currentRound).isEqualTo(3)
        advanceUntilIdle()

        val voteNavigation = async(UnconfinedTestDispatcher(testScheduler)) { viewModel.effect.first() }
        viewModel.onAction(GameAction.FinishRound)
        assertThat(voteNavigation.await()).isEqualTo(GameEffect.NavigateToVote)
    }

    @Test
    fun `double start rounds emits one navigation`() = runTest {
        startReadyGame(rounds = 2)
        val effects = mutableListOf<GameEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.effect.toList(effects) }

        viewModel.onAction(GameAction.StartRounds)
        viewModel.onAction(GameAction.StartRounds)
        testScheduler.runCurrent()

        assertThat(viewModel.uiState.value.phase).isEqualTo(GamePhase.PLAYING_ROUNDS)
        assertThat(effects.count { it == GameEffect.NavigateToRound }).isEqualTo(1)
    }

    @Test
    fun `double finish round advances only one round`() = runTest {
        startReadyGame(rounds = 3)
        viewModel.onAction(GameAction.StartRounds)

        viewModel.onAction(GameAction.FinishRound)
        viewModel.onAction(GameAction.FinishRound)

        assertThat(viewModel.uiState.value.currentRound).isEqualTo(2)
        assertThat(viewModel.uiState.value.phase).isEqualTo(GamePhase.ADVANCING_ROUND)
        advanceUntilIdle()
        assertThat(viewModel.uiState.value.phase).isEqualTo(GamePhase.PLAYING_ROUNDS)
    }

    @Test
    fun `double finish on last round emits one vote navigation`() = runTest {
        startReadyGame(rounds = 1)
        viewModel.onAction(GameAction.StartRounds)
        val effects = mutableListOf<GameEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.effect.toList(effects) }

        viewModel.onAction(GameAction.FinishRound)
        viewModel.onAction(GameAction.FinishRound)
        testScheduler.runCurrent()

        assertThat(viewModel.uiState.value.phase).isEqualTo(GamePhase.VOTING)
        assertThat(effects.count { it == GameEffect.NavigateToVote }).isEqualTo(1)
    }

    @Test
    fun `finish round outside playing phase does nothing`() = runTest {
        viewModel.onAction(GameAction.FinishRound)
        assertThat(viewModel.uiState.value).isEqualTo(GameState())

        startReadyGame(rounds = 2)
        viewModel.onAction(GameAction.FinishRound)
        assertThat(viewModel.uiState.value.phase).isEqualTo(GamePhase.READY)
        assertThat(viewModel.uiState.value.currentRound).isEqualTo(1)
    }

    @Test
    fun `start rounds outside ready phase does nothing`() = runTest {
        viewModel.onAction(GameAction.StartRounds)
        assertThat(viewModel.uiState.value).isEqualTo(GameState())

        viewModel.onAction(startAction())
        advanceUntilIdle()
        viewModel.onAction(GameAction.StartRounds)
        assertThat(viewModel.uiState.value.phase).isEqualTo(GamePhase.REVEALING_ROLES)
    }

    @Test
    fun `cancelled start game cannot update state or navigate`() = runTest {
        val repositoryStarted = CompletableDeferred<Unit>()
        val releaseRepository = CompletableDeferred<Unit>()
        coEvery { repository.getWords(any(), any()) } coAnswers {
            repositoryStarted.complete(Unit)
            releaseRepository.await()
            WordsResult.Success(words)
        }
        val effects = mutableListOf<GameEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.effect.toList(effects) }

        viewModel.onAction(startAction())
        testScheduler.runCurrent()
        repositoryStarted.await()
        assertThat(viewModel.uiState.value.phase).isEqualTo(GamePhase.LOADING)

        viewModel.onAction(GameAction.CancelStartGame)
        releaseRepository.complete(Unit)
        advanceUntilIdle()

        assertThat(viewModel.uiState.value).isEqualTo(GameState())
        assertThat(effects).doesNotContain(GameEffect.NavigateToRevealRoles)
        assertThat(effects.none { it is GameEffect.ShowError }).isTrue()
    }

    @Test
    fun `reset keeps settings separate from game`() = runTest {
        viewModel.onAction(GameAction.SetHapticsEnabled(false))
        viewModel.onAction(startAction())
        advanceUntilIdle()
        viewModel.onAction(GameAction.ResetGame)

        assertThat(viewModel.uiState.value).isEqualTo(GameState())
        assertThat(viewModel.settings.value.hapticsEnabled).isFalse()
    }

    @Test
    fun `role reveal advances every player and reaches ready`() = runTest {
        viewModel.onAction(startAction())
        advanceUntilIdle()
        repeat(5) { index ->
            viewModel.onAction(GameAction.RevealRole)
            assertThat(viewModel.uiState.value.isRoleVisible).isTrue()
            viewModel.onAction(GameAction.HideRoleAndNext)
            if (index < 4) assertThat(viewModel.uiState.value.currentPlayerIndex).isEqualTo(index + 1)
        }
        assertThat(viewModel.uiState.value.isReadyToPlay).isTrue()
    }

    private suspend fun startReadyGame(rounds: Int) {
        viewModel.onAction(startAction(rounds = rounds))
        dispatcher.scheduler.advanceUntilIdle()
        repeat(5) {
            viewModel.onAction(GameAction.RevealRole)
            viewModel.onAction(GameAction.HideRoleAndNext)
        }
        dispatcher.scheduler.advanceUntilIdle()
    }

    private fun createViewModel(random: Random): GameViewModel {
        val strings = mockk<StringProvider>()
        every { strings.getString(any()) } answers {
            when (firstArg<Int>()) {
                com.masmultimedia.sospechapp.R.string.error_custom_word_required -> "Custom word required"
                com.masmultimedia.sospechapp.R.string.error_word_catalog_unavailable -> "Catalog unavailable"
                else -> "Invalid players"
            }
        }
        return GameViewModel(
            application = mockk<Application>(relaxed = true),
            stringProvider = strings,
            wordsRepository = repository,
            categoryHistoryPrefs = history,
            dispatcher = dispatcher,
            random = random,
        )
    }

    private fun startAction(
        players: Int = 5,
        impostors: Int = 1,
        rounds: Int = 1,
        useCustom: Boolean = false,
        customWord: String? = null,
        category: String? = null,
        difficulty: String? = null,
    ) = GameAction.StartGame(
        totalPlayers = players,
        impostors = impostors,
        rounds = rounds,
        useCustomWord = useCustom,
        wordInput = customWord,
        category = category,
        difficulty = difficulty,
    )

    private companion object {
        fun word(category: String, difficulty: String, text: String) = Word(
            id = "$category:$difficulty:$text",
            text = text,
            category = category,
            difficulty = difficulty,
        )
    }
}
