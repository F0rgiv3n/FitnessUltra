package com.fitnessultra.ui.run

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fitnessultra.R
import com.fitnessultra.databinding.BottomSheetTargetPaceBinding
import com.fitnessultra.util.SettingsManager
import com.fitnessultra.util.TrackingUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TargetPaceSetupSheet : BottomSheetDialogFragment() {

    var onApply: ((WorkoutConfig.TargetPace) -> Unit)? = null

    private var _binding: BottomSheetTargetPaceBinding? = null
    private val binding get() = _binding!!

    private var paceMin = 6
    private var paceSec = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetTargetPaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val unitLabel = TrackingUtils.distanceUnitLabel(SettingsManager.useMiles(requireContext()), requireContext())
        binding.tvPaceUnit.text = getString(R.string.target_pace_label, unitLabel)

        updateDisplay()

        binding.btnPaceMinPlus.setOnClickListener  { paceMin = (paceMin + 1).coerceAtMost(20); updateDisplay() }
        binding.btnPaceMinMinus.setOnClickListener { paceMin = (paceMin - 1).coerceAtLeast(2); updateDisplay() }
        binding.btnPaceSecPlus.setOnClickListener  { paceSec = (paceSec + 5).let { if (it >= 60) 0  else it }; updateDisplay() }
        binding.btnPaceSecMinus.setOnClickListener { paceSec = (paceSec - 5).let { if (it < 0)  55 else it }; updateDisplay() }

        binding.btnApplyPace.setOnClickListener {
            onApply?.invoke(WorkoutConfig.TargetPace(paceSecPerUnit = paceMin * 60 + paceSec))
            dismiss()
        }
    }

    private fun updateDisplay() {
        binding.tvPaceMin.text = paceMin.toString()
        binding.tvPaceSec.text = "%02d".format(paceSec)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
