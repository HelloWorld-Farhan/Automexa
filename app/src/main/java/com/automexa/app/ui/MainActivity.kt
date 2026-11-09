package com.automexa.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
                    AnimatedContent(
                        targetState = currentScreen,
                        label = "ScreenTransition",
                        transitionSpec = {
                            fadeIn(animationSpec = tween(500)) + scaleIn(
                                initialScale = 0.96f,
                                animationSpec = tween(500)
                            ) togetherWith fadeOut(animationSpec = tween(400))
                        }
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
