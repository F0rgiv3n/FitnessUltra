package com.fitnessultra.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fitnessultra.data.db.AppDatabase
import com.fitnessultra.data.db.entity.RunEntity
import com.fitnessultra.data.repository.RunRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RunRepository(
        AppDatabase.getInstance(application).runDao()
    )

    val runs = repository.allRuns.asLiveData()

    /** Set of run IDs that currently hold a personal record (longest distance or fastest pace). */
    val prRunIds = repository.allRuns.map { runs ->
        val ids = mutableSetOf<Long>()
        if (runs.isNotEmpty()) {
            runs.maxByOrNull { it.distanceMeters }?.let { ids.add(it.id) }
            runs.filter { it.avgSpeedKmh > 0 }.maxByOrNull { it.avgSpeedKmh }?.let { ids.add(it.id) }
        }
        ids as Set<Long>
    }.asLiveData()

    fun deleteRun(run: RunEntity) {
        viewModelScope.launch {
            repository.deleteRun(run)
        }
    }

    fun restoreRun(run: RunEntity) {
        viewModelScope.launch {
            repository.insertRun(run)
        }
    }
}
