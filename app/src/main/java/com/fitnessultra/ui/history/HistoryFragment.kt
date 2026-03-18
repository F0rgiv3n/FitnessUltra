package com.fitnessultra.ui.history

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.fitnessultra.R
import com.fitnessultra.data.db.entity.RunEntity
import com.fitnessultra.databinding.FragmentHistoryBinding
import java.text.SimpleDateFormat
import java.util.*

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var runAdapter: RunAdapter

    private var cachedRuns: List<RunEntity> = emptyList()
    private var selectedTab = 0  // 0=7d, 1=4w, 2=6m

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runAdapter = RunAdapter { run ->
            val bundle = Bundle().apply { putLong("runId", run.id) }
            findNavController().navigate(R.id.action_historyFragment_to_chartsFragment, bundle)
        }

        binding.rvRuns.apply {
            adapter = runAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        setupStepsChart()

        val swipeToDelete = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val run = runAdapter.currentList[viewHolder.adapterPosition]
                viewModel.deleteRun(run)
                Snackbar.make(requireView(), "Run deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") { viewModel.restoreRun(run) }
                    .show()
            }
        }
        ItemTouchHelper(swipeToDelete).attachToRecyclerView(binding.rvRuns)

        viewModel.runs.observe(viewLifecycleOwner) { runs ->
            cachedRuns = runs
            runAdapter.submitList(runs)
            binding.tvEmpty.visibility = if (runs.isEmpty()) View.VISIBLE else View.GONE
            binding.cardStepsHistory.visibility = if (runs.isEmpty()) View.GONE else View.VISIBLE
            updateStepsChart()
        }

        binding.tabsSteps.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                selectedTab = tab.position
                updateStepsChart()
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setupStepsChart() {
        binding.tabsSteps.apply {
            addTab(newTab().setText("Last 7 Days"))
            addTab(newTab().setText("Last 4 Weeks"))
            addTab(newTab().setText("Last 6 Months"))
        }

        binding.chartSteps.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            setDrawGridBackground(false)
            setFitBars(true)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.setDrawGridLines(false)
            axisLeft.setDrawGridLines(true)
            axisLeft.axisMinimum = 0f
            axisRight.isEnabled = false
            animateY(400)
        }
    }

    private fun updateStepsChart() {
        val (labels, values) = buildStepData(cachedRuns, selectedTab)

        val entries = values.mapIndexed { i, v -> BarEntry(i.toFloat(), v.toFloat()) }
        val primaryColor = ContextCompat.getColor(requireContext(), R.color.colorPrimary)

        val dataSet = BarDataSet(entries, "Steps").apply {
            color = primaryColor
            valueTextColor = Color.DKGRAY
            valueTextSize = 9f
            setDrawValues(true)
        }

        binding.chartSteps.apply {
            data = BarData(dataSet).also { it.barWidth = 0.6f }
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.labelCount = labels.size
            invalidate()
        }

        val total = values.sum()
        val periodLabel = when (selectedTab) { 0 -> "7 days"; 1 -> "4 weeks"; else -> "6 months" }
        binding.tvStepsTotalLabel.text = "Total: ${"%,d".format(total)} steps in $periodLabel"
    }

    private fun buildStepData(runs: List<RunEntity>, period: Int): Pair<List<String>, List<Int>> {
        val labels = mutableListOf<String>()
        val steps = mutableListOf<Int>()

        when (period) {
            0 -> { // Last 7 days — one bar per day
                val dayFmt = SimpleDateFormat("EEE", Locale.getDefault())
                for (i in 6 downTo 0) {
                    val start = dayStart(Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) })
                    val end   = dayStart(Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i + 1) })
                    labels.add(dayFmt.format(Date(start)))
                    steps.add(runs.filter { it.dateTimestamp in start until end }.sumOf { it.stepCount })
                }
            }
            1 -> { // Last 4 weeks — one bar per week
                val dateFmt = SimpleDateFormat("d MMM", Locale.getDefault())
                for (i in 3 downTo 0) {
                    val weekStart = Calendar.getInstance().apply {
                        add(Calendar.WEEK_OF_YEAR, -i)
                        set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
                        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                    }
                    val weekEnd = Calendar.getInstance().apply {
                        timeInMillis = weekStart.timeInMillis
                        add(Calendar.WEEK_OF_YEAR, 1)
                    }
                    labels.add(dateFmt.format(weekStart.time))
                    steps.add(runs.filter { it.dateTimestamp in weekStart.timeInMillis until weekEnd.timeInMillis }.sumOf { it.stepCount })
                }
            }
            else -> { // Last 6 months — one bar per month
                val monthFmt = SimpleDateFormat("MMM", Locale.getDefault())
                for (i in 5 downTo 0) {
                    val monthStart = Calendar.getInstance().apply {
                        add(Calendar.MONTH, -i)
                        set(Calendar.DAY_OF_MONTH, 1)
                        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                    }
                    val monthEnd = Calendar.getInstance().apply {
                        timeInMillis = monthStart.timeInMillis
                        add(Calendar.MONTH, 1)
                    }
                    labels.add(monthFmt.format(monthStart.time))
                    steps.add(runs.filter { it.dateTimestamp in monthStart.timeInMillis until monthEnd.timeInMillis }.sumOf { it.stepCount })
                }
            }
        }
        return Pair(labels, steps)
    }

    private fun dayStart(cal: Calendar): Long = cal.apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
