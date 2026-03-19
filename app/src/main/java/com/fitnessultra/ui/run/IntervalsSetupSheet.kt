package com.fitnessultra.ui.run

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fitnessultra.R
import com.fitnessultra.databinding.BottomSheetIntervalsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class IntervalsSetupSheet : BottomSheetDialogFragment() {

    var onApply: ((WorkoutConfig.Intervals) -> Unit)? = null

    private var _binding: BottomSheetIntervalsBinding? = null
    private val binding get() = _binding!!

    private var runSec = 60
    private var walkSec = 30
    private var reps = 5

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetIntervalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateDisplay()

        binding.btnRunMinus.setOnClickListener  { runSec  = (runSec  - 5).coerceAtLeast(5);  updateDisplay() }
        binding.btnRunPlus.setOnClickListener   { runSec  = (runSec  + 5).coerceAtMost(600); updateDisplay() }
        binding.btnWalkMinus.setOnClickListener { walkSec = (walkSec - 5).coerceAtLeast(5);  updateDisplay() }
        binding.btnWalkPlus.setOnClickListener  { walkSec = (walkSec + 5).coerceAtMost(600); updateDisplay() }
        binding.btnRepsMinus.setOnClickListener { reps    = (reps    - 1).coerceAtLeast(1);  updateDisplay() }
        binding.btnRepsPlus.setOnClickListener  { reps    = (reps    + 1).coerceAtMost(20);  updateDisplay() }

        binding.btnApplyIntervals.setOnClickListener {
            onApply?.invoke(WorkoutConfig.Intervals(runSec, walkSec, reps))
            dismiss()
        }
    }

    private fun updateDisplay() {
        binding.tvRunSec.text  = runSec.toString()
        binding.tvWalkSec.text = walkSec.toString()
        binding.tvReps.text    = reps.toString()
        val totalSec = reps * (runSec + walkSec)
        binding.tvSummary.text = getString(
            R.string.sheet_interval_summary,
            reps, runSec, walkSec,
            totalSec / 60, totalSec % 60
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
