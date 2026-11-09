package com.automexa.app

import android.app.Application
import com.automexa.app.data.local.AutomexaDatabase
import com.automexa.app.service.AutomexaNotificationManager

class AutomexaApplication : Application() {

    val database: AutomexaDatabase by lazy {
        AutomexaDatabase.getDatabase(this)
    }

    override fun onCreate() {
        super.onCreate()
        // Initialize notification channels
        AutomexaNotificationManager.createNotificationChannels(this)
    }
}
