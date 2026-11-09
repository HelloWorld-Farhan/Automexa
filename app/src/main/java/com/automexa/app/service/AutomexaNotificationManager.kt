package com.automexa.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.automexa.app.ui.MainActivity

object AutomexaNotificationManager {

    const val CHANNEL_ID_LIVE = "automexa_live_channel"
    const val CHANNEL_ID_ALERTS = "automexa_alerts_channel"
    const val CHANNEL_ID_UPDATES = "automexa_updates_channel"
    const val NOTIFICATION_ID_FOREGROUND = 1001
    const val NOTIFICATION_ID_ALERT = 1002
    const val NOTIFICATION_ID_UPDATE = 1003

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
                description = "Notifies when a new version of Automexa is ready to install."
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

    fun sendHumanAlert(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val launchIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alert = NotificationCompat.Builder(context, CHANNEL_ID_ALERTS)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        notificationManager.notify(NOTIFICATION_ID_ALERT, alert)
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

        notificationManager.notify(NOTIFICATION_ID_UPDATE, notification)
    }
}
