package com.fitnessultra.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.widget.RemoteViews
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.HandlerThread
import android.os.Looper
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import org.osmdroid.util.GeoPoint
import com.fitnessultra.MainActivity
import com.fitnessultra.R
import com.fitnessultra.data.db.AppDatabase
import com.fitnessultra.data.db.entity.LocationPoint
import com.fitnessultra.data.db.entity.RunEntity
import com.fitnessultra.data.db.entity.RunSplit
import com.fitnessultra.ui.run.RunWidgetProvider
import com.fitnessultra.util.SettingsManager
import com.fitnessultra.util.ThumbnailUtils
import com.fitnessultra.util.TrackingUtils
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream

class TrackingService : LifecycleService() {

    companion object {
        const val ACTION_START_OR_RESUME = "ACTION_START_OR_RESUME"
        const val ACTION_PAUSE           = "ACTION_PAUSE"
        const val ACTION_STOP            = "ACTION_STOP"
        const val ACTION_STOP_AND_SAVE   = "ACTION_STOP_AND_SAVE"

        private const val NOTIFICATION_CHANNEL_ID   = "tracking_channel"
        private const val NOTIFICATION_CHANNEL_NAME = "Tracking"
        private const val NOTIFICATION_ID           = 1
        private const val ELEVATION_THRESHOLD       = 3.0 // metres — filters GPS altitude noise

        val isTracking           = MutableLiveData<Boolean>()
        val pathPoints           = MutableLiveData<MutableList<GeoPoint>>()
        val rawLocations         = MutableLiveData<MutableList<Location>>()
        val timeRunInMillis      = MutableLiveData<Long>()
        val currentSpeedKmh      = MutableLiveData<Float>()
        val totalDistanceMeters  = MutableLiveData<Float>()
        val elevationGainMeters  = MutableLiveData<Float>()
        val stepCount            = MutableLiveData<Int>()
        val kmSplits             = MutableLiveData<MutableList<Long>>()
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var notificationManager: NotificationManager
    private lateinit var sensorManager: SensorManager
    private var stepSensorListener: SensorEventListener? = null
    private var usingStepCounter = false
    private var stepCounterBaseline = -1L
    private var stepCounterAccumulated = 0
    private var lastStepTime = 0L

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Default + serviceJob)
    private var locationThread: HandlerThread? = null

    private var wakeLock: PowerManager.WakeLock? = null

    private var timeStarted = 0L
    private var timeRun = 0L
    private var lastAltitude = Double.MIN_VALUE
    private var timerJob: Job? = null
    private var slowUpdateCount = 0
    private var lastAcceptedLocation: Location? = null
    private var lastKmReached = 0
    private val splitTimesMs = mutableListOf<Long>()

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(
            NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
        )
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                if (isTracking.value == true) {
                    result.locations.forEach { location ->
                        if (SettingsManager.isAutoPauseEnabled(this@TrackingService)) {
                            if (location.speed * 3.6f < 1.0f) {
                                slowUpdateCount++
                                if (slowUpdateCount >= 3) {
                                    slowUpdateCount = 0
                                    timeRun += System.currentTimeMillis() - timeStarted
                                    timerJob?.cancel()
                                    isTracking.postValue(false)
                                    updateNotification(timeRun, totalDistanceMeters.value ?: 0f, tracking = false)
                                    updateWidget(timeRun, totalDistanceMeters.value ?: 0f, tracking = false)
                                    stopStepCounter()
                                    releaseWakeLock()
                                    return@forEach
                                }
                            } else {
                                slowUpdateCount = 0
                            }
                        }
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
        rawLocations.postValue(mutableListOf())
        timeRunInMillis.postValue(0L)
        currentSpeedKmh.postValue(0f)
        totalDistanceMeters.postValue(0f)
        elevationGainMeters.postValue(0f)
        stepCount.postValue(0)
        timeRun = 0L
        lastAltitude = Double.MIN_VALUE
        slowUpdateCount = 0
        lastAcceptedLocation = null
        stepCounterBaseline = -1L
        stepCounterAccumulated = 0
        lastStepTime = 0L
        kmSplits.postValue(mutableListOf())
        lastKmReached = 0
        splitTimesMs.clear()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
            when (action) {
                ACTION_START_OR_RESUME -> {
                    acquireWakeLock()
                    startForegroundService()
                    isTracking.postValue(true)
                    startTimer()
                    startStepCounter()
                }
                ACTION_PAUSE -> {
                    releaseWakeLock()
                    isTracking.postValue(false)
                    timerJob?.cancel()
                    timeRun += System.currentTimeMillis() - timeStarted
                    updateNotification(timeRun, totalDistanceMeters.value ?: 0f, tracking = false)
                    updateWidget(timeRun, totalDistanceMeters.value ?: 0f, tracking = false)
                    stopStepCounter()
                }
                ACTION_STOP -> {
                    releaseWakeLock()
                    isTracking.postValue(false)
                    timerJob?.cancel()
                    stopStepCounter()
                    updateWidget(0L, 0f, tracking = false)
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                    initValues()
                }
                ACTION_STOP_AND_SAVE -> {
                    releaseWakeLock()
                    // Accumulate any remaining active time
                    val finalTimeRun = if (isTracking.value == true)
                        timeRun + (System.currentTimeMillis() - timeStarted)
                    else timeRun
                    isTracking.postValue(false)
                    timerJob?.cancel()
                    stopStepCounter()
                    updateWidget(0L, 0f, tracking = false)

                    // Snapshot LiveData on main thread before going background
                    val distanceMeters  = totalDistanceMeters.value ?: 0f
                    val elevGain        = elevationGainMeters.value ?: 0f
                    val steps           = stepCount.value ?: 0
                    val rawLocSnapshot  = rawLocations.value?.toList() ?: emptyList()
                    val splitsSnapshot  = kmSplits.value?.toList() ?: emptyList()

                    serviceScope.launch {
                        saveRunToDb(finalTimeRun, distanceMeters, elevGain, steps, rawLocSnapshot, splitsSnapshot)
                        withContext(Dispatchers.Main) {
                            stopForeground(STOP_FOREGROUND_REMOVE)
                            stopSelf()
                            initValues()
                        }
                    }
                }
            }
        }
        return START_STICKY
    }

    private suspend fun saveRunToDb(
        durationMillis: Long,
        distanceMeters: Float,
        elevGain: Float,
        steps: Int,
        rawLocs: List<Location>,
        splits: List<Long>
    ) {
        if (distanceMeters < 10f || durationMillis < 5_000L) return  // skip trivial runs

        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val weightKg = prefs.getFloat("weight_kg", 70f)
        val gender = SettingsManager.gender(this)

        val avgSpeedKmh = if (durationMillis > 0)
            (distanceMeters / 1000f) / (durationMillis / 1000f / 3600f) else 0f
        val calories = TrackingUtils.calculateCalories(distanceMeters, weightKg, gender)

        val run = RunEntity(
            dateTimestamp = System.currentTimeMillis(),
            avgSpeedKmh = avgSpeedKmh,
            distanceMeters = distanceMeters,
            durationMillis = durationMillis,
            caloriesBurned = calories,
            elevationGainMeters = elevGain,
            stepCount = steps
        )

        val db = AppDatabase.getInstance(this)
        val runId = db.runDao().insertRun(run)

        if (rawLocs.isNotEmpty()) {
            val points = rawLocs.map { loc ->
                LocationPoint(
                    runId = runId,
                    latitude = loc.latitude,
                    longitude = loc.longitude,
                    altitude = if (loc.hasAltitude()) loc.altitude else 0.0,
                    speedMs = loc.speed,
                    timestamp = loc.time
                )
            }
            db.runDao().insertLocationPoints(points)

            val bitmap = ThumbnailUtils.render(rawLocs)
            if (bitmap != null) {
                val dir = File(filesDir, "thumbnails")
                dir.mkdirs()
                try {
                    FileOutputStream(File(dir, "$runId.png")).use { out ->
                        bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 85, out)
                    }
                } finally {
                    bitmap.recycle()
                }
            }
        }

        if (splits.isNotEmpty()) {
            db.runDao().insertSplits(splits.mapIndexed { i, ms ->
                RunSplit(runId = runId, kmNumber = i + 1, splitMs = ms)
            })
        }
    }

    private fun startForegroundService() {
        startForeground(NOTIFICATION_ID, buildNotification(0L, 0f, tracking = true))
    }

    private fun updateNotification(elapsedMs: Long, distanceMeters: Float, tracking: Boolean) {
        notificationManager.notify(NOTIFICATION_ID, buildNotification(elapsedMs, distanceMeters, tracking))
    }

    private fun buildNotification(elapsedMs: Long, distanceMeters: Float, tracking: Boolean) =
        NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).apply {
            setAutoCancel(false)
            setOngoing(true)
            setSmallIcon(R.drawable.ic_run)
            setContentTitle(TrackingUtils.formatTime(elapsedMs))

            val useMiles = SettingsManager.useMiles(this@TrackingService)
            val distStr  = TrackingUtils.formatDistance(distanceMeters, useMiles, this@TrackingService)
            val paceStr  = TrackingUtils.calculatePace(distanceMeters, elapsedMs, useMiles, this@TrackingService)
            setContentText(getString(R.string.notification_content, distStr, paceStr))

            val openIntent = PendingIntent.getActivity(
                this@TrackingService, 0,
                Intent(this@TrackingService, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            setContentIntent(openIntent)

            // Action 1: Pause / Resume
            val pauseResumeLabel: String
            val pauseResumeIcon: Int
            val pauseResumeAction: String
            if (tracking) {
                pauseResumeLabel  = getString(R.string.replay_pause)
                pauseResumeIcon   = R.drawable.ic_pause_notification
                pauseResumeAction = ACTION_PAUSE
            } else {
                pauseResumeLabel  = getString(R.string.replay_play)
                pauseResumeIcon   = R.drawable.ic_run
                pauseResumeAction = ACTION_START_OR_RESUME
            }
            val pauseResumeIntent = PendingIntent.getService(
                this@TrackingService, 1,
                Intent(this@TrackingService, TrackingService::class.java).apply { action = pauseResumeAction },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            addAction(pauseResumeIcon, pauseResumeLabel, pauseResumeIntent)

            // Action 2: Stop & Save
            val stopIntent = PendingIntent.getService(
                this@TrackingService, 2,
                Intent(this@TrackingService, TrackingService::class.java).apply { action = ACTION_STOP_AND_SAVE },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            addAction(R.drawable.ic_stop_notification, getString(R.string.btn_finish), stopIntent)
        }.build()

    private fun updateWidget(elapsedMs: Long, distanceMeters: Float, tracking: Boolean) {
        val manager = AppWidgetManager.getInstance(this)
        val ids = manager.getAppWidgetIds(ComponentName(this, RunWidgetProvider::class.java))
        if (ids.isEmpty()) return

        val views = RemoteViews(packageName, R.layout.widget_run)
        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widgetRoot, openIntent)

        if (elapsedMs > 0 || tracking) {
            val useMiles = SettingsManager.useMiles(this)
            val status = if (tracking) "● RUNNING" else "⏸ PAUSED"
            views.setTextViewText(R.id.widgetStatus, status)
            views.setTextViewText(R.id.widgetTimer, TrackingUtils.formatTime(elapsedMs))
            val dist = TrackingUtils.formatDistance(distanceMeters, useMiles, this)
            val pace = TrackingUtils.calculatePace(distanceMeters, elapsedMs, useMiles, this)
            views.setTextViewText(R.id.widgetStats, "$dist  ·  $pace")
        } else {
            views.setTextViewText(R.id.widgetStatus, "FitnessUltra")
            views.setTextViewText(R.id.widgetTimer, "--:--:--")
            views.setTextViewText(R.id.widgetStats, getString(R.string.widget_no_run))
        }

        manager.updateAppWidget(ids, views)
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            val priority = if (SettingsManager.gpsAccuracy(this) == "high")
                Priority.PRIORITY_HIGH_ACCURACY
            else
                Priority.PRIORITY_BALANCED_POWER_ACCURACY
            val request = LocationRequest.Builder(priority, 3000L)
                .setMinUpdateIntervalMillis(2000L)
                .build()
            locationThread = HandlerThread("LocationThread").apply { start() }
            fusedLocationClient.requestLocationUpdates(request, locationCallback, locationThread!!.looper)
        } else {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            locationThread?.quit()
            locationThread = null
        }
    }

    private fun addPathPoint(location: Location) {
        if (location.accuracy > 25f) return

        val prev = lastAcceptedLocation
        if (prev != null) {
            val distanceM = prev.distanceTo(location)
            val timeDeltaS = (location.time - prev.time) / 1000f
            if (timeDeltaS > 0f) {
                val impliedSpeedKmh = (distanceM / timeDeltaS) * 3.6f
                if (impliedSpeedKmh > 120f) return
            }
        }
        lastAcceptedLocation = location

        val pos = GeoPoint(location.latitude, location.longitude)
        val points = pathPoints.value?.apply { add(pos) } ?: mutableListOf(pos)
        pathPoints.postValue(points)
        val locs = rawLocations.value?.apply { add(location) } ?: mutableListOf(location)
        rawLocations.postValue(locs)

        if (points.size > 1) {
            val last = points[points.size - 2]
            val results = FloatArray(1)
            Location.distanceBetween(last.latitude, last.longitude, pos.latitude, pos.longitude, results)
            val newTotal = (totalDistanceMeters.value ?: 0f) + results[0]
            totalDistanceMeters.postValue(newTotal)
            checkKmSplit(newTotal)
        }
    }

    private fun checkKmSplit(totalMeters: Float) {
        val newKm = (totalMeters / 1000f).toInt()
        if (newKm > lastKmReached && newKm > 0) {
            val elapsed = timeRun + (System.currentTimeMillis() - timeStarted)
            val prevCumulative = splitTimesMs.sum()
            splitTimesMs.add(elapsed - prevCumulative)
            kmSplits.postValue(ArrayList(splitTimesMs))
            lastKmReached = newKm
        }
    }

    private fun trackElevation(location: Location) {
        if (!location.hasAltitude()) return
        val alt = location.altitude
        if (lastAltitude == Double.MIN_VALUE) {
            lastAltitude = alt
            return
        }
        val diff = alt - lastAltitude
        when {
            diff >= ELEVATION_THRESHOLD -> {
                elevationGainMeters.postValue((elevationGainMeters.value ?: 0f) + diff.toFloat())
                lastAltitude = alt
            }
            diff <= -ELEVATION_THRESHOLD -> {
                lastAltitude = alt
            }
        }
    }

    private fun startTimer() {
        timeStarted = System.currentTimeMillis()
        timerJob = serviceScope.launch {
            while (isActive) {
                val elapsed = timeRun + (System.currentTimeMillis() - timeStarted)
                timeRunInMillis.postValue(elapsed)
                val dist = totalDistanceMeters.value ?: 0f
                updateNotification(elapsed, dist, tracking = true)
                updateWidget(elapsed, dist, tracking = true)
                delay(1000L)
            }
        }
    }

    private fun startStepCounter() {
        val counterSensor  = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        val detectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        when {
            counterSensor != null -> {
                usingStepCounter = true
                stepCounterBaseline = -1L
                stepSensorListener = object : SensorEventListener {
                    override fun onSensorChanged(event: SensorEvent) {
                        if (isTracking.value != true) return
                        val hardwareCount = event.values[0].toLong()
                        if (stepCounterBaseline == -1L) stepCounterBaseline = hardwareCount
                        stepCount.postValue((stepCounterAccumulated + (hardwareCount - stepCounterBaseline)).toInt())
                    }
                    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
                }
                sensorManager.registerListener(stepSensorListener, counterSensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
            detectorSensor != null -> {
                usingStepCounter = false
                stepSensorListener = object : SensorEventListener {
                    override fun onSensorChanged(event: SensorEvent) {
                        if (isTracking.value != true) return
                        val now = System.currentTimeMillis()
                        if (now - lastStepTime < 200L) return
                        lastStepTime = now
                        stepCount.postValue((stepCount.value ?: 0) + 1)
                    }
                    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
                }
                sensorManager.registerListener(stepSensorListener, detectorSensor, SensorManager.SENSOR_DELAY_FASTEST)
            }
        }
    }

    private fun stopStepCounter() {
        if (usingStepCounter) {
            stepCounterAccumulated = stepCount.value ?: 0
        }
        stepSensorListener?.let { sensorManager.unregisterListener(it) }
        stepSensorListener = null
    }

    private fun acquireWakeLock() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "FitnessUltra::TrackingWakeLock")
        wakeLock?.acquire(6 * 60 * 60 * 1000L)
    }

    private fun releaseWakeLock() {
        if (wakeLock?.isHeld == true) wakeLock?.release()
        wakeLock = null
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseWakeLock()
        serviceJob.cancel()
        stopStepCounter()
        locationThread?.quit()
        locationThread = null
    }
}
