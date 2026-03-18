package com.fitnessultra.ui.weight

import androidx.core.graphics.toColorInt
import android.os.Bundle
import com.fitnessultra.util.SettingsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.fitnessultra.data.db.entity.WeightEntry
import com.fitnessultra.databinding.FragmentWeightBinding
import java.text.SimpleDateFormat
import java.util.*

class WeightFragment : Fragment() {

    private var _binding: FragmentWeightBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WeightViewModel by viewModels()
    private var currentEntries: List<WeightEntry> = emptyList()

    private fun useLbs() = SettingsManager.useLbs(requireContext())
    private fun weightUnit() = if (useLbs()) "lbs" else "kg"
    private fun displayWeight(kg: Float) = if (useLbs()) kg * 2.20462f else kg
    private fun toKg(displayValue: Float) = if (useLbs()) displayValue / 2.20462f else displayValue

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWeightBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupChart(binding.chartWeight)
        setupChart(binding.chartBmi)

        // Update weight input hint to reflect current unit
        binding.tilWeight.hint = "Weight (${weightUnit()})"

        // Tap on weight chart point → Edit/Delete dialog
        binding.chartWeight.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry, h: Highlight) {
                val index = e.x.toInt()
                if (index in currentEntries.indices) {
                    showEntryDialog(currentEntries[index])
                }
                binding.chartWeight.highlightValues(null)
            }
            override fun onNothingSelected() {}
        })

        val savedHeight = viewModel.getHeightCm()
        val savedAge = viewModel.getAge()
        if (savedHeight > 0f) showInfoSummary(savedHeight, savedAge) else showInfoForm()

        binding.btnSaveInfo.setOnClickListener {
            val height = binding.etHeight.text.toString().toFloatOrNull()
            val age = binding.etAge.text.toString().toIntOrNull()
            if (height == null || height <= 0f) {
                Toast.makeText(requireContext(), "Enter a valid height", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.saveUserInfo(height, age ?: 0)
            showInfoSummary(height, age ?: 0)
            viewModel.weightEntries.value?.let { updateAll(it) }
        }

        binding.btnEditInfo.setOnClickListener { showInfoForm() }

        binding.btnSaveWeight.setOnClickListener {
            val inputValue = binding.etWeight.text.toString().toFloatOrNull()
            if (inputValue == null || inputValue <= 0f) {
                Toast.makeText(requireContext(), "Enter a valid weight", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.saveWeight(toKg(inputValue))
            binding.etWeight.text?.clear()
        }

        viewModel.weightEntries.observe(viewLifecycleOwner) { entries ->
            currentEntries = entries
            updateAll(entries)
        }
    }

    private fun showEntryDialog(entry: WeightEntry) {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dateStr = sdf.format(Date(entry.dateTimestamp))
        val unit = weightUnit()

        AlertDialog.Builder(requireContext())
            .setTitle("Entry – $dateStr")
            .setMessage("Weight: %.1f %s".format(displayWeight(entry.weightKg), unit))
            .setPositiveButton("Edit") { _, _ -> showEditDialog(entry) }
            .setNegativeButton("Delete") { _, _ -> showDeleteConfirm(entry) }
            .setNeutralButton("Cancel", null)
            .show()
    }

    private fun showEditDialog(entry: WeightEntry) {
        val unit = weightUnit()
        val input = EditText(requireContext()).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText("%.1f".format(displayWeight(entry.weightKg)))
            selectAll()
            setPadding(48, 24, 48, 24)
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Edit weight ($unit)")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val inputVal = input.text.toString().toFloatOrNull()
                if (inputVal != null && inputVal > 0f) {
                    viewModel.updateEntry(entry, toKg(inputVal))
                } else {
                    Toast.makeText(requireContext(), "Invalid value", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirm(entry: WeightEntry) {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val unit = weightUnit()
        AlertDialog.Builder(requireContext())
            .setTitle("Delete entry")
            .setMessage("Delete ${sdf.format(Date(entry.dateTimestamp))} – %.1f %s?".format(displayWeight(entry.weightKg), unit))
            .setPositiveButton("Delete") { _, _ -> viewModel.deleteEntry(entry) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showInfoSummary(heightCm: Float, age: Int) {
        binding.layoutInfoForm.visibility = View.GONE
        binding.layoutInfoSummary.visibility = View.VISIBLE
        val agePart = if (age > 0) " · Age: $age" else ""
        binding.tvInfoSummary.text = "Height: ${heightCm.toInt()} cm$agePart"
    }

    private fun showInfoForm() {
        binding.layoutInfoSummary.visibility = View.GONE
        binding.layoutInfoForm.visibility = View.VISIBLE
        val savedHeight = viewModel.getHeightCm()
        val savedAge = viewModel.getAge()
        if (savedHeight > 0f) binding.etHeight.setText(savedHeight.toInt().toString())
        if (savedAge > 0) binding.etAge.setText(savedAge.toString())
    }

    private fun updateAll(entries: List<WeightEntry>) {
        if (entries.isEmpty()) return

        val unit = weightUnit()
        val sdf = SimpleDateFormat("dd/MM", Locale.getDefault())
        val labels = entries.map { sdf.format(Date(it.dateTimestamp)) }
        val weightsKg = entries.map { it.weightKg }
        val weightsDisplay = weightsKg.map { displayWeight(it) }

        if (entries.size > 1) {
            val diff = weightsDisplay.last() - weightsDisplay[weightsDisplay.size - 2]
            val sign = if (diff >= 0) "+" else ""
            val color = if (diff <= 0) "#388E3C".toColorInt() else "#D32F2F".toColorInt()
            binding.tvWeightDiff.text = "Change from last entry: ${sign}%.1f %s".format(diff, unit)
            binding.tvWeightDiff.setTextColor(color)
            binding.tvWeightDiff.visibility = View.VISIBLE
        }

        val bmi = viewModel.calculateBmi(weightsKg.last())
        if (bmi != null) {
            binding.tvCurrentBmi.text = "BMI: %.1f".format(bmi)
            binding.tvCurrentBmi.setTextColor(bmiColor(bmi))
            binding.tvBmiCategory.text = viewModel.bmiCategory(bmi)
            binding.layoutBmiCurrent.visibility = View.VISIBLE
            binding.bmiGauge.bmi = bmi
            binding.cardBmiGauge.visibility = View.VISIBLE
        }

        buildColoredChart(binding.chartWeight, weightsDisplay, labels)

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

    private fun buildColoredChart(chart: LineChart, values: List<Float>, labels: List<String>) {
        if (values.size < 2) {
            chart.clear()
            chart.setNoDataText("Add more entries to see chart")
            chart.invalidate()
            return
        }

        val dataSets = mutableListOf<ILineDataSet>()
        for (i in 0 until values.size - 1) {
            val color = if (values[i + 1] <= values[i]) "#388E3C".toColorInt() else "#D32F2F".toColorInt()
            val ds = LineDataSet(
                listOf(Entry(i.toFloat(), values[i]), Entry((i + 1).toFloat(), values[i + 1])), ""
            ).apply {
                this.color = color
                setCircleColor(color)
                circleRadius = 6f
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
        bmi < 18.5f -> "#1565C0".toColorInt()
        bmi < 25f   -> "#388E3C".toColorInt()
        bmi < 30f   -> "#F57F17".toColorInt()
        else        -> "#D32F2F".toColorInt()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
