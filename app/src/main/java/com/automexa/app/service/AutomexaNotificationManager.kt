package com.automexa.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.automexa.app.ui.MainActivity
import java.io.File

object AutomexaNotificationManager {

    const val CHANNEL_ID_LIVE = "automexa_live_channel"
    const val CHANNEL_ID_ALERTS = "automexa_alerts_channel"
    const val CHANNEL_ID_UPDATES = "automexa_updates_channel"
    const val NOTIFICATION_ID_FOREGROUND = 1001
    const val NOTIFICATION_ID_ALERT = 1002
    const val NOTIFICATION_ID_UPDATE_PROGRESS = 1003
    const val NOTIFICATION_ID_WELCOME = 1004

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Live progress channel
            val liveChannel = NotificationChannel(
                CHANNEL_ID_LIVE,
                "Automexa Live Engine",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows live status while Automexa is actively applying to jobs."
                setShowBadge(false)
            }

            // High priority alerts channel (e.g. CAPTCHA / review alerts)
            val alertChannel = NotificationChannel(
                CHANNEL_ID_ALERTS,
                "Automexa Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Urgent alerts when human interaction or manual review is required."
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
            }

            // In-App Updates channel
            val updateChannel = NotificationChannel(
                CHANNEL_ID_UPDATES,
                "Automexa Updates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies when a new version of Automexa is downloading or ready to install."
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
            }

            notificationManager.createNotificationChannel(liveChannel)
            notificationManager.createNotificationChannel(alertChannel)
            notificationManager.createNotificationChannel(updateChannel)
        }
    }

    fun buildForegroundNotification(
        context: Context,
        title: String = "Automexa Active",
        content: String = "Monitoring & applying to target roles..."
    ): NotificationCompat.Builder {
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_ID_LIVE)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_menu_send)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
    }

    fun sendWelcomeNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            10,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val welcomeText = "Your autonomous career engine is active and ready. Tap to configure target job portals."

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_ALERTS)
            .setContentTitle("⚡ Welcome to Automexa")
            .setContentText(welcomeText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(welcomeText))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        notificationManager.notify(NOTIFICATION_ID_WELCOME, notification)
    }

    fun showDownloadProgressNotification(context: Context, progressPercent: Int, version: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_UPDATES)
            .setContentTitle("⚡ Automexa Downloading v$version")
            .setContentText("Downloading package: $progressPercent%")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setProgress(100, progressPercent, false)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(NOTIFICATION_ID_UPDATE_PROGRESS, notification)
    }

    fun showDownloadCompleteNotification(context: Context, apkFile: File, version: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val apkUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            apkFile
        )
        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            11,
            installIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_UPDATES)
            .setContentTitle("✅ Automexa v$version Download Complete")
            .setContentText("Tap here to complete installation and update the app.")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Update downloaded successfully. Tap to install Automexa v$version."))
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setOngoing(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        notificationManager.notify(NOTIFICATION_ID_UPDATE_PROGRESS, notification)
    }

    fun sendUpdateNotification(context: Context, newVersion: String, releaseNotes: String?) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("OPEN_UPDATE_DIALOG", true)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            2,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val summaryText = releaseNotes?.take(250)
            ?: "Automexa v$newVersion is ready. Tap to download and install with one touch."

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_UPDATES)
            .setContentTitle("⚡ Automexa Update Ready (v$newVersion)")
            .setContentText("Tap to install the latest build with improved speed.")
            .setStyle(NotificationCompat.BigTextStyle().bigText(summaryText))
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        notificationManager.notify(NOTIFICATION_ID_UPDATE_PROGRESS, notification)
    }
}
