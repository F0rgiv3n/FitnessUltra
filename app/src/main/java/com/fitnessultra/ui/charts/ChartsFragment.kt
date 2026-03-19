package com.fitnessultra.ui.charts

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.fitnessultra.R
import com.fitnessultra.databinding.FragmentChartsBinding
import com.fitnessultra.util.GpxExporter
import com.fitnessultra.util.SettingsManager
import com.fitnessultra.util.TrackingUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
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

        binding.btnReplayRoute.setOnClickListener {
            val bundle = android.os.Bundle().apply { putLong("runId", runId) }
            findNavController().navigate(R.id.action_chartsFragment_to_replayFragment, bundle)
        }

        binding.btnExportGpx.setOnClickListener { exportGpx(runId) }

        val useMiles = SettingsManager.useMiles(requireContext())
        val speedLabel = getString(R.string.chart_label_speed, TrackingUtils.speedUnitLabel(useMiles, requireContext()))
        val paceLabel  = getString(R.string.chart_label_pace, TrackingUtils.distanceUnitLabel(useMiles, requireContext()))

        lifecycleScope.launch {
            val run = viewModel.getRunById(runId)
            if (run != null) {
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                binding.tvRunDate.text = sdf.format(Date(run.dateTimestamp))
                binding.tvRunDistance.text = TrackingUtils.formatDistance(run.distanceMeters, useMiles, requireContext())
                binding.tvRunDuration.text = TrackingUtils.formatTime(run.durationMillis)
                binding.tvRunCalories.text = getString(R.string.calories_format, run.caloriesBurned)
                binding.tvRunSteps.text = if (run.stepCount > 0)
                    getString(R.string.steps_format, run.stepCount)
                else getString(R.string.label_not_available)

                // Cadence
                val cadence = if (run.durationMillis > 0 && run.stepCount > 0)
                    (run.stepCount * 60000L / run.durationMillis).toInt()
                else 0
                binding.tvRunCadence.text = if (cadence > 0)
                    getString(R.string.cadence_format, cadence)
                else getString(R.string.label_not_available)
            }

            // Split table (independent of location points)
            val splits = viewModel.getSplits(runId)
            if (splits.isNotEmpty()) {
                binding.splitCard.visibility = View.VISIBLE
                splits.forEach { split ->
                    val row = buildSplitRow(split.kmNumber.toString(), split.splitMs, useMiles)
                    binding.splitTableContainer.addView(row)
                }
                val best = splits.minByOrNull { it.splitMs }!!
                binding.bestSplitRow.visibility = View.VISIBLE
                binding.tvBestSplitTime.text = TrackingUtils.formatTime(best.splitMs)
                binding.tvBestSplitPace.text = TrackingUtils.calculatePace(
                    if (useMiles) 1609.344f else 1000f,
                    best.splitMs,
                    useMiles,
                    requireContext()
                )
            }

            val points = viewModel.getLocationPoints(runId)
            if (points.isEmpty()) return@launch

            val startTime = points.first().timestamp.toFloat()

            // Speed chart (convert m/s → display unit)
            val speedMultiplier = if (useMiles) 3.6f * 0.621371f else 3.6f
            val speedEntries = points.map { p ->
                Entry((p.timestamp - startTime) / 1000f, p.speedMs * speedMultiplier)
            }
            val speedDataSet = LineDataSet(speedEntries, speedLabel).apply {
                color = Color.BLUE
                setCircleColor(Color.BLUE)
                circleRadius = 2f
                setDrawValues(false)
            }
            binding.chartSpeed.data = LineData(speedDataSet)
            binding.chartSpeed.invalidate()

            // Elevation chart (always in metres)
            val elevEntries = points.map { p ->
                Entry((p.timestamp - startTime) / 1000f, p.altitude.toFloat())
            }
            val elevDataSet = LineDataSet(elevEntries, getString(R.string.chart_label_altitude)).apply {
                color = Color.GREEN
                setCircleColor(Color.GREEN)
                circleRadius = 2f
                setDrawValues(false)
            }
            binding.chartElevation.data = LineData(elevDataSet)
            binding.chartElevation.invalidate()

            // Pace chart — x axis in display distance units
            val unitMeters = if (useMiles) 1609.344f else 1000f
            val paceEntries = mutableListOf<Entry>()
            var cumulativeUnits = 0f
            for (i in 1 until points.size) {
                val dTime = (points[i].timestamp - points[i - 1].timestamp).toFloat() / 1000f / 60f
                val dMeters = points[i].speedMs * ((points[i].timestamp - points[i - 1].timestamp) / 1000f)
                cumulativeUnits += dMeters / unitMeters
                val dUnits = dMeters / unitMeters
                if (dUnits > 0.001f) {
                    val pace = dTime / dUnits
                    paceEntries.add(Entry(cumulativeUnits, pace.coerceAtMost(20f)))
                }
            }
            val paceDataSet = LineDataSet(paceEntries, paceLabel).apply {
                color = Color.RED
                setCircleColor(Color.RED)
                circleRadius = 2f
                setDrawValues(false)
            }
            binding.chartPace.data = LineData(paceDataSet)
            binding.chartPace.invalidate()
        }
    }

    private fun exportGpx(runId: Long) {
        lifecycleScope.launch {
            val run = viewModel.getRunById(runId)
            val points = viewModel.getLocationPoints(runId)
            if (run == null || points.isEmpty()) {
                Toast.makeText(requireContext(), R.string.msg_gpx_no_data, Toast.LENGTH_SHORT).show()
                return@launch
            }
            val gpxContent = withContext(Dispatchers.IO) { GpxExporter.generate(run, points) }
            val sdf = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US)
            val filename = getString(R.string.gpx_filename, sdf.format(Date(run.dateTimestamp)))
            val gpxFile = withContext(Dispatchers.IO) {
                val dir = File(requireContext().cacheDir, "gpx")
                dir.mkdirs()
                val f = File(dir, filename)
                f.writeText(gpxContent)
                f
            }
            val uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                gpxFile
            )
            val share = Intent(Intent.ACTION_SEND).apply {
                type = "application/gpx+xml"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, filename)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(share, getString(R.string.gpx_share_title)))
        }
    }

    private fun buildSplitRow(label: String, splitMs: Long, useMiles: Boolean): android.widget.LinearLayout {
        val row = android.widget.LinearLayout(requireContext()).apply {
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = android.widget.LinearLayout.HORIZONTAL
            setPadding(0, 6, 0, 6)
        }
        fun cell(text: String, weight: Float, bold: Boolean = false) = android.widget.TextView(requireContext()).apply {
            layoutParams = android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, weight)
            this.text = text
            textSize = 14f
            gravity = android.view.Gravity.CENTER
            if (bold) setTypeface(null, android.graphics.Typeface.BOLD)
        }
        val distanceM = if (useMiles) 1609.344f else 1000f
        row.addView(cell(label, 1f, bold = true))
        row.addView(cell(TrackingUtils.formatTime(splitMs), 2f))
        row.addView(cell(TrackingUtils.calculatePace(distanceM, splitMs, useMiles, requireContext()), 2f))
        return row
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
