package com.fitnessultra.ui.weight

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
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

        setupChart()

        binding.btnSaveWeight.setOnClickListener {
            val input = binding.etWeight.text.toString().toFloatOrNull()
            if (input == null || input <= 0f) {
                Toast.makeText(requireContext(), "Enter a valid weight", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.saveWeight(input)
            // Persist as default user weight for calorie calculation
            requireActivity().getSharedPreferences("user_prefs", 0).edit()
                .putFloat("weight_kg", input).apply()
            binding.etWeight.text?.clear()
        }

        viewModel.weightEntries.observe(viewLifecycleOwner) { entries ->
            updateChart(entries)
            updateBmi(entries)
        }
    }

    private fun setupChart() {
        binding.chartWeight.apply {
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(true)
            setDragEnabled(true)
            setScaleEnabled(true)
        }
    }

    private fun updateChart(entries: List<WeightEntry>) {
        if (entries.isEmpty()) return

        val barEntries = entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.weightKg)
        }

        val colors = entries.mapIndexed { index, entry ->
            if (index == 0) Color.GRAY
            else if (entry.weightKg > entries[index - 1].weightKg) Color.RED else Color.GREEN
        }

        val sdf = SimpleDateFormat("dd/MM", Locale.getDefault())
        val labels = entries.map { sdf.format(Date(it.dateTimestamp)) }
        binding.chartWeight.xAxis.valueFormatter =
            com.github.mikephil.charting.formatter.IndexAxisValueFormatter(labels)

        val dataSet = BarDataSet(barEntries, "Weight").apply {
            this.colors = colors
            setDrawValues(true)
        }

        binding.chartWeight.data = BarData(dataSet)
        binding.chartWeight.invalidate()
    }

    private fun updateBmi(entries: List<WeightEntry>) {
        if (entries.isEmpty()) return
        val lastWeight = entries.last().weightKg
        val prefs = requireActivity().getSharedPreferences("user_prefs", 0)
        val heightM = prefs.getFloat("height_m", 0f)
        if (heightM > 0f) {
            val bmi = lastWeight / (heightM * heightM)
            binding.tvBmi.text = "BMI: %.1f".format(bmi)
            binding.tvBmi.visibility = View.VISIBLE
        }
        if (entries.size > 1) {
            val diff = lastWeight - entries[entries.size - 2].weightKg
            val sign = if (diff >= 0) "+" else ""
            binding.tvWeightDiff.text = "Change: ${sign}%.1f kg".format(diff)
            binding.tvWeightDiff.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
