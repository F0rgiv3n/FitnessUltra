package com.fitnessultra.util;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u0006J\u0016\u0010\b\u001a\u00020\t2\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\u000bJ\u000e\u0010\f\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\u0006J\u000e\u0010\u000e\u001a\u00020\t2\u0006\u0010\u000f\u001a\u00020\u0006J\u0018\u0010\u0010\u001a\u00020\t2\u0006\u0010\u0011\u001a\u00020\u000b2\b\b\u0002\u0010\u0012\u001a\u00020\u0013\u00a8\u0006\u0014"}, d2 = {"Lcom/fitnessultra/util/TrackingUtils;", "", "()V", "calculateCalories", "", "distanceMeters", "", "weightKg", "calculatePace", "", "durationMillis", "", "formatDistance", "meters", "formatSpeedKmh", "kmh", "formatTime", "ms", "includeMillis", "", "app_debug"})
public final class TrackingUtils {
    @org.jetbrains.annotations.NotNull()
    public static final com.fitnessultra.util.TrackingUtils INSTANCE = null;
    
    private TrackingUtils() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String formatTime(long ms, boolean includeMillis) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String formatDistance(float meters) {
        return null;
    }
    
    /**
     * Returns pace as "MM:SS / km", or "--:--" if no movement yet.
     */
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String calculatePace(float distanceMeters, long durationMillis) {
        return null;
    }
    
    /**
     * Calories = Distance(km) × Weight(kg) × 1.036
     */
    public final int calculateCalories(float distanceMeters, float weightKg) {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String formatSpeedKmh(float kmh) {
        return null;
    }
}