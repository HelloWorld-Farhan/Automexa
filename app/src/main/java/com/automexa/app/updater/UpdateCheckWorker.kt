package com.automexa.app.updater

import android.content.Context
import androidx.work.*
import com.automexa.app.service.AutomexaNotificationManager
import java.util.concurrent.TimeUnit

class UpdateCheckWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val updateInfo = AppUpdateManager.checkForUpdates()
            if (updateInfo.hasUpdate) {
                AutomexaNotificationManager.sendUpdateNotification(
                    context,
                    updateInfo.latestVersion,
                    updateInfo.releaseNotes
                )
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "AutomexaPeriodicUpdateCheck"

        fun schedulePeriodicCheck(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<UpdateCheckWorker>(6, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}

