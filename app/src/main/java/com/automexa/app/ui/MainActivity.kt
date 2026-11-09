package com.automexa.app.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
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

                // Runtime Notification Permission for Android 13+ (API 33+)
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    // Notification permission handled
                }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED

                        if (!hasPermission) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    AnimatedContent(
                        targetState = currentScreen,
                        label = "ScreenTransition",
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(220, easing = FastOutSlowInEasing)) +
                                    scaleIn(
                                        initialScale = 0.97f,
                                        animationSpec = tween(220, easing = FastOutSlowInEasing)
                                    )) togetherWith
                                    fadeOut(animationSpec = tween(180, easing = FastOutSlowInEasing))
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
