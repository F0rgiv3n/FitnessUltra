package com.fitnessultra.ui.run

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.fitnessultra.service.TrackingService

class RunViewModel(application: Application) : AndroidViewModel(application) {

    val isTracking = TrackingService.isTracking
    val pathPoints = TrackingService.pathPoints
    val timeRunInMillis = TrackingService.timeRunInMillis
    val currentSpeedKmh = TrackingService.currentSpeedKmh
    val totalDistanceMeters = TrackingService.totalDistanceMeters
    val elevationGainMeters = TrackingService.elevationGainMeters
    val stepCount = TrackingService.stepCount

    fun sendCommand(action: String) {
        Intent(getApplication(), TrackingService::class.java).also {
            it.action = action
            getApplication<Application>().startService(it)
        }
    }
}
