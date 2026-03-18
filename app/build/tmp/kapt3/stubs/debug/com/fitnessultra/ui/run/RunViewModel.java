package com.fitnessultra.ui.run;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u0007J\u000e\u0010\u001f\u001a\u00020\u001d2\u0006\u0010 \u001a\u00020!R\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\tR\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\tR\u001d\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u000f0\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\tR\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00150\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\tR\u0017\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00180\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\tR\u0017\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\t\u00a8\u0006\""}, d2 = {"Lcom/fitnessultra/ui/run/RunViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "(Landroid/app/Application;)V", "currentSpeedKmh", "Landroidx/lifecycle/MutableLiveData;", "", "getCurrentSpeedKmh", "()Landroidx/lifecycle/MutableLiveData;", "elevationGainMeters", "getElevationGainMeters", "isTracking", "", "pathPoints", "", "Lorg/osmdroid/util/GeoPoint;", "getPathPoints", "repository", "Lcom/fitnessultra/data/repository/RunRepository;", "stepCount", "", "getStepCount", "timeRunInMillis", "", "getTimeRunInMillis", "totalDistanceMeters", "getTotalDistanceMeters", "saveRun", "", "weightKg", "sendCommand", "action", "", "app_debug"})
public final class RunViewModel extends androidx.lifecycle.AndroidViewModel {
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<java.lang.Boolean> isTracking = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<java.util.List<org.osmdroid.util.GeoPoint>> pathPoints = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<java.lang.Long> timeRunInMillis = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<java.lang.Float> currentSpeedKmh = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<java.lang.Float> totalDistanceMeters = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<java.lang.Float> elevationGainMeters = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<java.lang.Integer> stepCount = null;
    @org.jetbrains.annotations.NotNull()
    private final com.fitnessultra.data.repository.RunRepository repository = null;
    
    public RunViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.lang.Boolean> isTracking() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.util.List<org.osmdroid.util.GeoPoint>> getPathPoints() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.lang.Long> getTimeRunInMillis() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.lang.Float> getCurrentSpeedKmh() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.lang.Float> getTotalDistanceMeters() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.lang.Float> getElevationGainMeters() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MutableLiveData<java.lang.Integer> getStepCount() {
        return null;
    }
    
    public final void sendCommand(@org.jetbrains.annotations.NotNull()
    java.lang.String action) {
    }
    
    /**
     * Call this when user presses Stop to persist the run.
     */
    public final void saveRun(float weightKg) {
    }
}