package com.fitnessultra.ui.weight

import android.app.Application
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fitnessultra.R
import com.fitnessultra.data.db.AppDatabase
import com.fitnessultra.data.db.entity.WeightEntry
import com.fitnessultra.data.repository.WeightRepository
import kotlinx.coroutines.launch

class WeightViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WeightRepository(
        AppDatabase.getInstance(application).weightDao()
    )

    private val prefs: SharedPreferences =
        application.getSharedPreferences("user_prefs", 0)

    val weightEntries = repository.allWeightEntries.asLiveData()

    fun saveWeight(weightKg: Float) {
        prefs.edit { putFloat("weight_kg", weightKg) }
        viewModelScope.launch {
            repository.insertWeightEntry(
                WeightEntry(weightKg = weightKg, dateTimestamp = System.currentTimeMillis())
            )
        }
    }

    fun saveUserInfo(heightCm: Float, age: Int) {
        prefs.edit {
            putFloat("height_cm", heightCm)
            putFloat("height_m", heightCm / 100f)
            putInt("age", age)
        }
    }

    fun getHeightCm(): Float = prefs.getFloat("height_cm", 0f)
    fun getAge(): Int = prefs.getInt("age", 0)

    fun calculateBmi(weightKg: Float): Float? {
        val heightM = prefs.getFloat("height_m", 0f)
        if (heightM <= 0f) return null
        return weightKg / (heightM * heightM)
    }

    fun bmiCategoryRes(bmi: Float): Int = when {
        bmi < 18.5f -> R.string.bmi_underweight
        bmi < 25f   -> R.string.bmi_normal
        bmi < 30f   -> R.string.bmi_overweight
        else        -> R.string.bmi_obese
    }

    fun deleteEntry(entry: WeightEntry) {
        viewModelScope.launch { repository.deleteWeightEntry(entry) }
    }

    fun updateEntry(entry: WeightEntry, newWeightKg: Float) {
        viewModelScope.launch { repository.updateWeightEntry(entry.copy(weightKg = newWeightKg)) }
    }
}
