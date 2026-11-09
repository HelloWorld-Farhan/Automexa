package com.automexa.app.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.automexa.app.BuildConfig
import com.automexa.app.service.AutomexaForegroundService
import com.automexa.app.service.AutomexaNotificationManager
import com.automexa.app.ui.theme.*
import com.automexa.app.updater.AppUpdateManager
import com.automexa.app.updater.UpdateInfo
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var selectedCategory by remember { mutableStateOf("All platforms") }
    var isLinkedInActive by remember { mutableStateOf(true) }
    var isNaukriActive by remember { mutableStateOf(false) }
    var isEngineRunning by remember { mutableStateOf(false) }

    // In-App Update state
    var showUpdateDialog by remember { mutableStateOf(false) }
    var latestUpdateInfo by remember { mutableStateOf<UpdateInfo?>(null) }
    var isCheckingUpdates by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableFloatStateOf(0f) }
    var isDownloading by remember { mutableStateOf(false) }
    var updateStatusMessage by remember { mutableStateOf<String?>(null) }

    val currentDateStr = remember {
        val sdf = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
        sdf.format(Date())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Clean Header (Real Android Status bar seamlessly blends above)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Morning, Farhan!",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary
            )

            // Minimalist real-time engine status pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isEngineRunning) GreenSwitch else PillInactive)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isEngineRunning) "ACTIVE" else "READY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isEngineRunning) Color(0xFF1D3514) else TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Top Summary Section: 2 Columns
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(184.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left Card: Jobs Applied Today
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(28.dp),
                        spotColor = Color(0x0A000000)
                    )
                    .clip(RoundedCornerShape(28.dp))
                    .background(CardBackground)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Jobs\napplied",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 21.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isEngineRunning) "18" else "0",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Normal,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = currentDateStr,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            // Right 2x2 Quick Action Scenes Grid
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionSceneButton(
                        icon = Icons.Default.WbSunny,
                        label = "Wake up",
                        bgColor = SceneBlue,
                        textColor = Color(0xFF2A3036),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            isEngineRunning = true
                            AutomexaForegroundService.start(context)
                        }
                    )
                    QuickActionSceneButton(
                        icon = Icons.Default.Bedtime,
                        label = "Sleep",
                        bgColor = ScenePurple,
                        textColor = Color(0xFF30283C),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            isEngineRunning = false
                            AutomexaForegroundService.stop(context)
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionSceneButton(
                        icon = Icons.Default.Bolt,
                        label = "Turbo",
                        bgColor = SceneTeal,
                        textColor = Color(0xFF243538),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            isLinkedInActive = true
                            isNaukriActive = true
                        }
                    )
                    QuickActionSceneButton(
                        icon = Icons.Default.Coffee,
                        label = "Reset",
                        bgColor = ScenePink,
                        textColor = Color(0xFF3F2B2E),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            // Reset state
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Horizontally Scrollable Category Pills
        val categories = listOf("All platforms", "LinkedIn", "Naukri", "Indeed")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                val isSelected = selectedCategory == category
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isSelected) AccentDark else PillInactive)
                        .clickable { selectedCategory = category }
                        .padding(horizontal = 16.dp, vertical = 9.dp)
                ) {
                    Text(
                        text = category,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color.White else TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Two-Column Grid of Target Platforms
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Platform Card 1: LinkedIn
            PlatformDeviceCard(
                title = "LinkedIn",
                statusBadge = "to launch 00:05:11",
                isActive = isLinkedInActive,
                onToggle = { isLinkedInActive = !isLinkedInActive },
                icon = Icons.Default.WorkOutline,
                modifier = Modifier.weight(1f)
            )

            // Platform Card 2: Naukri
            PlatformDeviceCard(
                title = "Naukri",
                statusBadge = if (isNaukriActive) "engine ready" else "standby",
                isActive = isNaukriActive,
                onToggle = { isNaukriActive = !isNaukriActive },
                icon = Icons.Default.Apartment,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // In-App Self-Updater Card
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Automexa Version",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Normal,
                            color = TextPrimary
                        )
                        Text(
                            text = "v${BuildConfig.VERSION_NAME} (Current)",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Button(
                        onClick = {
                            isCheckingUpdates = true
                            updateStatusMessage = "Checking GitHub releases..."
                            coroutineScope.launch {
                                val info = AppUpdateManager.checkForUpdates()
                                latestUpdateInfo = info
                                isCheckingUpdates = false

                                if (info.hasUpdate) {
                                    showUpdateDialog = true
                                    AutomexaNotificationManager.sendUpdateNotification(
                                        context,
                                        info.latestVersion,
                                        info.releaseNotes
                                    )
                                } else {
                                    updateStatusMessage = "You're running the latest version (v${BuildConfig.VERSION_NAME})."
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentDark,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !isCheckingUpdates
                    ) {
                        if (isCheckingUpdates) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Check Update",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                updateStatusMessage?.let { msg ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = msg,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

    // Modern In-App Update Modal Popup (Matching code.html design)
    if (showUpdateDialog) {
        Dialog(onDismissRequest = { if (!isDownloading) showUpdateDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(CardBackground)
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Update Icon with soft accent circle
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(SceneTeal),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SystemUpdateAlt,
                            contentDescription = "Update",
                            tint = Color(0xFF1D3514),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "New Version Available",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Automexa v${latestUpdateInfo?.latestVersion ?: "1.0.1"}",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Release notes card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(AppBackground)
                            .padding(14.dp)
                    ) {
                        Text(
                            text = latestUpdateInfo?.releaseNotes?.take(200) ?: "Includes performance enhancements, improved form fill logic, and background automation stability.",
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Download Progress Animation
                    if (isDownloading) {
                        val animatedProgress by animateFloatAsState(
                            targetValue = downloadProgress,
                            label = "downloadProgress"
                        )
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            LinearProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = GreenSwitch,
                                trackColor = PillInactive
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Downloading: ${(downloadProgress * 100).toInt()}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                        }
                    } else {
                        // Action Buttons: Cancel or Update Now
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            TextButton(
                                onClick = { showUpdateDialog = false },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
                            ) {
                                Text("Later", fontSize = 13.sp)
                            }

                            Button(
                                onClick = {
                                    latestUpdateInfo?.downloadUrl?.let { url ->
                                        AppUpdateManager.checkAndRequestInstallPermission(context)
                                        isDownloading = true
                                        coroutineScope.launch {
                                            AppUpdateManager.downloadWithProgress(
                                                context,
                                                url,
                                                latestUpdateInfo?.latestVersion ?: "latest"
                                            ) { progress ->
                                                downloadProgress = progress
                                            }
                                            isDownloading = false
                                            showUpdateDialog = false
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1.5f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AccentDark,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Update Now", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionSceneButton(
    icon: ImageVector,
    label: String,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }
}

@Composable
fun PlatformDeviceCard(
    title: String,
    statusBadge: String,
    isActive: Boolean,
    onToggle: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(180.dp)
            .shadow(2.dp, RoundedCornerShape(28.dp), spotColor = Color(0x0A000000))
            .clip(RoundedCornerShape(28.dp))
            .background(CardBackground)
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PillInactive)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = statusBadge,
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Power Toggle Button matching code.html greenSwitch
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isActive) GreenSwitch else PillInactive)
                        .clickable { onToggle() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = "Toggle",
                        tint = if (isActive) Color(0xFF1D3514) else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0x1F000000),
                    modifier = Modifier.size(52.dp)
                )
            }
        }
    }
}
