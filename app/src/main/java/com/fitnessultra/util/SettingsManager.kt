package com.fitnessultra.util

import android.content.Context
import androidx.preference.PreferenceManager

object SettingsManager {

    fun isVoiceEnabled(context: Context): Boolean =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean("pref_voice_enabled", true)

    fun voiceFrequencyKm(context: Context): Int =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getString("pref_voice_frequency", "1")?.toIntOrNull() ?: 1

    fun distanceUnit(context: Context): String =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getString("pref_distance_unit", "km") ?: "km"

    fun weightUnit(context: Context): String =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getString("pref_weight_unit", "kg") ?: "kg"

    fun theme(context: Context): String =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getString("pref_theme", "system") ?: "system"
}
