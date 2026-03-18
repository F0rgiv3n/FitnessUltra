package com.fitnessultra.ui.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.fitnessultra.R
import com.fitnessultra.data.db.entity.RunEntity
import com.fitnessultra.databinding.FragmentGoalsBinding
import com.fitnessultra.util.SettingsManager
import com.fitnessultra.util.TrackingUtils
import java.text.SimpleDateFormat
import java.util.*

class GoalsFragment : Fragment() {

    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GoalsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGoalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupWeekHeader()

        binding.btnEditDistanceGoal.setOnClickListener { showEditDialog(GoalType.DISTANCE) }
        binding.btnEditTimeGoal.setOnClickListener    { showEditDialog(GoalType.TIME) }
        binding.btnEditStepsGoal.setOnClickListener   { showEditDialog(GoalType.STEPS) }

        viewModel.weeklyRuns.observe(viewLifecycleOwner) { runs ->
            updateDistanceCard(runs)
            updateTimeCard(runs)
            updateStepsCard(runs)
            updateDayDots(runs)
        }
    }

    // ── Week header ──────────────────────────────────────────────────────────

    private fun setupWeekHeader() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        val start = cal.time
        cal.add(Calendar.DAY_OF_WEEK, 6)
        val end = cal.time
        val sdf = SimpleDateFormat("d MMM", Locale.getDefault())
        binding.tvWeekLabel.text = getString(R.string.goal_week_label, sdf.format(start), sdf.format(end))
    }

    private fun updateDayDots(runs: List<RunEntity>) {
        binding.llDayDots.removeAllViews()

        val dayNames = resources.getStringArray(R.array.week_day_names)
        val primaryColor = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        val inactiveColor = ContextCompat.getColor(requireContext(), R.color.routeGray)

        // Determine which calendar days of the week had a run
        val activeDays = mutableSetOf<Int>()
        runs.forEach { run ->
            val c = Calendar.getInstance().apply { timeInMillis = run.dateTimestamp }
            activeDays.add(c.get(Calendar.DAY_OF_WEEK))
        }

        val firstDay = Calendar.getInstance().firstDayOfWeek
        val context = requireContext()
        for (i in 0..6) {
            val dayOfWeek = ((firstDay - 1 + i) % 7) + 1
            val col = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = android.view.Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            val dot = View(context).apply {
                val size = resources.getDimensionPixelSize(R.dimen.day_dot_size)
                layoutParams = LinearLayout.LayoutParams(size, size).also { it.bottomMargin = 4 }
                background = ContextCompat.getDrawable(context, R.drawable.bg_day_dot)
                backgroundTintList = android.content.res.ColorStateList.valueOf(
                    if (dayOfWeek in activeDays) primaryColor else inactiveColor
                )
            }
            val label = TextView(context).apply {
                text = dayNames[i]
                textSize = 10f
                gravity = android.view.Gravity.CENTER
            }
            col.addView(dot)
            col.addView(label)
            binding.llDayDots.addView(col)
        }
    }

    // ── Goal cards ────────────────────────────────────────────────────────────

    private fun updateDistanceCard(runs: List<RunEntity>) {
        val useMiles = SettingsManager.useMiles(requireContext())
        val actualMeters = runs.sumOf { it.distanceMeters.toDouble() }.toFloat()
        val actualDisplay = TrackingUtils.fromKm(actualMeters / 1000f, useMiles)
        val goalDisplay   = TrackingUtils.fromKm(viewModel.goalDistanceKm, useMiles)
        val unit = TrackingUtils.distanceUnitLabel(useMiles, requireContext())
        val pct = ((actualDisplay / goalDisplay) * 100).toInt().coerceAtMost(100)
        binding.progressDistance.progress = pct
        binding.tvDistanceValue.text = getString(R.string.goal_progress_distance, actualDisplay, goalDisplay, unit)
        binding.tvDistancePct.text = getString(R.string.percent_format, pct)
        setProgressColor(binding.progressDistance, pct)
    }

    private fun updateTimeCard(runs: List<RunEntity>) {
        val actualMin = runs.sumOf { it.durationMillis } / 60000
        val goalMin = viewModel.goalTimeMinutes.toLong()
        val pct = ((actualMin.toFloat() / goalMin) * 100).toInt().coerceAtMost(100)
        binding.progressTime.progress = pct
        binding.tvTimeValue.text = getString(R.string.goal_progress_min, actualMin, goalMin)
        binding.tvTimePct.text = getString(R.string.percent_format, pct)
        setProgressColor(binding.progressTime, pct)
    }

    private fun updateStepsCard(runs: List<RunEntity>) {
        val actual = runs.sumOf { it.stepCount }
        val goal = viewModel.goalSteps
        val pct = ((actual.toFloat() / goal) * 100).toInt().coerceAtMost(100)
        binding.progressSteps.progress = pct
        binding.tvStepsValue.text = getString(R.string.goal_progress_steps, "%,d".format(actual), "%,d".format(goal))
        binding.tvStepsPct.text = getString(R.string.percent_format, pct)
        setProgressColor(binding.progressSteps, pct)
    }

    private fun setProgressColor(bar: com.google.android.material.progressindicator.LinearProgressIndicator, pct: Int) {
        val color = when {
            pct >= 100 -> ContextCompat.getColor(requireContext(), R.color.goalComplete)
            pct >= 60  -> ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            else       -> ContextCompat.getColor(requireContext(), R.color.goalLow)
        }
        bar.setIndicatorColor(color)
    }

    // ── Edit dialog ───────────────────────────────────────────────────────────

    private enum class GoalType { DISTANCE, TIME, STEPS }

    private fun showEditDialog(type: GoalType) {
        val useMiles = SettingsManager.useMiles(requireContext())
        val (title, hint, current) = when (type) {
            GoalType.DISTANCE -> Triple(
                getString(R.string.goal_edit_distance_title),
                getString(R.string.goal_edit_distance_hint, TrackingUtils.distanceUnitLabel(useMiles, requireContext())),
                TrackingUtils.fromKm(viewModel.goalDistanceKm, useMiles).toInt().toString()
            )
            GoalType.TIME -> Triple(
                getString(R.string.goal_edit_time_title),
                getString(R.string.goal_edit_time_hint),
                viewModel.goalTimeMinutes.toString()
            )
            GoalType.STEPS -> Triple(
                getString(R.string.goal_edit_steps_title),
                getString(R.string.goal_edit_steps_hint),
                viewModel.goalSteps.toString()
            )
        }

        val input = EditText(requireContext()).apply {
            setText(current)
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setHint(hint)
            setPadding(48, 24, 48, 8)
            selectAll()
        }

        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(input)
            .setPositiveButton(R.string.btn_save_info) { _, _ ->
                val value = input.text.toString().toIntOrNull() ?: return@setPositiveButton
                when (type) {
                    GoalType.DISTANCE -> viewModel.goalDistanceKm = TrackingUtils.toKm(value.toFloat(), useMiles)
                    GoalType.TIME     -> viewModel.goalTimeMinutes = value
                    GoalType.STEPS    -> viewModel.goalSteps = value
                }
                // Re-trigger update with the same run list
                viewModel.weeklyRuns.value?.let { runs ->
                    updateDistanceCard(runs)
                    updateTimeCard(runs)
                    updateStepsCard(runs)
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
