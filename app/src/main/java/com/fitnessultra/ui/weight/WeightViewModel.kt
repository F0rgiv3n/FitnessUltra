package com.fitnessultra.ui.weight

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fitnessultra.data.db.AppDatabase
import com.fitnessultra.data.db.entity.WeightEntry
import com.fitnessultra.data.repository.WeightRepository
import kotlinx.coroutines.launch

class WeightViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WeightRepository(
        AppDatabase.getInstance(application).weightDao()
    )

    val weightEntries = repository.allWeightEntries.asLiveData()

    fun saveWeight(weightKg: Float) {
        viewModelScope.launch {
            repository.insertWeightEntry(
                WeightEntry(weightKg = weightKg, dateTimestamp = System.currentTimeMillis())
            )
        }
    }

    fun deleteEntry(entry: WeightEntry) {
        viewModelScope.launch {
            repository.deleteWeightEntry(entry)
        }
    }
}
