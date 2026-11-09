package com.automexa.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.automexa.app.data.local.dao.JobLogDao
import com.automexa.app.data.local.dao.UserProfileDao
import com.automexa.app.data.local.entities.JobLogEntity
import com.automexa.app.data.local.entities.UserProfileEntity

@Database(
    entities = [UserProfileEntity::class, JobLogEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AutomexaDatabase : RoomDatabase() {

    abstract fun userProfileDao(): UserProfileDao
    abstract fun jobLogDao(): JobLogDao

    companion object {
        @Volatile
        private var INSTANCE: AutomexaDatabase? = null

        fun getDatabase(context: Context): AutomexaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AutomexaDatabase::class.java,
                    "automexa_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
