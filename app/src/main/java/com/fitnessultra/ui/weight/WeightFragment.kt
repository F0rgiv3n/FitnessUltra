package com.fitnessultra.ui.weight

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.fitnessultra.data.db.entity.WeightEntry
import com.fitnessultra.databinding.FragmentWeightBinding
import java.text.SimpleDateFormat
import java.util.*

class WeightFragment : Fragment() {

    private var _binding: FragmentWeightBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WeightViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWeightBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Pre-fill saved personal info
        val savedHeight = viewModel.getHeightCm()
        val savedAge = viewModel.getAge()
        if (savedHeight > 0f) binding.etHeight.setText(savedHeight.toInt().toString())
        if (savedAge > 0) binding.etAge.setText(savedAge.toString())

        setupChart(binding.chartWeight)
        setupChart(binding.chartBmi)

        binding.btnSaveInfo.setOnClickListener {
            val height = binding.etHeight.text.toString().toFloatOrNull()
            val age = binding.etAge.text.toString().toIntOrNull()
            if (height == null || height <= 0f) {
                Toast.makeText(requireContext(), "Enter a valid height", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.saveUserInfo(height, age ?: 0)
            Toast.makeText(requireContext(), "Info saved", Toast.LENGTH_SHORT).show()
            // Refresh charts with new height
            viewModel.weightEntries.value?.let { updateAll(it) }
        }

        binding.btnSaveWeight.setOnClickListener {
            val weight = binding.etWeight.text.toString().toFloatOrNull()
            if (weight == null || weight <= 0f) {
                Toast.makeText(requireContext(), "Enter a valid weight", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.saveWeight(weight)
            binding.etWeight.text?.clear()
        }

        viewModel.weightEntries.observe(viewLifecycleOwner) { entries ->
            updateAll(entries)
        }
    }

    private fun updateAll(entries: List<WeightEntry>) {
        if (entries.isEmpty()) return

        val sdf = SimpleDateFormat("dd/MM", Locale.getDefault())
        val labels = entries.map { sdf.format(Date(it.dateTimestamp)) }
        val weights = entries.map { it.weightKg }

        // Weight diff label
        if (entries.size > 1) {
            val diff = weights.last() - weights[weights.size - 2]
            val sign = if (diff >= 0) "+" else ""
            val color = if (diff <= 0) Color.parseColor("#388E3C") else Color.RED
            binding.tvWeightDiff.text = "Change from last entry: ${sign}%.1f kg".format(diff)
            binding.tvWeightDiff.setTextColor(color)
            binding.tvWeightDiff.visibility = View.VISIBLE
        }

        // Current BMI
        val bmi = viewModel.calculateBmi(weights.last())
        if (bmi != null) {
            binding.tvCurrentBmi.text = "BMI: %.1f".format(bmi)
            binding.tvCurrentBmi.setTextColor(bmiColor(bmi))
            binding.tvBmiCategory.text = viewModel.bmiCategory(bmi)
            binding.layoutBmiCurrent.visibility = View.VISIBLE
        }

        // Weight chart
        buildColoredChart(binding.chartWeight, weights, labels)

        // BMI chart
        val bmiValues = entries.mapNotNull { viewModel.calculateBmi(it.weightKg) }
        if (bmiValues.size >= 2) {
            binding.tvBmiChartNote.visibility = View.GONE
            buildColoredChart(binding.chartBmi, bmiValues, labels)
        } else {
            binding.tvBmiChartNote.visibility = View.VISIBLE
            binding.chartBmi.clear()
            binding.chartBmi.setNoDataText("Enter height to calculate BMI")
            binding.chartBmi.invalidate()
        }
    }

    /**
     * Builds a line chart where each segment is green if value decreased, red if increased.
     */
    private fun buildColoredChart(chart: LineChart, values: List<Float>, labels: List<String>) {
        if (values.size < 2) {
            chart.clear()
            chart.setNoDataText("Add more entries to see chart")
            chart.invalidate()
            return
        }

        val dataSets = mutableListOf<ILineDataSet>()

        for (i in 0 until values.size - 1) {
            val color = if (values[i + 1] <= values[i]) Color.parseColor("#388E3C") else Color.RED
            val ds = LineDataSet(
                listOf(Entry(i.toFloat(), values[i]), Entry((i + 1).toFloat(), values[i + 1])),
                ""
            ).apply {
                this.color = color
                setCircleColor(color)
                circleRadius = 5f
                lineWidth = 2.5f
                setDrawValues(true)
                valueTextSize = 10f
                mode = LineDataSet.Mode.LINEAR
            }
            dataSets.add(ds)
        }

        chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        chart.data = LineData(dataSets)
        chart.invalidate()
    }

    private fun setupChart(chart: LineChart) {
        chart.apply {
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(true)
            setDragEnabled(true)
            setScaleEnabled(true)
            setNoDataText("No entries yet")
        }
    }

    private fun bmiColor(bmi: Float): Int = when {
        bmi < 18.5f -> Color.parseColor("#1565C0") // blue - underweight
        bmi < 25f   -> Color.parseColor("#388E3C") // green - normal
        bmi < 30f   -> Color.parseColor("#F57F17") // orange - overweight
        else        -> Color.RED                    // red - obese
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
