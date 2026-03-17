package com.fitnessultra.ui.weight;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\nJ\u000e\u0010\u0010\u001a\u00020\u000e2\u0006\u0010\u0011\u001a\u00020\u0012R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\u0013"}, d2 = {"Lcom/fitnessultra/ui/weight/WeightViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "(Landroid/app/Application;)V", "repository", "Lcom/fitnessultra/data/repository/WeightRepository;", "weightEntries", "Landroidx/lifecycle/LiveData;", "", "Lcom/fitnessultra/data/db/entity/WeightEntry;", "getWeightEntries", "()Landroidx/lifecycle/LiveData;", "deleteEntry", "", "entry", "saveWeight", "weightKg", "", "app_debug"})
public final class WeightViewModel extends androidx.lifecycle.AndroidViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.fitnessultra.data.repository.WeightRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.fitnessultra.data.db.entity.WeightEntry>> weightEntries = null;
    
    public WeightViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.fitnessultra.data.db.entity.WeightEntry>> getWeightEntries() {
        return null;
    }
    
    public final void saveWeight(float weightKg) {
    }
    
    public final void deleteEntry(@org.jetbrains.annotations.NotNull()
    com.fitnessultra.data.db.entity.WeightEntry entry) {
    }
}