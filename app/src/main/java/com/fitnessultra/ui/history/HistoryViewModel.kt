package com.fitnessultra.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import com.fitnessultra.data.db.AppDatabase
import com.fitnessultra.data.repository.RunRepository

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RunRepository(
        AppDatabase.getInstance(application).runDao()
    )

    val runs = repository.allRuns.asLiveData()
}
