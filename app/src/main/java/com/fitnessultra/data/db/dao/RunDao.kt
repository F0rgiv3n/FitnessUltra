package com.fitnessultra.data.db.dao

import androidx.room.*
import com.fitnessultra.data.db.entity.LocationPoint
import com.fitnessultra.data.db.entity.RunEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RunDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: RunEntity): Long

    @Delete
    suspend fun deleteRun(run: RunEntity)

    @Query("SELECT * FROM runs ORDER BY dateTimestamp DESC")
    fun getAllRuns(): Flow<List<RunEntity>>

    @Query("SELECT * FROM runs WHERE dateTimestamp >= :since ORDER BY dateTimestamp DESC")
    fun getRunsSince(since: Long): Flow<List<RunEntity>>

    @Query("SELECT * FROM runs WHERE id = :runId")
    suspend fun getRunById(runId: Long): RunEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocationPoints(points: List<LocationPoint>)

    @Query("SELECT * FROM location_points WHERE runId = :runId ORDER BY timestamp ASC")
    suspend fun getLocationPointsForRun(runId: Long): List<LocationPoint>
}
