package com.fitnessultra.util

import android.content.Context
import com.fitnessultra.R
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

    fun formatDistance(meters: Float, useMiles: Boolean, context: Context): String =
        if (useMiles) context.getString(R.string.format_distance_mi, meters / METERS_PER_MILE)
        else          context.getString(R.string.format_distance_km, meters / 1000f)

    fun formatSpeedKmh(kmh: Float, useMiles: Boolean, context: Context): String =
        if (useMiles) context.getString(R.string.format_speed_mph, kmh * KM_TO_MI)
        else          context.getString(R.string.format_speed_kmh, kmh)

    /** Returns pace as "MM:SS / km" or "MM:SS / mi", or "--:--" if no movement. */
    fun calculatePace(distanceMeters: Float, durationMillis: Long, useMiles: Boolean = false, context: Context): String {
        if (distanceMeters <= 0f || durationMillis <= 0L) return context.getString(R.string.pace_no_data)
        val unitMeters = if (useMiles) METERS_PER_MILE else 1000f
        val distanceUnits = distanceMeters / unitMeters
        val durationMinutes = durationMillis / 1000f / 60f
        val paceMinPerUnit = durationMinutes / distanceUnits
        val paceMin = paceMinPerUnit.toInt()
        val paceSec = ((paceMinPerUnit - paceMin) * 60).toInt()
        return context.getString(R.string.format_pace_with_unit, paceMin, paceSec, distanceUnitLabel(useMiles, context))
    }

    /** Returns current pace in seconds per unit (km or mi), or Int.MAX_VALUE if no movement. */
    fun calculatePaceSec(distanceMeters: Float, durationMillis: Long, useMiles: Boolean = false): Int {
        if (distanceMeters <= 0f || durationMillis <= 0L) return Int.MAX_VALUE
        val unitMeters = if (useMiles) METERS_PER_MILE else 1000f
        val distanceUnits = distanceMeters / unitMeters
        val durationMinutes = durationMillis / 1000f / 60f
        return ((durationMinutes / distanceUnits) * 60f).toInt()
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

    fun distanceUnitLabel(useMiles: Boolean, context: Context): String =
        if (useMiles) context.getString(R.string.unit_mi) else context.getString(R.string.unit_km)

    fun speedUnitLabel(useMiles: Boolean, context: Context): String =
        if (useMiles) context.getString(R.string.unit_mph) else context.getString(R.string.unit_kmh)
}
