package com.fitnessultra.ui.charts;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001c\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\n\u001a\u00020\u000bH\u0086@\u00a2\u0006\u0002\u0010\fJ\u0018\u0010\r\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\n\u001a\u00020\u000bH\u0086@\u00a2\u0006\u0002\u0010\fR\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/fitnessultra/ui/charts/ChartsViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "(Landroid/app/Application;)V", "repository", "Lcom/fitnessultra/data/repository/RunRepository;", "getLocationPoints", "", "Lcom/fitnessultra/data/db/entity/LocationPoint;", "runId", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getRunById", "Lcom/fitnessultra/data/db/entity/RunEntity;", "app_debug"})
public final class ChartsViewModel extends androidx.lifecycle.AndroidViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.fitnessultra.data.repository.RunRepository repository = null;
    
    public ChartsViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getRunById(long runId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.fitnessultra.data.db.entity.RunEntity> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getLocationPoints(long runId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.fitnessultra.data.db.entity.LocationPoint>> $completion) {
        return null;
    }
}