package com.automexa.app.ui

import android.Manifest
import android.content.Context
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
import com.automexa.app.data.auth.AuthManager
import com.automexa.app.service.AutomexaNotificationManager
import com.automexa.app.ui.auth.AuthScreen
import com.automexa.app.ui.dashboard.DashboardScreen
import com.automexa.app.ui.intro.VideoIntroScreen
import com.automexa.app.ui.theme.AutomexaTheme

enum class AppNavState {
    INTRO_VIDEO,
    AUTH,
    DASHBOARD
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AutomexaTheme {
                val authManager = remember { AuthManager(this@MainActivity) }
                var currentScreen by remember { mutableStateOf(AppNavState.INTRO_VIDEO) }

                // Runtime Notification Permission for Android 13+ (API 33+)
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted) {
                        triggerWelcomeIfFirstTime()
                    }
                }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED

                        if (!hasPermission) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            triggerWelcomeIfFirstTime()
                        }
                    } else {
                        triggerWelcomeIfFirstTime()
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
                                        currentScreen = if (authManager.isLoggedIn) {
                                            AppNavState.DASHBOARD
                                        } else {
                                            AppNavState.AUTH
                                        }
                                    }
                                )
                            }
                            AppNavState.AUTH -> {
                                AuthScreen(
                                    onAuthSuccess = {
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

    private fun triggerWelcomeIfFirstTime() {
        val prefs = getSharedPreferences("automexa_prefs", Context.MODE_PRIVATE)
        val hasSent = prefs.getBoolean("has_sent_welcome", false)
        if (!hasSent) {
            AutomexaNotificationManager.sendWelcomeNotification(this)
            prefs.edit().putBoolean("has_sent_welcome", true).apply()
        }
    }
}
