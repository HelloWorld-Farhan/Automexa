package com.automexa.app.updater

import android.content.Context
import androidx.work.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class RenderKeepAliveWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    override suspend fun doWork(): Result {
        return try {
            val request = Request.Builder()
                .url(RENDER_HEALTH_URL)
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        const val RENDER_HEALTH_URL = "https://automexa-backend.onrender.com/health"
        private const val KEEP_ALIVE_WORK_NAME = "AutomexaRenderKeepAlive"

        fun scheduleKeepAlive(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            // Ping Render every 14 minutes to prevent free instance sleep
            val request = PeriodicWorkRequestBuilder<RenderKeepAliveWorker>(14, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                KEEP_ALIVE_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}

