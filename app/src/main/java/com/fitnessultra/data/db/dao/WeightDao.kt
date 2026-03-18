package com.fitnessultra.data.db.dao

import androidx.room.*
import com.fitnessultra.data.db.entity.WeightEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightEntry(entry: WeightEntry)

    @Delete
    suspend fun deleteWeightEntry(entry: WeightEntry)

    @Update
    suspend fun updateWeightEntry(entry: WeightEntry)

    @Query("SELECT * FROM weight_entries ORDER BY dateTimestamp ASC")
    fun getAllWeightEntries(): Flow<List<WeightEntry>>
}
