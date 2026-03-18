package com.fitnessultra.ui.replay

import androidx.core.graphics.toColorInt
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fitnessultra.R
import com.fitnessultra.data.db.entity.LocationPoint
import com.fitnessultra.databinding.FragmentReplayBinding
import com.fitnessultra.util.SettingsManager
import com.fitnessultra.util.TrackingUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.text.SimpleDateFormat
import java.util.*

class ReplayFragment : Fragment() {

    private var _binding: FragmentReplayBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReplayViewModel by viewModels()

    private var points: List<LocationPoint> = emptyList()
    private var cumulativeDistances: FloatArray = FloatArray(0)

    private var routePolyline: Polyline? = null
    private var completedPolyline: Polyline? = null
    private var runnerMarker: Marker? = null

    private var currentIndex = 0
    private var isPlaying = false
    private var speedMultiplier = 1f
    private var replayJob: Job? = null
    private var isScrubbing = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReplayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Configuration.getInstance().userAgentValue = requireContext().packageName
        binding.replayMapView.apply {
            setTileSource(SettingsManager.tileSource(requireContext()))
            setMultiTouchControls(true)
        }

        val runId = arguments?.getLong("runId") ?: return

        lifecycleScope.launch {
            val run = viewModel.getRunById(runId)
            points = viewModel.getLocationPoints(runId)

            if (points.isEmpty()) {
                binding.tvNoData.visibility = View.VISIBLE
                binding.replayMapView.visibility = View.GONE
                return@launch
            }

            // Pre-calculate cumulative distances (avoids O(n²) in updateStatsAt)
            cumulativeDistances = FloatArray(points.size)
            val tmp = FloatArray(1)
            for (i in 1 until points.size) {
                Location.distanceBetween(
                    points[i - 1].latitude, points[i - 1].longitude,
                    points[i].latitude, points[i].longitude,
                    tmp
                )
                cumulativeDistances[i] = cumulativeDistances[i - 1] + tmp[0]
            }

            // Update toolbar subtitle with run date + total distance
            run?.let {
                val useMiles = SettingsManager.useMiles(requireContext())
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                activity?.title = getString(R.string.notification_content,
                    sdf.format(Date(it.dateTimestamp)),
                    TrackingUtils.formatDistance(it.distanceMeters, useMiles, requireContext()))
            }

            setupOverlays()
            fitMapToRoute()
            updateStatsAt(0)
            binding.seekBar.max = points.size - 1
            binding.seekBar.progress = 0
            binding.btnPlayPause.isEnabled = true
        }

        binding.btnPlayPause.setOnClickListener { togglePlayPause() }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            private var wasPlaying = false

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                wasPlaying = isPlaying
                if (isPlaying) pauseReplay()
                isScrubbing = true
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    currentIndex = progress
                    updatePositionAt(currentIndex)
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                isScrubbing = false
                if (wasPlaying) startReplay()
            }
        })
        binding.btn1x.setOnClickListener  { setSpeed(1f) }
        binding.btn2x.setOnClickListener  { setSpeed(2f) }
        binding.btn5x.setOnClickListener  { setSpeed(5f) }
        binding.btn10x.setOnClickListener { setSpeed(10f) }

        setSpeed(1f) // highlight 1x by default
    }

    private fun setupOverlays() {
        val geoPoints = points.map { GeoPoint(it.latitude, it.longitude) }

        // Full route — gray background
        routePolyline = Polyline().apply {
            setPoints(geoPoints)
            outlinePaint.color = ContextCompat.getColor(requireContext(), R.color.routeGray)
            outlinePaint.strokeWidth = 6f
        }

        // Completed portion — primary color
        completedPolyline = Polyline().apply {
            outlinePaint.color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            outlinePaint.strokeWidth = 8f
        }

        // Runner marker
        runnerMarker = Marker(binding.replayMapView).apply {
            position = geoPoints.first()
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_runner_marker)
            title = null
        }

        binding.replayMapView.overlays.addAll(listOf(routePolyline, completedPolyline, runnerMarker))
        binding.replayMapView.invalidate()
    }

    private fun fitMapToRoute() {
        if (points.size < 2) {
            binding.replayMapView.controller.apply {
                setZoom(16.0)
                setCenter(GeoPoint(points.first().latitude, points.first().longitude))
            }
            return
        }
        val lats = points.map { it.latitude }
        val lngs = points.map { it.longitude }
        val box = BoundingBox(lats.max(), lngs.max(), lats.min(), lngs.min())
        binding.replayMapView.post {
            binding.replayMapView.zoomToBoundingBox(box.increaseByScale(1.3f), true)
        }
    }

    private fun togglePlayPause() {
        if (isPlaying) pauseReplay() else startReplay()
    }

    private fun startReplay() {
        if (currentIndex >= points.size - 1) currentIndex = 0  // restart if finished
        isPlaying = true
        binding.btnPlayPause.setText(R.string.replay_pause)

        replayJob = lifecycleScope.launch {
            while (currentIndex < points.size - 1 && isActive) {
                updatePositionAt(currentIndex)
                val dt = points[currentIndex + 1].timestamp - points[currentIndex].timestamp
                delay((dt / speedMultiplier).toLong().coerceIn(30L, 2000L))
                currentIndex++
            }
            // Finished — show final position
            updatePositionAt(points.size - 1)
            isPlaying = false
            currentIndex = 0
            binding.btnPlayPause.setText(R.string.replay_replay)
        }
    }

    private fun pauseReplay() {
        replayJob?.cancel()
        isPlaying = false
        binding.btnPlayPause.setText(R.string.replay_play)
    }

    private fun updatePositionAt(index: Int) {
        val geoPoint = GeoPoint(points[index].latitude, points[index].longitude)
        runnerMarker?.position = geoPoint
        completedPolyline?.setPoints(
            points.take(index + 1).map { GeoPoint(it.latitude, it.longitude) }
        )
        binding.replayMapView.controller.animateTo(geoPoint)
        binding.replayMapView.invalidate()
        if (!isScrubbing) binding.seekBar.progress = index
        updateStatsAt(index)
    }

    private fun updateStatsAt(index: Int) {
        val elapsedMs = points[index].timestamp - points.first().timestamp
        binding.tvElapsed.text = TrackingUtils.formatTime(elapsedMs)
        val useMiles = context?.let { SettingsManager.useMiles(it) } ?: false
        binding.tvDistanceCovered.text = TrackingUtils.formatDistance(cumulativeDistances[index], useMiles, requireContext())
        binding.tvCurrentSpeed.text = TrackingUtils.formatSpeedKmh(points[index].speedMs * 3.6f, useMiles, requireContext())
    }

    private fun setSpeed(multiplier: Float) {
        speedMultiplier = multiplier
        val selectedColor = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        listOf(
            binding.btn1x  to 1f,
            binding.btn2x  to 2f,
            binding.btn5x  to 5f,
            binding.btn10x to 10f
        ).forEach { (btn, value) ->
            btn.setBackgroundColor(if (value == multiplier) selectedColor else "#00000000".toColorInt())
            btn.setTextColor(
                if (value == multiplier) "#FFFFFFFF".toColorInt()
                else selectedColor
            )
        }
    }

    override fun onResume() {
        super.onResume()
        binding.replayMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.replayMapView.onPause()
        pauseReplay()
    }

    override fun onDestroyView() {
        replayJob?.cancel()
        _binding = null
        super.onDestroyView()
    }
}
