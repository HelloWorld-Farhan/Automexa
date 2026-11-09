package com.automexa.app.data.auth

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

data class AuthResult(
    val success: Boolean,
    val message: String,
    val isLocked: Boolean = false,
    val email: String? = null,
    val fullName: String? = null
)

class AuthManager(private val context: Context) {

    private val prefs = context.getSharedPreferences("automexa_auth_prefs", Context.MODE_PRIVATE)
    private val client = OkHttpClient.Builder()
        .followRedirects(true)
        .followSslRedirects(true)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    companion object {
        const val SCRIPT_URL = "https://script.google.com/macros/s/AKfycbzIE3eD2wi_pZky6GThxQtdpsJKVXPg4Q5DSC1SSQcsKrxUdjtT3vvr01H2L8znyyDM/exec"
        const val MAX_FAILED_ATTEMPTS = 3
    }

    var isLoggedIn: Boolean
        get() = prefs.getBoolean("is_logged_in", false)
        set(value) = prefs.edit().putBoolean("is_logged_in", value).apply()

    var userEmail: String?
        get() = prefs.getString("user_email", null)
        set(value) = prefs.edit().putString("user_email", value).apply()

    var userFullName: String?
        get() = prefs.getString("user_full_name", "Farhan Khalid")
        set(value) = prefs.edit().putString("user_full_name", value).apply()

    var failedAttempts: Int
        get() = prefs.getInt("failed_attempts", 0)
        set(value) = prefs.edit().putInt("failed_attempts", value).apply()

    val isLocked: Boolean
        get() = failedAttempts >= MAX_FAILED_ATTEMPTS

    suspend fun login(email: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        if (isLocked) {
            return@withContext AuthResult(
                success = false,
                message = "Account locked due to 3 incorrect attempts. Please verify via OTP to reset your password.",
                isLocked = true
            )
        }

        val jsonPayload = JsonObject().apply {
            addProperty("action", "login")
            addProperty("email", email.trim())
            addProperty("password", password)
        }

        val responseJson = postToScript(jsonPayload)

        if (responseJson != null && responseJson.get("status")?.asString == "success") {
            failedAttempts = 0
            isLoggedIn = true
            userEmail = email.trim()
            userFullName = responseJson.get("fullName")?.asString ?: "Candidate"
            AuthResult(success = true, message = "Login successful", email = userEmail, fullName = userFullName)
        } else {
            failedAttempts++
            val lockedNow = failedAttempts >= MAX_FAILED_ATTEMPTS
            val msg = if (lockedNow) {
                "3 incorrect attempts detected! For your security, account is locked. Please reset password with OTP."
            } else {
                "Incorrect password. Attempt ${failedAttempts} of $MAX_FAILED_ATTEMPTS."
            }
            AuthResult(success = false, message = msg, isLocked = lockedNow)
        }
    }

    suspend fun signup(fullName: String, email: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        val jsonPayload = JsonObject().apply {
            addProperty("action", "signup")
            addProperty("fullName", fullName.trim())
            addProperty("email", email.trim())
            addProperty("password", password)
        }

        val responseJson = postToScript(jsonPayload)

        if (responseJson != null && responseJson.get("status")?.asString == "success") {
            AuthResult(success = true, message = "Account created successfully! Please verify your email.")
        } else {
            val msg = responseJson?.get("message")?.asString ?: "Unable to create account. Please try again."
            AuthResult(success = false, message = msg)
        }
    }

    suspend fun sendOtp(email: String, type: String = "Verification"): AuthResult = withContext(Dispatchers.IO) {
        val jsonPayload = JsonObject().apply {
            addProperty("action", "sendOtp")
            addProperty("email", email.trim())
            addProperty("type", type)
        }

        val responseJson = postToScript(jsonPayload)
        val success = responseJson != null && responseJson.get("status")?.asString == "success"
        val msg = responseJson?.get("message")?.asString ?: if (success) "OTP sent successfully" else "Failed to send OTP"
        AuthResult(success = success, message = msg)
    }

    suspend fun verifyOtp(email: String, otp: String): AuthResult = withContext(Dispatchers.IO) {
        val jsonPayload = JsonObject().apply {
            addProperty("action", "verifyOtp")
            addProperty("email", email.trim())
            addProperty("otp", otp.trim())
        }

        val responseJson = postToScript(jsonPayload)
        val success = responseJson != null && responseJson.get("status")?.asString == "success"
        val msg = responseJson?.get("message")?.asString ?: if (success) "OTP verified" else "Invalid or expired OTP"
        AuthResult(success = success, message = msg)
    }

    suspend fun resetPassword(email: String, newPassword: String, otp: String): AuthResult = withContext(Dispatchers.IO) {
        val jsonPayload = JsonObject().apply {
            addProperty("action", "resetPassword")
            addProperty("email", email.trim())
            addProperty("newPassword", newPassword)
            addProperty("otp", otp.trim())
        }

        val responseJson = postToScript(jsonPayload)
        val success = responseJson != null && responseJson.get("status")?.asString == "success"
        if (success) {
            failedAttempts = 0 // Unlock account on successful reset
        }
        val msg = responseJson?.get("message")?.asString ?: if (success) "Password updated" else "Password reset failed"
        AuthResult(success = success, message = msg)
    }

    fun logout() {
        isLoggedIn = false
        prefs.edit().remove("user_email").apply()
    }

    private fun postToScript(jsonPayload: JsonObject): JsonObject? {
        return try {
            val body = jsonPayload.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url(SCRIPT_URL)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: return null
            JsonParser.parseString(responseString).asJsonObject
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

