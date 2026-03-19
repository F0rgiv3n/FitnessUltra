package com.fitnessultra.ui.run

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fitnessultra.R
import com.fitnessultra.databinding.FragmentRunBinding
import com.fitnessultra.service.TrackingService
import com.fitnessultra.util.SettingsManager
import com.fitnessultra.util.TrackingUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.util.Locale

class RunFragment : Fragment() {

    private var _binding: FragmentRunBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RunViewModel by viewModels()
    private var isTracking = false
    private var routePolyline: Polyline? = null
    private var locationMarker: Marker? = null

    private lateinit var tts: TextToSpeech
    private var lastVoiceKm = 0

    private var workoutConfig: WorkoutConfig = WorkoutConfig.FreeRun
    private var intervalJob: Job? = null
    private var lastPaceAlertMs = 0L
    private val paceAlertCooldownMs = 30_000L

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            startCountdownAndRun()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRunBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapView.apply {
            applyMapStyle()
            setMultiTouchControls(true)
            controller.setZoom(17.0)
        }

        routePolyline = Polyline().apply {
            outlinePaint.color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            outlinePaint.strokeWidth = 8f
        }
        binding.mapView.overlays.add(routePolyline)

        locationMarker = Marker(binding.mapView).apply {
            icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_runner_marker)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            title = null
            infoWindow = null
            isEnabled = false
        }
        binding.mapView.overlays.add(locationMarker)

        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val langCode = SettingsManager.voiceLanguage(requireContext())
                val locale = if (langCode == "default") Locale.getDefault() else Locale(langCode)
                tts.language = locale
            }
        }

        promptBatteryOptimizationIfNeeded()

        binding.btnToggleRun.setOnClickListener {
            if (isTracking) {
                viewModel.sendCommand(TrackingService.ACTION_PAUSE)
            } else if ((viewModel.timeRunInMillis.value ?: 0L) > 0L) {
                // Resuming from pause — no setup dialog
                requestPermissionsAndStart()
            } else {
                // Fresh start — show workout setup
                showWorkoutSetup()
            }
        }

        binding.btnStopRun.setOnClickListener {
            intervalJob?.cancel()
            intervalJob = null
            workoutConfig = WorkoutConfig.FreeRun
            lastVoiceKm = 0
            lastPaceAlertMs = 0L
            val weightKg = getUserWeight()
            val gender = SettingsManager.gender(requireContext())
            viewModel.saveRun(weightKg, gender)
            viewModel.sendCommand(TrackingService.ACTION_STOP)
            routePolyline?.setPoints(emptyList())
            locationMarker?.isEnabled = false
            binding.mapView.invalidate()
        }

        observeTracking()
    }

    private fun showWorkoutSetup() {
        WorkoutSetupBottomSheet().apply {
            onStart = { config ->
                workoutConfig = config
                requestPermissionsAndStart()
            }
        }.show(parentFragmentManager, "workout_setup")
    }

    private fun applyMapStyle() {
        binding.mapView.setTileSource(SettingsManager.tileSource(requireContext()))
    }

    private fun startCountdownAndRun() {
        if (!SettingsManager.isCountdownEnabled(requireContext())) {
            viewModel.sendCommand(TrackingService.ACTION_START_OR_RESUME)
            startWorkoutLogic()
            return
        }
        binding.tvCountdown.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            for (i in 3 downTo 1) {
                binding.tvCountdown.text = i.toString()
                delay(1000L)
            }
            binding.tvCountdown.visibility = View.GONE
            viewModel.sendCommand(TrackingService.ACTION_START_OR_RESUME)
            startWorkoutLogic()
        }
    }

    private fun startWorkoutLogic() {
        val config = workoutConfig
        if (config is WorkoutConfig.Intervals) {
            intervalJob?.cancel()
            intervalJob = viewLifecycleOwner.lifecycleScope.launch {
                run {
                    repeat(config.reps) {
                        if (!isActive) return@run
                        speakTts(getString(R.string.tts_start_running, config.runSeconds))
                        waitActiveSeconds(config.runSeconds)
                        if (!isActive) return@run
                        speakTts(getString(R.string.tts_start_walking, config.walkSeconds))
                        waitActiveSeconds(config.walkSeconds)
                    }
                }
                if (isActive) speakTts(getString(R.string.tts_workout_complete))
            }
        }
    }

    /** Waits [totalSeconds] of active (non-paused) tracking time. */
    private suspend fun waitActiveSeconds(totalSeconds: Int) {
        var elapsed = 0
        while (elapsed < totalSeconds) {
            delay(1000L)
            if (viewModel.isTracking.value == true) elapsed++
        }
    }

    private fun observeTracking() {
        viewModel.isTracking.observe(viewLifecycleOwner) { tracking ->
            isTracking = tracking
            binding.btnToggleRun.setText(if (tracking) R.string.replay_pause else R.string.btn_start)
            binding.btnStopRun.visibility =
                if (!tracking && (viewModel.timeRunInMillis.value ?: 0L) > 0L) View.VISIBLE else View.GONE
        }

        viewModel.pathPoints.observe(viewLifecycleOwner) { points ->
            updatePolyline(points)
            if (points.isNotEmpty()) {
                val lastPoint = points.last()
                locationMarker?.position = lastPoint
                locationMarker?.isEnabled = true
                if (SettingsManager.isMapFollow(requireContext())) {
                    binding.mapView.controller.setCenter(lastPoint)
                }
            } else {
                locationMarker?.isEnabled = false
            }
            checkVoiceMilestone()
        }

        viewModel.timeRunInMillis.observe(viewLifecycleOwner) { ms ->
            binding.tvTimer.text = TrackingUtils.formatTime(ms)
        }

        viewModel.totalDistanceMeters.observe(viewLifecycleOwner) { meters ->
            val useMiles = SettingsManager.useMiles(requireContext())
            binding.tvDistance.text = TrackingUtils.formatDistance(meters, useMiles, requireContext())
            val durationMs = viewModel.timeRunInMillis.value ?: 0L
            val paceStr = TrackingUtils.calculatePace(meters, durationMs, useMiles, requireContext())
            binding.tvPace.text = paceStr
            checkTargetPace(meters, durationMs, useMiles)
        }

        viewModel.currentSpeedKmh.observe(viewLifecycleOwner) { kmh ->
            binding.tvSpeed.text = TrackingUtils.formatSpeedKmh(kmh, SettingsManager.useMiles(requireContext()), requireContext())
        }

        viewModel.stepCount.observe(viewLifecycleOwner) { steps ->
            binding.tvSteps.text = (steps ?: 0).toString()
        }

        viewModel.elevationGainMeters.observe(viewLifecycleOwner) { gain ->
            binding.tvElevation.text = getString(R.string.elevation_gain_format, gain ?: 0f)
        }
    }

    private fun checkTargetPace(meters: Float, durationMs: Long, useMiles: Boolean) {
        val config = workoutConfig as? WorkoutConfig.TargetPace ?: return
        if (meters < 500f) return  // wait for meaningful distance
        val now = System.currentTimeMillis()
        if (now - lastPaceAlertMs < paceAlertCooldownMs) return

        val currentPaceSec = TrackingUtils.calculatePaceSec(meters, durationMs, useMiles)
        val diff = currentPaceSec - config.paceSecPerUnit
        when {
            diff > config.toleranceSec -> {
                speakTts(getString(R.string.tts_pace_too_slow))
                binding.tvPace.setTextColor("#D32F2F".toColorInt())
                lastPaceAlertMs = now
            }
            diff < -config.toleranceSec -> {
                speakTts(getString(R.string.tts_pace_too_fast))
                binding.tvPace.setTextColor("#1565C0".toColorInt())
                lastPaceAlertMs = now
            }
            else -> binding.tvPace.setTextColor(
                requireContext().getColor(android.R.color.tab_indicator_text)
            )
        }
    }

    private fun updatePolyline(points: MutableList<GeoPoint>) {
        routePolyline?.setPoints(points)
        binding.mapView.invalidate()
    }

    private fun checkVoiceMilestone() {
        if (!SettingsManager.isVoiceEnabled(requireContext())) return
        val freqKm = SettingsManager.voiceFrequencyKm(requireContext())
        val distanceKm = (viewModel.totalDistanceMeters.value ?: 0f) / 1000f
        val milestonePassed = (distanceKm / freqKm).toInt()
        if (milestonePassed > lastVoiceKm) {
            lastVoiceKm = milestonePassed
            val kmAnnounced = milestonePassed * freqKm
            val durationMs = viewModel.timeRunInMillis.value ?: 0L
            val paceStr = TrackingUtils.calculatePace(kmAnnounced * 1000f, durationMs, context = requireContext())
            speakTts(getString(R.string.tts_voice_milestone, kmAnnounced, paceStr))
        }
    }

    private fun speakTts(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_ADD, null, null)
    }

    private fun requestPermissionsAndStart() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACTIVITY_RECOGNITION)
        }

        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }

        if (allGranted) startCountdownAndRun()
        else locationPermissionLauncher.launch(permissions.toTypedArray())
    }

    private fun promptBatteryOptimizationIfNeeded() {
        val pm = requireContext().getSystemService(Context.POWER_SERVICE) as PowerManager
        val pkg = requireContext().packageName
        if (pm.isIgnoringBatteryOptimizations(pkg)) return

        val prefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("battery_opt_asked", false)) return
        prefs.edit { putBoolean("battery_opt_asked", true) }

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.battery_opt_title))
            .setMessage(getString(R.string.battery_opt_message))
            .setPositiveButton(getString(R.string.battery_opt_open)) { _, _ ->
                startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun getUserWeight(): Float {
        val prefs = requireActivity().getSharedPreferences("user_prefs", 0)
        return prefs.getFloat("weight_kg", 70f)
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        applyMapStyle()
        if (SettingsManager.isKeepScreenOn(requireContext())) {
            requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onDestroyView() {
        intervalJob?.cancel()
        tts.shutdown()
        _binding = null
        super.onDestroyView()
    }
}
