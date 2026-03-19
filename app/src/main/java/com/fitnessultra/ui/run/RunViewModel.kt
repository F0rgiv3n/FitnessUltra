package com.fitnessultra.ui.run

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fitnessultra.data.db.AppDatabase
import com.fitnessultra.data.db.entity.LocationPoint
import com.fitnessultra.data.db.entity.RunEntity
import com.fitnessultra.data.repository.RunRepository
import com.fitnessultra.service.TrackingService
import com.fitnessultra.util.TrackingUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RunViewModel(application: Application) : AndroidViewModel(application) {

    val isTracking = TrackingService.isTracking
    val pathPoints = TrackingService.pathPoints
    val timeRunInMillis = TrackingService.timeRunInMillis
    val currentSpeedKmh = TrackingService.currentSpeedKmh
    val totalDistanceMeters = TrackingService.totalDistanceMeters
    val elevationGainMeters = TrackingService.elevationGainMeters
    val stepCount = TrackingService.stepCount

    private val repository = RunRepository(
        AppDatabase.getInstance(application).runDao()
    )

    fun sendCommand(action: String) {
        Intent(getApplication(), TrackingService::class.java).also {
            it.action = action
            getApplication<Application>().startService(it)
        }
    }

    /** Call this when user presses Stop to persist the run. */
    fun saveRun(weightKg: Float, gender: String = "none") {
        // Snapshot LiveData values on main thread before switching dispatcher
        val distanceMeters = totalDistanceMeters.value ?: return
        val durationMillis = timeRunInMillis.value ?: return
        val elevationGain = elevationGainMeters.value ?: 0f
        val steps = stepCount.value ?: 0
        val pathSnapshot = pathPoints.value?.map { it } ?: emptyList()

        viewModelScope.launch(Dispatchers.IO) {
            val avgSpeedKmh = if (durationMillis > 0)
                (distanceMeters / 1000f) / (durationMillis / 1000f / 3600f)
            else 0f
            val calories = TrackingUtils.calculateCalories(distanceMeters, weightKg, gender)

            val run = RunEntity(
                dateTimestamp = System.currentTimeMillis(),
                avgSpeedKmh = avgSpeedKmh,
                distanceMeters = distanceMeters,
                durationMillis = durationMillis,
                caloriesBurned = calories,
                elevationGainMeters = elevationGain,
                stepCount = steps
            )

            val points = pathSnapshot.mapIndexed { index, geoPoint ->
                LocationPoint(
                    runId = 0,
                    latitude = geoPoint.latitude,
                    longitude = geoPoint.longitude,
                    altitude = 0.0,
                    speedMs = 0f,
                    timestamp = System.currentTimeMillis() + index
                )
            }

            val runId = repository.insertRun(run)
            if (points.isNotEmpty()) {
                repository.insertLocationPoints(points.map { it.copy(runId = runId) })
            }
        }
    }
}
