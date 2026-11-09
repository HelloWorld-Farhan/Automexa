package com.automexa.app.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.content.Intent
import android.graphics.Path
import android.os.Bundle
import android.os.PowerManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.automexa.app.data.local.AutomexaDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AutomexaAccessibilityService : AccessibilityService() {

    private val scope = CoroutineScope(Dispatchers.IO)
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate() {
        super.onCreate()
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        @Suppress("DEPRECATION")
        wakeLock = powerManager.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or
                    PowerManager.ACQUIRE_CAUSES_WAKEUP or
                    PowerManager.ON_AFTER_RELEASE,
            "Automexa:AccessibilityWakeLock"
        )
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val rootNode = rootInActiveWindow ?: return

        scope.launch {
            handleWindowContent(rootNode, event.packageName?.toString() ?: "")
        }
    }

    private suspend fun handleWindowContent(rootNode: AccessibilityNodeInfo, packageName: String) {
        val database = AutomexaDatabase.getDatabase(applicationContext)
        val userProfile = database.userProfileDao().getUserProfileSync()

        // 1. Detect and click "Easy Apply" or "Apply" button
        val easyApplyButtons = rootNode.findAccessibilityNodeInfosByText("Easy Apply")
        if (easyApplyButtons.isNotEmpty()) {
            easyApplyButtons[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
            return
        }

        // 2. Scan and fill form inputs
        if (userProfile != null) {
            scanAndFillNodes(rootNode, userProfile)
        }
    }

    private fun scanAndFillNodes(
        node: AccessibilityNodeInfo,
        profile: com.automexa.app.data.local.entities.UserProfileEntity
    ) {
        if (node.isEditable) {
            val hint = node.hintText?.toString() ?: ""
            val desc = node.contentDescription?.toString() ?: ""
            val label = "$hint $desc".lowercase()

            val textToFill = when {
                label.contains("phone") || label.contains("mobile") -> profile.phone
                label.contains("experience") || label.contains("years") -> profile.totalExperienceYears.toString()
                label.contains("notice") -> profile.noticePeriodDays.toString()
                label.contains("expected") && (label.contains("ctc") || label.contains("salary")) -> profile.expectedCtc
                label.contains("current") && (label.contains("ctc") || label.contains("salary")) -> profile.currentCtc
                else -> null
            }

            textToFill?.let { text ->
                val arguments = Bundle().apply {
                    putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
                }
                node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
            }
        }

        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { scanAndFillNodes(it, profile) }
        }
    }

    /**
     * Wakes the display and executes a vertical swipe up on the keyguard (SKEDit unlock pattern).
     */
    fun wakeAndUnlockSwipe() {
        wakeLock?.acquire(3000)
        val swipePath = Path().apply {
            moveTo(540f, 1800f)
            lineTo(540f, 400f)
        }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(swipePath, 0, 300))
            .build()

        dispatchGesture(gesture, null, null)
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        wakeLock?.let {
            if (it.isHeld) it.release()
        }
    }

    companion object {
        var instance: AutomexaAccessibilityService? = null
            private set
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }
}

