package com.automexa.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.automexa.app.R
import com.automexa.app.ui.MainActivity

object AutomexaNotificationManager {

    const val CHANNEL_ID_LIVE = "automexa_live_channel"
    const val CHANNEL_ID_ALERTS = "automexa_alerts_channel"
    const val NOTIFICATION_ID_FOREGROUND = 1001
    const val NOTIFICATION_ID_ALERT = 1002

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
            }

            notificationManager.createNotificationChannel(liveChannel)
            notificationManager.createNotificationChannel(alertChannel)
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
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        notificationManager.notify(NOTIFICATION_ID_ALERT, alert)
    }
}
