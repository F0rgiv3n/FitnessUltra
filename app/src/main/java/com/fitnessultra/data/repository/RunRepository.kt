package com.fitnessultra.data.repository

import com.fitnessultra.data.db.dao.RunDao
import com.fitnessultra.data.db.entity.LocationPoint
import com.fitnessultra.data.db.entity.RunEntity
import com.fitnessultra.data.db.entity.RunSplit
import kotlinx.coroutines.flow.Flow

class RunRepository(private val runDao: RunDao) {

    val allRuns: Flow<List<RunEntity>> = runDao.getAllRuns()

    suspend fun insertRun(run: RunEntity): Long = runDao.insertRun(run)

    suspend fun deleteRun(run: RunEntity) = runDao.deleteRun(run)

    suspend fun getRunById(runId: Long): RunEntity? = runDao.getRunById(runId)

    suspend fun insertLocationPoints(points: List<LocationPoint>) =
        runDao.insertLocationPoints(points)

    suspend fun getLocationPointsForRun(runId: Long): List<LocationPoint> =
        runDao.getLocationPointsForRun(runId)

    suspend fun insertSplits(splits: List<RunSplit>) = runDao.insertSplits(splits)

    suspend fun getSplitsForRun(runId: Long): List<RunSplit> = runDao.getSplitsForRun(runId)
}
