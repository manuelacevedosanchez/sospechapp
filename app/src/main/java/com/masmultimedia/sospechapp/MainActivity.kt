package com.masmultimedia.sospechapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.masmultimedia.sospechapp.game.GameAction
import com.masmultimedia.sospechapp.game.GameEffect
import com.masmultimedia.sospechapp.game.GameViewModel
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

class MainActivity : ComponentActivity() {

    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SospechAppTheme {
                SospechApp(gameViewModel)
            }
        }
    }
}

@Composable
fun SospechApp(gameViewModel: GameViewModel) {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        SospechNavHost(
            navController = navController,
            gameViewModel = gameViewModel
        )
    }
}

@Composable
fun SospechNavHost(
    navController: NavHostController,
    gameViewModel: GameViewModel
) {
    val state by gameViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val view = LocalView.current
    DisposableEffect(state.settings.keepScreenOn) {
        view.keepScreenOn = state.settings.keepScreenOn
        onDispose { view.keepScreenOn = false }
    }

    // Collect one-shot effects for navigation
    LaunchedEffect(Unit) {
        gameViewModel.effect.collect { effect ->
            when (effect) {
                GameEffect.NavigateToRevealRoles -> {
                    navController.navigate(SospechAppDestination.RevealRoles.route)
                }

                GameEffect.NavigateToReadyToPlay -> {
                    navController.navigate(SospechAppDestination.ReadyToPlay.route)
                }

                is GameEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
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
                        onMiscClick = { navController.navigate(SospechAppDestination.Misc.route) }
                    )
                }

                composable(SospechAppDestination.GameConfig.route) {
                    GameConfigScreen(
                        onBackClick = { navController.popBackStack() },
                        onStartGame = { totalPlayers, impostors, wordInput ->
                            gameViewModel.onAction(
                                GameAction.StartGame(
                                    totalPlayers = totalPlayers,
                                    impostors = impostors,
                                    wordInput = wordInput
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
                    RevealRolesScreen(
                        state = state,
                        onRevealRole = { gameViewModel.onAction(GameAction.RevealRole) },
                        onHideAndNext = { gameViewModel.onAction(GameAction.HideRoleAndNext) }
                    )
                }

                composable(SospechAppDestination.ReadyToPlay.route) {
                    ReadyToPlayScreen(
                        onBackToMenu = {
                            gameViewModel.onAction(GameAction.ResetGame)
                            navController.popBackStack(
                                SospechAppDestination.MainMenu.route,
                                inclusive = false
                            )
                        }
                    )
                }

                composable(SospechAppDestination.Settings.route) {
                    SettingsScreen(
                        state = state,
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
                    text = "Cargando palabra..."
                )
            }
        }
    }
}