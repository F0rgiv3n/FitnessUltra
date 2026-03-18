package com.fitnessultra.ui.run

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.fitnessultra.R
import com.fitnessultra.databinding.FragmentWorkoutSetupBinding
import com.fitnessultra.util.SettingsManager
import com.fitnessultra.util.TrackingUtils

class WorkoutSetupBottomSheet : BottomSheetDialogFragment() {

    var onStart: ((WorkoutConfig) -> Unit)? = null

    private var _binding: FragmentWorkoutSetupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWorkoutSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val unitLabel = TrackingUtils.distanceUnitLabel(SettingsManager.useMiles(requireContext()), requireContext())
        binding.tvPaceLabel.text = getString(R.string.target_pace_label, unitLabel)

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            binding.layoutIntervals.visibility =
                if (checkedId == R.id.radioIntervals) View.VISIBLE else View.GONE
            binding.layoutTargetPace.visibility =
                if (checkedId == R.id.radioTargetPace) View.VISIBLE else View.GONE
        }

        binding.btnStartWorkout.setOnClickListener {
            onStart?.invoke(buildConfig())
            dismiss()
        }
    }

    private fun buildConfig(): WorkoutConfig = when (binding.radioGroup.checkedRadioButtonId) {
        R.id.radioIntervals -> WorkoutConfig.Intervals(
            runSeconds  = binding.etRunSeconds.text.toString().toIntOrNull() ?: 60,
            walkSeconds = binding.etWalkSeconds.text.toString().toIntOrNull() ?: 30,
            reps        = binding.etReps.text.toString().toIntOrNull() ?: 5
        )
        R.id.radioTargetPace -> WorkoutConfig.TargetPace(
            paceSecPerUnit = (binding.etPaceMin.text.toString().toIntOrNull() ?: 6) * 60
                           + (binding.etPaceSec.text.toString().toIntOrNull() ?: 0)
        )
        else -> WorkoutConfig.FreeRun
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
