package com.fitnessultra.ui.charts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.fitnessultra.data.db.AppDatabase
import com.fitnessultra.data.db.entity.LocationPoint
import com.fitnessultra.data.db.entity.RunEntity
import com.fitnessultra.data.repository.RunRepository

class ChartsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RunRepository(
        AppDatabase.getInstance(application).runDao()
    )

    suspend fun getRunById(runId: Long): RunEntity? = repository.getRunById(runId)

    suspend fun getLocationPoints(runId: Long): List<LocationPoint> =
        repository.getLocationPointsForRun(runId)
}
