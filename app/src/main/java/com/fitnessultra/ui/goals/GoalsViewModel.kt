package com.fitnessultra.ui.goals

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.fitnessultra.data.db.AppDatabase
import java.util.Calendar

class GoalsViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).runDao()
    private val prefs = PreferenceManager.getDefaultSharedPreferences(application)

    private val weekStart: Long
        get() = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    val weeklyRuns = dao.getRunsSince(weekStart).asLiveData()

    var goalDistanceKm: Float
        get() = prefs.getFloat("goal_distance_km", 30f)
        set(v) = prefs.edit { putFloat("goal_distance_km", v) }

    var goalTimeMinutes: Int
        get() = prefs.getInt("goal_time_minutes", 180)
        set(v) = prefs.edit { putInt("goal_time_minutes", v) }

    var goalSteps: Int
        get() = prefs.getInt("goal_steps", 50000)
        set(v) = prefs.edit { putInt("goal_steps", v) }
}
