package com.fitnessultra.ui.weight;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0002J,\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00110\u00172\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00190\u0017H\u0002J$\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001f2\b\u0010 \u001a\u0004\u0018\u00010!H\u0016J\b\u0010\"\u001a\u00020\u0013H\u0016J\u001a\u0010#\u001a\u00020\u00132\u0006\u0010$\u001a\u00020\u001b2\b\u0010 \u001a\u0004\u0018\u00010!H\u0016J\u0010\u0010%\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0002J\u0016\u0010&\u001a\u00020\u00132\f\u0010\'\u001a\b\u0012\u0004\u0012\u00020(0\u0017H\u0002R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\u00020\u00048BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007R\u001b\u0010\b\u001a\u00020\t8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\f\u0010\r\u001a\u0004\b\n\u0010\u000b\u00a8\u0006)"}, d2 = {"Lcom/fitnessultra/ui/weight/WeightFragment;", "Landroidx/fragment/app/Fragment;", "()V", "_binding", "Lcom/fitnessultra/databinding/FragmentWeightBinding;", "binding", "getBinding", "()Lcom/fitnessultra/databinding/FragmentWeightBinding;", "viewModel", "Lcom/fitnessultra/ui/weight/WeightViewModel;", "getViewModel", "()Lcom/fitnessultra/ui/weight/WeightViewModel;", "viewModel$delegate", "Lkotlin/Lazy;", "bmiColor", "", "bmi", "", "buildColoredChart", "", "chart", "Lcom/github/mikephil/charting/charts/LineChart;", "values", "", "labels", "", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onDestroyView", "onViewCreated", "view", "setupChart", "updateAll", "entries", "Lcom/fitnessultra/data/db/entity/WeightEntry;", "app_debug"})
public final class WeightFragment extends androidx.fragment.app.Fragment {
    @org.jetbrains.annotations.Nullable()
    private com.fitnessultra.databinding.FragmentWeightBinding _binding;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy viewModel$delegate = null;
    
    public WeightFragment() {
        super();
    }
    
    private final com.fitnessultra.databinding.FragmentWeightBinding getBinding() {
        return null;
    }
    
    private final com.fitnessultra.ui.weight.WeightViewModel getViewModel() {
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
    
    private final void updateAll(java.util.List<com.fitnessultra.data.db.entity.WeightEntry> entries) {
    }
    
    /**
     * Builds a line chart where each segment is green if value decreased, red if increased.
     */
    private final void buildColoredChart(com.github.mikephil.charting.charts.LineChart chart, java.util.List<java.lang.Float> values, java.util.List<java.lang.String> labels) {
    }
    
    private final void setupChart(com.github.mikephil.charting.charts.LineChart chart) {
    }
    
    private final int bmiColor(float bmi) {
        return 0;
    }
    
    @java.lang.Override()
    public void onDestroyView() {
    }
}