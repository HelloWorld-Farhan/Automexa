package com.automexa.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.automexa.app.data.local.entities.JobLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JobLogDao {

    @Query("SELECT * FROM job_application_logs ORDER BY appliedTimestamp DESC")
    fun getAllLogs(): Flow<List<JobLogEntity>>

    @Query("SELECT COUNT(*) FROM job_application_logs WHERE status = 'Applied'")
    fun getAppliedCount(): Flow<Int>

    @Query("SELECT * FROM job_application_logs ORDER BY appliedTimestamp DESC LIMIT :limit")
    fun getRecentLogs(limit: Int): Flow<List<JobLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: JobLogEntity): Long
}
