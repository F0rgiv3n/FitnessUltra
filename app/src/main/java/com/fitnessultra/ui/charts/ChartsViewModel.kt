package com.fitnessultra.ui.charts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import com.fitnessultra.data.db.AppDatabase
import com.fitnessultra.data.db.entity.LocationPoint
import com.fitnessultra.data.repository.RunRepository
import kotlinx.coroutines.flow.Flow

class ChartsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RunRepository(
        AppDatabase.getInstance(application).runDao()
    )

    val runs = repository.allRuns.asLiveData()

    suspend fun getLocationPoints(runId: Long): List<LocationPoint> =
        repository.getLocationPointsForRun(runId)
}
