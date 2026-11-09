package com.automexa.app.updater

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.automexa.app.BuildConfig
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

data class UpdateInfo(
    val hasUpdate: Boolean,
    val latestVersion: String,
    val downloadUrl: String?,
    val releaseNotes: String?
)

object AppUpdateManager {

    private const val GITHUB_REPO = "HelloWorld-Farhan/Automexa"
    private const val RELEASES_API_URL = "https://api.github.com/repos/$GITHUB_REPO/releases/latest"

    private val httpClient = OkHttpClient()

    suspend fun checkForUpdates(): UpdateInfo = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(RELEASES_API_URL)
                .header("Accept", "application/vnd.github.v3+json")
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext UpdateInfo(false, BuildConfig.VERSION_NAME, null, null)
            }

            val body = response.body?.string() ?: return@withContext UpdateInfo(false, BuildConfig.VERSION_NAME, null, null)
            val jsonObject = JsonParser.parseString(body).asJsonObject

            val tagName = jsonObject.get("tag_name")?.asString?.removePrefix("v") ?: BuildConfig.VERSION_NAME
            val releaseNotes = jsonObject.get("body")?.asString

            var apkDownloadUrl: String? = null
            val assets = jsonObject.getAsJsonArray("assets")
            if (assets != null) {
                for (assetElement in assets) {
                    val asset = assetElement.asJsonObject
                    val name = asset.get("name")?.asString ?: ""
                    if (name.endsWith(".apk", ignoreCase = true)) {
                        apkDownloadUrl = asset.get("browser_download_url")?.asString
                        break
                    }
                }
            }

            val isNewer = isVersionNewer(tagName, BuildConfig.VERSION_NAME)
            UpdateInfo(
                hasUpdate = isNewer && apkDownloadUrl != null,
                latestVersion = tagName,
                downloadUrl = apkDownloadUrl,
                releaseNotes = releaseNotes
            )
        } catch (e: Exception) {
            e.printStackTrace()
            UpdateInfo(false, BuildConfig.VERSION_NAME, null, null)
        }
    }

    private fun isVersionNewer(remote: String, current: String): Boolean {
        val remoteParts = remote.split(".").mapNotNull { it.toIntOrNull() }
        val currentParts = current.split(".").mapNotNull { it.toIntOrNull() }

        val length = maxOf(remoteParts.size, currentParts.size)
        for (i in 0 until length) {
            val r = remoteParts.getOrElse(i) { 0 }
            val c = currentParts.getOrElse(i) { 0 }
            if (r > c) return true
            if (r < c) return false
        }
        return false
    }

    fun downloadAndInstall(context: Context, downloadUrl: String, version: String) {
        val destination = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "Automexa-v$version.apk")
        if (destination.exists()) destination.delete()

        val request = DownloadManager.Request(Uri.parse(downloadUrl))
            .setTitle("Automexa Update v$version")
            .setDescription("Downloading latest release...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationUri(Uri.fromFile(destination))

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)

        // Register receiver for download complete
        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) ?: -1
                if (id == downloadId) {
                    installApk(context, destination)
                    try {
                        context.unregisterReceiver(this)
                    } catch (_: Exception) {}
                }
            }
        }

        context.registerReceiver(
            onComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            Context.RECEIVER_NOT_EXPORTED
        )
    }

    private fun installApk(context: Context, apkFile: File) {
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
        context.startActivity(installIntent)
    }
}
