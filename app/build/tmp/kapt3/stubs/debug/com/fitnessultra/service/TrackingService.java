package com.fitnessultra.service;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000j\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\u0018\u0000 *2\u00020\u0001:\u0001*B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0017H\u0002J\b\u0010\u0018\u001a\u00020\u0015H\u0002J\b\u0010\u0019\u001a\u00020\u0015H\u0016J\b\u0010\u001a\u001a\u00020\u0015H\u0016J\"\u0010\u001b\u001a\u00020\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u001e2\u0006\u0010\u001f\u001a\u00020\u001c2\u0006\u0010 \u001a\u00020\u001cH\u0016J\b\u0010!\u001a\u00020\u0015H\u0002J\b\u0010\"\u001a\u00020\u0015H\u0002J\u0010\u0010#\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0017H\u0002J\u0010\u0010$\u001a\u00020\u00152\u0006\u0010%\u001a\u00020&H\u0003J\u0010\u0010\'\u001a\u00020\u00152\u0006\u0010(\u001a\u00020)H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006+"}, d2 = {"Lcom/fitnessultra/service/TrackingService;", "Landroidx/lifecycle/LifecycleService;", "()V", "fusedLocationClient", "Lcom/google/android/gms/location/FusedLocationProviderClient;", "lastAltitude", "", "locationCallback", "Lcom/google/android/gms/location/LocationCallback;", "notificationManager", "Landroid/app/NotificationManager;", "serviceJob", "Lkotlinx/coroutines/CompletableJob;", "serviceScope", "Lkotlinx/coroutines/CoroutineScope;", "timeRun", "", "timeStarted", "timerJob", "Lkotlinx/coroutines/Job;", "addPathPoint", "", "location", "Landroid/location/Location;", "initValues", "onCreate", "onDestroy", "onStartCommand", "", "intent", "Landroid/content/Intent;", "flags", "startId", "startForegroundService", "startTimer", "trackElevation", "updateLocationTracking", "isTracking", "", "updateNotification", "time", "", "Companion", "app_debug"})
public final class TrackingService extends androidx.lifecycle.LifecycleService {
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_START_OR_RESUME = "ACTION_START_OR_RESUME";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_PAUSE = "ACTION_PAUSE";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_STOP = "ACTION_STOP";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String NOTIFICATION_CHANNEL_ID = "tracking_channel";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String NOTIFICATION_CHANNEL_NAME = "Tracking";
    private static final int NOTIFICATION_ID = 1;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.lifecycle.MutableLiveData<java.lang.Boolean> isTracking = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.lifecycle.MutableLiveData<java.util.List<org.osmdroid.util.GeoPoint>> pathPoints = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.lifecycle.MutableLiveData<java.lang.Long> timeRunInMillis = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.lifecycle.MutableLiveData<java.lang.Float> currentSpeedKmh = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.lifecycle.MutableLiveData<java.lang.Float> totalDistanceMeters = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.lifecycle.MutableLiveData<java.lang.Float> elevationGainMeters = null;
    private com.google.android.gms.location.FusedLocationProviderClient fusedLocationClient;
    private com.google.android.gms.location.LocationCallback locationCallback;
    private android.app.NotificationManager notificationManager;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CompletableJob serviceJob = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope serviceScope = null;
    private long timeStarted = 0L;
    private long timeRun = 0L;
    private double lastAltitude = 4.9E-324;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job timerJob;
    @org.jetbrains.annotations.NotNull()
    public static final com.fitnessultra.service.TrackingService.Companion Companion = null;
    
    public TrackingService() {
        super();
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
    
    private final void initValues() {
    }
    
    @java.lang.Override()
    public int onStartCommand(@org.jetbrains.annotations.Nullable()
    android.content.Intent intent, int flags, int startId) {
        return 0;
    }
    
    private final void startForegroundService() {
    }
    
    private final void updateNotification(java.lang.String time) {
    }
    
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    private final void updateLocationTracking(boolean isTracking) {
    }
    
    private final void addPathPoint(android.location.Location location) {
    }
    
    private final void trackElevation(android.location.Location location) {
    }
    
    private final void startTimer() {
    }
    
    @java.lang.Override()
    public void onDestroy() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082T\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\r0\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000fR\u0017\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u000fR\u001d\u0010\u0014\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00160\u00150\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u000fR\u0017\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00190\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u000fR\u0017\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\r0\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u000f\u00a8\u0006\u001d"}, d2 = {"Lcom/fitnessultra/service/TrackingService$Companion;", "", "()V", "ACTION_PAUSE", "", "ACTION_START_OR_RESUME", "ACTION_STOP", "NOTIFICATION_CHANNEL_ID", "NOTIFICATION_CHANNEL_NAME", "NOTIFICATION_ID", "", "currentSpeedKmh", "Landroidx/lifecycle/MutableLiveData;", "", "getCurrentSpeedKmh", "()Landroidx/lifecycle/MutableLiveData;", "elevationGainMeters", "getElevationGainMeters", "isTracking", "", "pathPoints", "", "Lorg/osmdroid/util/GeoPoint;", "getPathPoints", "timeRunInMillis", "", "getTimeRunInMillis", "totalDistanceMeters", "getTotalDistanceMeters", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
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
    }
}