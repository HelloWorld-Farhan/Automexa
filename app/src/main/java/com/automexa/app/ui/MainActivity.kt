package com.automexa.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.automexa.app.ui.dashboard.DashboardScreen
import com.automexa.app.ui.intro.VideoIntroScreen
import com.automexa.app.ui.theme.AutomexaTheme

enum class AppNavState {
    INTRO_VIDEO,
    DASHBOARD
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AutomexaTheme {
                var currentScreen by remember { mutableStateOf(AppNavState.INTRO_VIDEO) }

                Surface(modifier = Modifier.fillMaxSize()) {
                    Crossfade(
                        targetState = currentScreen,
                        label = "ScreenTransition"
                    ) { screen ->
                        when (screen) {
                            AppNavState.INTRO_VIDEO -> {
                                VideoIntroScreen(
                                    onSkipOrFinish = {
                                        currentScreen = AppNavState.DASHBOARD
                                    }
                                )
                            }
                            AppNavState.DASHBOARD -> {
                                DashboardScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}
