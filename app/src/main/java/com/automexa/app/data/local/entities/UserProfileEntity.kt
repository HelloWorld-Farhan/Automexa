package com.automexa.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val fullName: String = "Farhan Khalid",
    val email: String = "farhankhalid17968@gmail.com",
    val phone: String = "+91 9876543210",
    val location: String = "India",
    val linkedInProfileUrl: String = "https://www.linkedin.com/in/farhan-khalid-117514259/",
    val githubUrl: String = "https://github.com/HelloWorld-Farhan",
    val totalExperienceYears: Int = 4,
    val currentCtc: String = "12 LPA",
    val expectedCtc: String = "18 LPA",
    val noticePeriodDays: Int = 30,
    val requiresVisaSponsorship: Boolean = false,
    val authorizedToWork: Boolean = true,
    val primarySkills: String = "Kotlin, Android SDK, Jetpack Compose, Python, RAG, Automation",
    val resumeSummary: String = "Senior Mobile and Automation Software Engineer specializing in scalable native architecture, LLMs, and autonomous services."
)
