package com.fitnessultra.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\u000eJ\u0016\u0010\u000f\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\u000eJ\u0016\u0010\u0010\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\u000eR\u001d\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lcom/fitnessultra/data/repository/WeightRepository;", "", "weightDao", "Lcom/fitnessultra/data/db/dao/WeightDao;", "(Lcom/fitnessultra/data/db/dao/WeightDao;)V", "allWeightEntries", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/fitnessultra/data/db/entity/WeightEntry;", "getAllWeightEntries", "()Lkotlinx/coroutines/flow/Flow;", "deleteWeightEntry", "", "entry", "(Lcom/fitnessultra/data/db/entity/WeightEntry;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertWeightEntry", "updateWeightEntry", "app_debug"})
public final class WeightRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.fitnessultra.data.db.dao.WeightDao weightDao = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.fitnessultra.data.db.entity.WeightEntry>> allWeightEntries = null;
    
    public WeightRepository(@org.jetbrains.annotations.NotNull()
    com.fitnessultra.data.db.dao.WeightDao weightDao) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.fitnessultra.data.db.entity.WeightEntry>> getAllWeightEntries() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insertWeightEntry(@org.jetbrains.annotations.NotNull()
    com.fitnessultra.data.db.entity.WeightEntry entry, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteWeightEntry(@org.jetbrains.annotations.NotNull()
    com.fitnessultra.data.db.entity.WeightEntry entry, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateWeightEntry(@org.jetbrains.annotations.NotNull()
    com.fitnessultra.data.db.entity.WeightEntry entry, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}