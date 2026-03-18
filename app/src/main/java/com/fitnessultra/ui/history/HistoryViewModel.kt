package com.fitnessultra.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fitnessultra.data.db.AppDatabase
import com.fitnessultra.data.db.entity.RunEntity
import com.fitnessultra.data.repository.RunRepository
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RunRepository(
        AppDatabase.getInstance(application).runDao()
    )

    val runs = repository.allRuns.asLiveData()

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
