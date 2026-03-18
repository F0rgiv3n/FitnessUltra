package com.fitnessultra.ui.run

sealed class WorkoutConfig {
    object FreeRun : WorkoutConfig()

    data class Intervals(
        val runSeconds: Int,
        val walkSeconds: Int,
        val reps: Int
    ) : WorkoutConfig()

    data class TargetPace(
        val paceSecPerUnit: Int,       // seconds per km or per mi, matching current unit setting
        val toleranceSec: Int = 30     // alert when deviating by this many seconds
    ) : WorkoutConfig()
}
