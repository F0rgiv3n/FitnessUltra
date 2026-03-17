package com.fitnessultra.ui.run;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000t\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u001a\u001a\u00020\u001bH\u0002J\b\u0010\u001c\u001a\u00020\u001dH\u0002J\b\u0010\u001e\u001a\u00020\u001bH\u0002J$\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\"2\b\u0010#\u001a\u0004\u0018\u00010$2\b\u0010%\u001a\u0004\u0018\u00010&H\u0016J\b\u0010\'\u001a\u00020\u001bH\u0016J\b\u0010(\u001a\u00020\u001bH\u0016J\b\u0010)\u001a\u00020\u001bH\u0016J\u001a\u0010*\u001a\u00020\u001b2\u0006\u0010+\u001a\u00020 2\b\u0010%\u001a\u0004\u0018\u00010&H\u0016J\b\u0010,\u001a\u00020\u001bH\u0002J\u0016\u0010-\u001a\u00020\u001b2\f\u0010.\u001a\b\u0012\u0004\u0012\u0002000/H\u0002R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\u00020\u00048BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010\f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u000e0\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082.\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0014\u001a\u00020\u00158BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0018\u0010\u0019\u001a\u0004\b\u0016\u0010\u0017\u00a8\u00061"}, d2 = {"Lcom/fitnessultra/ui/run/RunFragment;", "Landroidx/fragment/app/Fragment;", "()V", "_binding", "Lcom/fitnessultra/databinding/FragmentRunBinding;", "binding", "getBinding", "()Lcom/fitnessultra/databinding/FragmentRunBinding;", "isTracking", "", "lastVoiceKm", "", "locationPermissionLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "", "", "routePolyline", "Lorg/osmdroid/views/overlay/Polyline;", "tts", "Landroid/speech/tts/TextToSpeech;", "viewModel", "Lcom/fitnessultra/ui/run/RunViewModel;", "getViewModel", "()Lcom/fitnessultra/ui/run/RunViewModel;", "viewModel$delegate", "Lkotlin/Lazy;", "checkVoiceMilestone", "", "getUserWeight", "", "observeTracking", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onDestroyView", "onPause", "onResume", "onViewCreated", "view", "requestPermissionsAndStart", "updatePolyline", "points", "", "Lorg/osmdroid/util/GeoPoint;", "app_debug"})
public final class RunFragment extends androidx.fragment.app.Fragment {
    @org.jetbrains.annotations.Nullable()
    private com.fitnessultra.databinding.FragmentRunBinding _binding;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy viewModel$delegate = null;
    private boolean isTracking = false;
    @org.jetbrains.annotations.Nullable()
    private org.osmdroid.views.overlay.Polyline routePolyline;
    private android.speech.tts.TextToSpeech tts;
    private int lastVoiceKm = 0;
    @org.jetbrains.annotations.NotNull()
    private final androidx.activity.result.ActivityResultLauncher<java.lang.String[]> locationPermissionLauncher = null;
    
    public RunFragment() {
        super();
    }
    
    private final com.fitnessultra.databinding.FragmentRunBinding getBinding() {
        return null;
    }
    
    private final com.fitnessultra.ui.run.RunViewModel getViewModel() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public android.view.View onCreateView(@org.jetbrains.annotations.NotNull()
    android.view.LayoutInflater inflater, @org.jetbrains.annotations.Nullable()
    android.view.ViewGroup container, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
        return null;
    }
    
    @java.lang.Override()
    public void onViewCreated(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void observeTracking() {
    }
    
    private final void updatePolyline(java.util.List<org.osmdroid.util.GeoPoint> points) {
    }
    
    private final void checkVoiceMilestone() {
    }
    
    private final void requestPermissionsAndStart() {
    }
    
    private final float getUserWeight() {
        return 0.0F;
    }
    
    @java.lang.Override()
    public void onResume() {
    }
    
    @java.lang.Override()
    public void onPause() {
    }
    
    @java.lang.Override()
    public void onDestroyView() {
    }
}