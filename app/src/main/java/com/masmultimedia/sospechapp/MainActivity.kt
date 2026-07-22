package com.masmultimedia.sospechapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.masmultimedia.sospechapp.game.GameAction
import com.masmultimedia.sospechapp.game.GameEffect
import com.masmultimedia.sospechapp.game.GamePhase
import com.masmultimedia.sospechapp.game.GameViewModel
import com.masmultimedia.sospechapp.game.GameViewModelFactory
import com.masmultimedia.sospechapp.navigation.SospechAppDestination
import com.masmultimedia.sospechapp.ui.HowToPlayScreen
import com.masmultimedia.sospechapp.ui.components.LoadingOverlay
import com.masmultimedia.sospechapp.ui.components.LocalSospechSnackbarHostState
import com.masmultimedia.sospechapp.ui.gameconfig.GameConfigScreen
import com.masmultimedia.sospechapp.ui.menu.MainMenuScreen
import com.masmultimedia.sospechapp.ui.misc.MiscScreen
import com.masmultimedia.sospechapp.ui.ready.ReadyToPlayScreen
import com.masmultimedia.sospechapp.ui.revealroles.RevealRolesScreen
import com.masmultimedia.sospechapp.ui.settings.SettingsScreen
import com.masmultimedia.sospechapp.ui.splash.SplashScreen
import com.masmultimedia.sospechapp.ui.theme.SospechAppTheme
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : ComponentActivity() {

    private val gameViewModel: GameViewModel by viewModels {
        GameViewModelFactory(application)
    }
    private var canRequestAds by mutableStateOf(false)
    private val isMobileAdsInitialized = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestConsentAndInitAds()
        enableEdgeToEdge()
        setContent {
            SospechAppTheme {
                SospechApp(gameViewModel, showAds = canRequestAds)
            }
        }
    }

    private fun requestConsentAndInitAds() {
        val consentInformation = UserMessagingPlatform.getConsentInformation(this)
        val params = ConsentRequestParameters.Builder().build()

        consentInformation.requestConsentInfoUpdate(
            this,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(this) {
                    canRequestAds = consentInformation.canRequestAds()
                    if (canRequestAds) initializeMobileAdsSdk()
                }
            },
            {
                // If consent update fails, keep ads disabled to stay conservative.
                canRequestAds = false
            }
        )

        if (consentInformation.canRequestAds()) {
            canRequestAds = true
            initializeMobileAdsSdk()
        }
    }

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitialized.getAndSet(true)) return
        // List of test device IDs for AdMob test ads
        // Add here the IDs of your real devices and emulators as needed
        val testDeviceIds = listOf(
            "DAFB97D487DC64145FC55FC0DCD9EB67" // Samsung Galaxy A34
            // "EMULATOR_DEVICE_ID" // Add your emulator ID here after you get it from logcat
        )
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(testDeviceIds)
                .build()
        )
        MobileAds.initialize(this) {}
    }
}

@Composable
fun SospechApp(
    gameViewModel: GameViewModel,
    showAds: Boolean
) {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        SospechNavHost(
            navController = navController,
            gameViewModel = gameViewModel,
            showAds = showAds
        )
    }
}

@Composable
fun SospechNavHost(
    navController: NavHostController,
    gameViewModel: GameViewModel,
    showAds: Boolean
) {
    val state by gameViewModel.uiState.collectAsState()
    val settings by gameViewModel.settings.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val view = LocalView.current
    DisposableEffect(settings.keepScreenOn) {
        view.keepScreenOn = settings.keepScreenOn
        onDispose { view.keepScreenOn = false }
    }

    // Collect one-shot effects for navigation
    LaunchedEffect(Unit) {
        gameViewModel.effect.collect { effect ->
            when (effect) {
                GameEffect.NavigateToRevealRoles -> navController.navigatePhase(SospechAppDestination.RevealRoles)
                GameEffect.NavigateToReadyToPlay -> navController.navigatePhase(SospechAppDestination.ReadyToPlay)
                GameEffect.NavigateToRound -> navController.navigatePhase(SospechAppDestination.Rounds)
                GameEffect.NavigateToVote -> navController.navigatePhase(SospechAppDestination.Vote)
                is GameEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    CompositionLocalProvider(
        LocalSospechSnackbarHostState provides snackbarHostState
    ) {
        Box(Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = SospechAppDestination.Splash.route
            ) {
                composable(SospechAppDestination.Vote.route) {
                    val backToMenu = {
                        gameViewModel.onAction(GameAction.ResetGame)
                        navController.popBackStack(SospechAppDestination.MainMenu.route, inclusive = false)
                        Unit
                    }
                    BackHandler(onBack = backToMenu)
                    val impostorIndices = state.roles.withIndex()
                        .filter { it.value == com.masmultimedia.sospechapp.game.PlayerRole.IMPOSTOR }
                        .map { it.index }
                    com.masmultimedia.sospechapp.ui.vote.VoteScreen(
                        impostorIndices = impostorIndices,
                        onBackToMenu = backToMenu,
                    )
                }
                composable(SospechAppDestination.Splash.route) {
                    SplashScreen(
                        onTimeout = {
                            navController.navigate(SospechAppDestination.MainMenu.route) {
                                popUpTo(SospechAppDestination.Splash.route) {
                                    inclusive = true
                                }
                            }
                        }
                    )
                }

                composable(SospechAppDestination.MainMenu.route) {
                    MainMenuScreen(
                        onNewGameClick = { navController.navigate(SospechAppDestination.GameConfig.route) },
                        onHowToPlayClick = { navController.navigate(SospechAppDestination.HowToPlay.route) },
                        onSettingsClick = { navController.navigate(SospechAppDestination.Settings.route) },
                        onMiscClick = { navController.navigate(SospechAppDestination.Misc.route) },
                        showAds = showAds
                    )
                }

                composable(SospechAppDestination.GameConfig.route) {
                    val cancelAndGoBack = {
                        gameViewModel.onAction(GameAction.CancelStartGame)
                        navController.popBackStack()
                        Unit
                    }
                    BackHandler(onBack = cancelAndGoBack)
                    GameConfigScreen(
                        state = state,
                        onBackClick = cancelAndGoBack,
                        onAction = gameViewModel::onAction,
                        onStartGame = { totalPlayers, impostors, rounds, wordInput, category, difficulty ->
                            gameViewModel.onAction(
                                GameAction.StartGame(
                                    totalPlayers = totalPlayers,
                                    impostors = impostors,
                                    rounds = rounds,
                                    useCustomWord = state.useCustomWord,
                                    wordInput = wordInput,
                                    category = category,
                                    difficulty = difficulty
                                )
                            )
                        }
                    )
                }

                composable(SospechAppDestination.HowToPlay.route) {
                    HowToPlayScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable(SospechAppDestination.RevealRoles.route) {
                    BackHandler {
                        gameViewModel.onAction(GameAction.ResetGame)
                        navController.popBackStack(SospechAppDestination.MainMenu.route, inclusive = false)
                    }
                    RevealRolesScreen(
                        state = state,
                        settings = settings,
                        onRevealRole = { gameViewModel.onAction(GameAction.RevealRole) },
                        onHideAndNext = { gameViewModel.onAction(GameAction.HideRoleAndNext) }
                    )
                }

                composable(SospechAppDestination.ReadyToPlay.route) {
                    val backToMenu = {
                        gameViewModel.onAction(GameAction.ResetGame)
                        navController.popBackStack(SospechAppDestination.MainMenu.route, inclusive = false)
                        Unit
                    }
                    BackHandler(onBack = backToMenu)
                    ReadyToPlayScreen(
                        onBackToMenu = backToMenu,
                        showAds = showAds,
                        onStartRounds = { gameViewModel.onAction(GameAction.StartRounds) }
                    )
                }

                composable(SospechAppDestination.Rounds.route) {
                    BackHandler {
                        gameViewModel.onAction(GameAction.ResetGame)
                        navController.popBackStack(SospechAppDestination.MainMenu.route, inclusive = false)
                    }
                    val currentRound = state.currentRound
                    val totalRounds = state.rounds
                    com.masmultimedia.sospechapp.ui.rounds.RoundsScreen(
                        currentRound = currentRound,
                        totalRounds = totalRounds,
                        enabled = state.phase == GamePhase.PLAYING_ROUNDS,
                        onNext = { gameViewModel.onAction(GameAction.FinishRound) }
                    )
                }

                composable(SospechAppDestination.Settings.route) {
                    SettingsScreen(
                        settings = settings,
                        onBackClick = { navController.popBackStack() },
                        onAction = gameViewModel::onAction
                    )
                }

                composable(SospechAppDestination.Misc.route) {
                    MiscScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }

            }

            if (state.isLoading) {
                LoadingOverlay(
                    text = stringResource(R.string.loading_word)
                )
            }
        }
    }
}

private fun NavHostController.navigatePhase(destination: SospechAppDestination) {
    navigate(destination.route) {
        popUpTo(SospechAppDestination.MainMenu.route) { inclusive = false }
        launchSingleTop = true
    }
}
