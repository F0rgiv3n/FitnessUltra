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
import kotlinx.coroutines.launch

class RunViewModel(application: Application) : AndroidViewModel(application) {

    val isTracking = TrackingService.isTracking
    val pathPoints = TrackingService.pathPoints
    val timeRunInMillis = TrackingService.timeRunInMillis
    val currentSpeedKmh = TrackingService.currentSpeedKmh
    val totalDistanceMeters = TrackingService.totalDistanceMeters
    val elevationGainMeters = TrackingService.elevationGainMeters

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
    fun saveRun(weightKg: Float) {
        val distanceMeters = totalDistanceMeters.value ?: return
        val durationMillis = timeRunInMillis.value ?: return
        val elevationGain = elevationGainMeters.value ?: 0f
        val avgSpeedKmh = if (durationMillis > 0)
            (distanceMeters / 1000f) / (durationMillis / 1000f / 3600f)
        else 0f
        val calories = TrackingUtils.calculateCalories(distanceMeters, weightKg)

        val run = RunEntity(
            dateTimestamp = System.currentTimeMillis(),
            avgSpeedKmh = avgSpeedKmh,
            distanceMeters = distanceMeters,
            durationMillis = durationMillis,
            caloriesBurned = calories,
            elevationGainMeters = elevationGain
        )

        val points = pathPoints.value?.mapIndexed { index, geoPoint ->
            LocationPoint(
                runId = 0, // updated after insert
                latitude = geoPoint.latitude,
                longitude = geoPoint.longitude,
                altitude = 0.0,
                speedMs = 0f,
                timestamp = System.currentTimeMillis() + index
            )
        } ?: emptyList()

        viewModelScope.launch {
            val runId = repository.insertRun(run)
            val pointsWithId = points.map { it.copy(runId = runId) }
            if (pointsWithId.isNotEmpty()) {
                repository.insertLocationPoints(pointsWithId)
            }
        }
    }
}
