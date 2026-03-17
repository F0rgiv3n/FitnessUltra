package com.fitnessultra.util

import java.util.concurrent.TimeUnit

object TrackingUtils {

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

    fun formatDistance(meters: Float): String {
        val km = meters / 1000f
        return "%.2f km".format(km)
    }

    /** Returns pace as "MM:SS / km", or "--:--" if no movement yet. */
    fun calculatePace(distanceMeters: Float, durationMillis: Long): String {
        if (distanceMeters <= 0f || durationMillis <= 0L) return "--:--"
        val distanceKm = distanceMeters / 1000f
        val durationMinutes = durationMillis / 1000f / 60f
        val paceMinPerKm = durationMinutes / distanceKm
        val paceMin = paceMinPerKm.toInt()
        val paceSec = ((paceMinPerKm - paceMin) * 60).toInt()
        return "%d:%02d / km".format(paceMin, paceSec)
    }

    /** Calories = Distance(km) × Weight(kg) × 1.036 */
    fun calculateCalories(distanceMeters: Float, weightKg: Float): Int {
        val distanceKm = distanceMeters / 1000f
        return (distanceKm * weightKg * 1.036f).toInt()
    }

    fun formatSpeedKmh(kmh: Float): String = "%.1f km/h".format(kmh)
}
