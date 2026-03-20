package com.fitnessultra.util

import android.content.Context
import androidx.preference.PreferenceManager
import org.osmdroid.tileprovider.tilesource.ITileSource
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.XYTileSource

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

    fun useMiles(context: Context): Boolean = distanceUnit(context) == "mi"

    fun weightUnit(context: Context): String =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getString("pref_weight_unit", "kg") ?: "kg"

    fun theme(context: Context): String =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getString("pref_theme", "system") ?: "system"

    fun useLbs(context: Context): Boolean = weightUnit(context) == "lbs"

    fun gpsAccuracy(context: Context): String =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getString("pref_gps_accuracy", "high") ?: "high"

    fun isAutoPauseEnabled(context: Context): Boolean =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean("pref_auto_pause", false)

    fun isAutoResumeEnabled(context: Context): Boolean =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean("pref_auto_resume", false)

    fun isKeepScreenOn(context: Context): Boolean =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean("pref_screen_on", true)

    fun mapStyle(context: Context): String =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getString("pref_map_style", "standard") ?: "standard"

    fun tileSource(context: Context): ITileSource = when (mapStyle(context)) {
        "cycle" -> XYTileSource(
            "CyclOSM", 0, 20, 256, ".png",
            arrayOf(
                "https://a.tile-cyclosm.openstreetmap.fr/cyclosm/",
                "https://b.tile-cyclosm.openstreetmap.fr/cyclosm/",
                "https://c.tile-cyclosm.openstreetmap.fr/cyclosm/"
            ), "© CyclOSM, OpenStreetMap contributors"
        )
        "transport" -> XYTileSource(
            "HumanitarianOSM", 0, 20, 256, ".png",
            arrayOf(
                "https://a.tile.openstreetmap.fr/hot/",
                "https://b.tile.openstreetmap.fr/hot/"
            ), "© OpenStreetMap contributors, HOT"
        )
        else -> TileSourceFactory.MAPNIK
    }

    fun isMapFollow(context: Context): Boolean =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean("pref_map_follow", true)

    fun isCountdownEnabled(context: Context): Boolean =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean("pref_countdown", true)

    fun voiceLanguage(context: Context): String =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getString("pref_voice_language", "default") ?: "default"

    fun gender(context: Context): String =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getString("pref_gender", "none") ?: "none"
}
