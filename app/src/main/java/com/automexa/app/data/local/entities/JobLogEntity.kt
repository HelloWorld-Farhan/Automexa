package com.automexa.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "job_application_logs")
data class JobLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val jobTitle: String,
    val companyName: String,
    val platform: String, // "LinkedIn", "Naukri", "Indeed"
    val appliedTimestamp: Long = System.currentTimeMillis(),
    val status: String, // "Applied", "Needs Review", "Skipped", "Failed"
    val notes: String = ""
)

