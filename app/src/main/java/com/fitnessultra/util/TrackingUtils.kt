package com.fitnessultra.util

import java.util.concurrent.TimeUnit

object TrackingUtils {

    private const val KM_TO_MI = 0.621371f
    private const val MI_TO_KM = 1.60934f
    private const val METERS_PER_MILE = 1609.344f

    fun formatTime(ms: Long, includeMillis: Boolean = false): String {
        var millis = ms
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        millis -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        millis -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis)
        millis -= TimeUnit.SECONDS.toMillis(seconds)

        return if (!includeMillis) {
            "%02d:%02d:%02d".format(hours, minutes, seconds)
        } else {
            "%02d:%02d:%02d:%02d".format(hours, minutes, seconds, millis / 10)
        }
    }

    fun formatDistance(meters: Float, useMiles: Boolean = false): String {
        return if (useMiles) {
            "%.2f mi".format(meters / METERS_PER_MILE)
        } else {
            "%.2f km".format(meters / 1000f)
        }
    }

    fun formatSpeedKmh(kmh: Float, useMiles: Boolean = false): String {
        return if (useMiles) {
            "%.1f mph".format(kmh * KM_TO_MI)
        } else {
            "%.1f km/h".format(kmh)
        }
    }

    /** Returns pace as "MM:SS / km" or "MM:SS / mi", or "--:--" if no movement. */
    fun calculatePace(distanceMeters: Float, durationMillis: Long, useMiles: Boolean = false): String {
        if (distanceMeters <= 0f || durationMillis <= 0L) return "--:--"
        val unitMeters = if (useMiles) METERS_PER_MILE else 1000f
        val distanceUnits = distanceMeters / unitMeters
        val durationMinutes = durationMillis / 1000f / 60f
        val paceMinPerUnit = durationMinutes / distanceUnits
        val paceMin = paceMinPerUnit.toInt()
        val paceSec = ((paceMinPerUnit - paceMin) * 60).toInt()
        val label = if (useMiles) "mi" else "km"
        return "%d:%02d / $label".format(paceMin, paceSec)
    }

    /** Calories with gender-specific MET factor. Male≈1.036, Female≈0.945, unknown≈1.036 */
    fun calculateCalories(distanceMeters: Float, weightKg: Float, gender: String = "none"): Int {
        val factor = if (gender == "female") 0.945f else 1.036f
        return (distanceMeters / 1000f * weightKg * factor).toInt()
    }

    /** Convert a user-entered distance value to km for internal storage. */
    fun toKm(value: Float, useMiles: Boolean) = if (useMiles) value * MI_TO_KM else value

    /** Convert a stored-km value to the display unit. */
    fun fromKm(km: Float, useMiles: Boolean) = if (useMiles) km * KM_TO_MI else km

    fun distanceUnitLabel(useMiles: Boolean) = if (useMiles) "mi" else "km"
    fun speedUnitLabel(useMiles: Boolean)    = if (useMiles) "mph" else "km/h"
}
