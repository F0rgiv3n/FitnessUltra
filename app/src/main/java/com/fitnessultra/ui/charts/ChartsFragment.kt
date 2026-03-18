package com.fitnessultra.ui.charts

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.fitnessultra.databinding.FragmentChartsBinding
import com.fitnessultra.util.TrackingUtils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ChartsFragment : Fragment() {

    private var _binding: FragmentChartsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChartsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChartsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCharts()

        val runId = arguments?.getLong("runId") ?: return

        lifecycleScope.launch {
            val run = viewModel.getRunById(runId)
            if (run != null) {
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                binding.tvRunDate.text = sdf.format(Date(run.dateTimestamp))
                binding.tvRunDistance.text = TrackingUtils.formatDistance(run.distanceMeters)
                binding.tvRunDuration.text = TrackingUtils.formatTime(run.durationMillis)
                binding.tvRunCalories.text = "${run.caloriesBurned} kcal"
                binding.tvRunSteps.text = if (run.stepCount > 0) "${run.stepCount} steps" else "-"
            }

            val points = viewModel.getLocationPoints(runId)
            if (points.isEmpty()) return@launch

            val startTime = points.first().timestamp.toFloat()

            // Speed chart
            val speedEntries = points.map { p ->
                Entry((p.timestamp - startTime) / 1000f, p.speedMs * 3.6f)
            }
            val speedDataSet = LineDataSet(speedEntries, "Speed (km/h)").apply {
                color = Color.BLUE
                setCircleColor(Color.BLUE)
                circleRadius = 2f
                setDrawValues(false)
            }
            binding.chartSpeed.data = LineData(speedDataSet)
            binding.chartSpeed.invalidate()

            // Elevation chart
            val elevEntries = points.map { p ->
                Entry((p.timestamp - startTime) / 1000f, p.altitude.toFloat())
            }
            val elevDataSet = LineDataSet(elevEntries, "Altitude (m)").apply {
                color = Color.GREEN
                setCircleColor(Color.GREEN)
                circleRadius = 2f
                setDrawValues(false)
            }
            binding.chartElevation.data = LineData(elevDataSet)
            binding.chartElevation.invalidate()

            // Pace chart (cumulative distance vs pace)
            val paceEntries = mutableListOf<Entry>()
            var cumulativeDistance = 0f
            for (i in 1 until points.size) {
                val dTime = (points[i].timestamp - points[i - 1].timestamp).toFloat() / 1000f / 60f
                val dDist = points[i].speedMs * ((points[i].timestamp - points[i - 1].timestamp) / 1000f) / 1000f
                cumulativeDistance += dDist
                if (dDist > 0.01f) {
                    val pace = dTime / dDist
                    paceEntries.add(Entry(cumulativeDistance, pace.coerceAtMost(20f)))
                }
            }
            val paceDataSet = LineDataSet(paceEntries, "Pace (min/km)").apply {
                color = Color.RED
                setCircleColor(Color.RED)
                circleRadius = 2f
                setDrawValues(false)
            }
            binding.chartPace.data = LineData(paceDataSet)
            binding.chartPace.invalidate()
        }
    }

    private fun setupCharts() {
        listOf(binding.chartSpeed, binding.chartElevation, binding.chartPace).forEach { chart ->
            chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            chart.description.isEnabled = false
            chart.legend.isEnabled = true
            chart.setTouchEnabled(true)
            chart.setDragEnabled(true)
            chart.setScaleEnabled(true)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
