package com.masmultimedia.sospechapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.masmultimedia.sospechapp.navigation.SospechAppDestination
import com.masmultimedia.sospechapp.ui.menu.MainMenuScreen
import com.masmultimedia.sospechapp.ui.splash.SplashScreen
import com.masmultimedia.sospechapp.ui.theme.SospechAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SospechAppTheme {
                SospechApp()
            }
        }
    }
}

@Composable
fun SospechApp() {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        SospechNavHost(navController = navController)
    }
}

@Composable
fun SospechNavHost(navController: NavHostController) {
    NavHost(
        navController = navController, startDestination = SospechAppDestination.Splash.route
    ) {
        composable(SospechAppDestination.Splash.route) {
            SplashScreen(
                onTimeout = {
                    navController.navigate(SospechAppDestination.MainMenu.route) {
                        popUpTo(SospechAppDestination.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(SospechAppDestination.MainMenu.route) {
            MainMenuScreen(
                onNewGameClick = {
                    // Navegar a la pantalla de configuración del juego cuando esté implementada
                    // navController.navigate(SospechAppDestination.GameConfig.route)
                },
                onHowToPlayClick = {
                    // Navegar a la pantalla de instrucciones cuando esté implementada
                    // navController.navigate(SospechAppDestination.HowToPlay.route)
                }
            )
        }
    }
}