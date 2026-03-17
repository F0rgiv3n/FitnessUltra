package com.fitnessultra.data.repository

import com.fitnessultra.data.db.dao.WeightDao
import com.fitnessultra.data.db.entity.WeightEntry
import kotlinx.coroutines.flow.Flow

class WeightRepository(private val weightDao: WeightDao) {

    val allWeightEntries: Flow<List<WeightEntry>> = weightDao.getAllWeightEntries()

    suspend fun insertWeightEntry(entry: WeightEntry) = weightDao.insertWeightEntry(entry)

    suspend fun deleteWeightEntry(entry: WeightEntry) = weightDao.deleteWeightEntry(entry)
}
