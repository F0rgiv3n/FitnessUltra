package com.fitnessultra.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import org.osmdroid.util.GeoPoint
import com.fitnessultra.MainActivity
import com.fitnessultra.R
import com.fitnessultra.util.TrackingUtils
import kotlinx.coroutines.*

class TrackingService : LifecycleService() {

    companion object {
        const val ACTION_START_OR_RESUME = "ACTION_START_OR_RESUME"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_STOP = "ACTION_STOP"

        private const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
        private const val NOTIFICATION_CHANNEL_NAME = "Tracking"
        private const val NOTIFICATION_ID = 1

        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<MutableList<GeoPoint>>()
        val timeRunInMillis = MutableLiveData<Long>()
        val currentSpeedKmh = MutableLiveData<Float>()
        val totalDistanceMeters = MutableLiveData<Float>()
        val elevationGainMeters = MutableLiveData<Float>()
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var notificationManager: NotificationManager

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private var timeStarted = 0L
    private var timeRun = 0L
    private var lastAltitude = Double.MIN_VALUE
    private var timerJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                if (isTracking.value == true) {
                    result.locations.forEach { location ->
                        addPathPoint(location)
                        currentSpeedKmh.postValue(location.speed * 3.6f)
                        trackElevation(location)
                    }
                }
            }
        }

        isTracking.observe(this) { updateLocationTracking(it) }

        initValues()
    }

    private fun initValues() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeRunInMillis.postValue(0L)
        currentSpeedKmh.postValue(0f)
        totalDistanceMeters.postValue(0f)
        elevationGainMeters.postValue(0f)
        timeRun = 0L
        lastAltitude = Double.MIN_VALUE
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
            when (action) {
                ACTION_START_OR_RESUME -> {
                    startForegroundService()
                    isTracking.postValue(true)
                    startTimer()
                }
                ACTION_PAUSE -> {
                    isTracking.postValue(false)
                    timerJob?.cancel()
                    timeRun += System.currentTimeMillis() - timeStarted
                    updateNotification(TrackingUtils.formatTime(timeRun))
                }
                ACTION_STOP -> {
                    isTracking.postValue(false)
                    timerJob?.cancel()
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                    initValues()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_run)
            .setContentTitle("FitnessUltra")
            .setContentText("00:00:00")
            .setContentIntent(pendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun updateNotification(time: String) {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_run)
            .setContentTitle("FitnessUltra")
            .setContentText(time)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L)
                .setMinUpdateIntervalMillis(2000L)
                .build()
            fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
        } else {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun addPathPoint(location: Location) {
        val pos = GeoPoint(location.latitude, location.longitude)
        val points = pathPoints.value?.apply { add(pos) } ?: mutableListOf(pos)
        pathPoints.postValue(points)

        if (points.size > 1) {
            val last = points[points.size - 2]
            val current = points.last()
            val results = FloatArray(1)
            Location.distanceBetween(last.latitude, last.longitude, current.latitude, current.longitude, results)
            totalDistanceMeters.postValue((totalDistanceMeters.value ?: 0f) + results[0])
        }
    }

    private fun trackElevation(location: Location) {
        if (lastAltitude != Double.MIN_VALUE) {
            val diff = location.altitude - lastAltitude
            if (diff > 0) {
                elevationGainMeters.postValue((elevationGainMeters.value ?: 0f) + diff.toFloat())
            }
        }
        lastAltitude = location.altitude
    }

    private fun startTimer() {
        timeStarted = System.currentTimeMillis()
        timerJob = serviceScope.launch {
            while (isActive) {
                val elapsed = timeRun + (System.currentTimeMillis() - timeStarted)
                timeRunInMillis.postValue(elapsed)
                updateNotification(TrackingUtils.formatTime(elapsed))
                delay(1000L)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }
}
