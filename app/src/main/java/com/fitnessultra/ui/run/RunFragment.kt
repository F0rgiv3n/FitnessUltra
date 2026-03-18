package com.fitnessultra.ui.run

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.fitnessultra.R
import com.fitnessultra.databinding.FragmentRunBinding
import com.fitnessultra.service.TrackingService
import com.fitnessultra.util.SettingsManager
import com.fitnessultra.util.TrackingUtils
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polyline
import java.util.Locale

class RunFragment : Fragment() {

    private var _binding: FragmentRunBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RunViewModel by viewModels()
    private var isTracking = false
    private var routePolyline: Polyline? = null

    private lateinit var tts: TextToSpeech
    private var lastVoiceKm = 0

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            viewModel.sendCommand(TrackingService.ACTION_START_OR_RESUME)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRunBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Configuration.getInstance().userAgentValue = requireContext().packageName

        binding.mapView.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(17.0)
        }

        routePolyline = Polyline().apply {
            outlinePaint.color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            outlinePaint.strokeWidth = 8f
        }
        binding.mapView.overlays.add(routePolyline)

        tts = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) tts.language = Locale.US
        }

        binding.btnToggleRun.setOnClickListener {
            if (isTracking) {
                viewModel.sendCommand(TrackingService.ACTION_PAUSE)
            } else {
                requestPermissionsAndStart()
            }
        }

        binding.btnStopRun.setOnClickListener {
            val weightKg = getUserWeight()
            viewModel.saveRun(weightKg)
            viewModel.sendCommand(TrackingService.ACTION_STOP)
            routePolyline?.setPoints(emptyList())
            binding.mapView.invalidate()
            lastVoiceKm = 0
        }

        observeTracking()
    }

    private fun observeTracking() {
        viewModel.isTracking.observe(viewLifecycleOwner) { tracking ->
            isTracking = tracking
            binding.btnToggleRun.text = if (tracking) "PAUSE" else "START"
            binding.btnStopRun.visibility =
                if (!tracking && (viewModel.timeRunInMillis.value ?: 0L) > 0L) View.VISIBLE else View.GONE
        }

        viewModel.pathPoints.observe(viewLifecycleOwner) { points ->
            updatePolyline(points)
            if (points.isNotEmpty()) {
                binding.mapView.controller.animateTo(points.last())
            }
            checkVoiceMilestone()
        }

        viewModel.timeRunInMillis.observe(viewLifecycleOwner) { ms ->
            binding.tvTimer.text = TrackingUtils.formatTime(ms)
        }

        viewModel.totalDistanceMeters.observe(viewLifecycleOwner) { meters ->
            val useMiles = SettingsManager.useMiles(requireContext())
            binding.tvDistance.text = TrackingUtils.formatDistance(meters, useMiles)
            val durationMs = viewModel.timeRunInMillis.value ?: 0L
            binding.tvPace.text = TrackingUtils.calculatePace(meters, durationMs, useMiles)
        }

        viewModel.currentSpeedKmh.observe(viewLifecycleOwner) { kmh ->
            binding.tvSpeed.text = TrackingUtils.formatSpeedKmh(kmh, SettingsManager.useMiles(requireContext()))
        }

        viewModel.stepCount.observe(viewLifecycleOwner) { steps ->
            binding.tvSteps.text = (steps ?: 0).toString()
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
            val paceStr = TrackingUtils.calculatePace(kmAnnounced * 1000f, durationMs)
            tts.speak(
                "$kmAnnounced kilometer completed – pace $paceStr",
                TextToSpeech.QUEUE_ADD,
                null,
                null
            )
        }
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

        if (allGranted) {
            viewModel.sendCommand(TrackingService.ACTION_START_OR_RESUME)
        } else {
            locationPermissionLauncher.launch(permissions.toTypedArray())
        }
    }

    private fun getUserWeight(): Float {
        val prefs = requireActivity().getSharedPreferences("user_prefs", 0)
        return prefs.getFloat("weight_kg", 70f)
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroyView() {
        tts.shutdown()
        _binding = null
        super.onDestroyView()
    }
}
