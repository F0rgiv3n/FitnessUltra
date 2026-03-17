package com.fitnessultra.data.db.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\t0\bH\'J\u0016\u0010\n\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006\u00a8\u0006\u000b"}, d2 = {"Lcom/fitnessultra/data/db/dao/WeightDao;", "", "deleteWeightEntry", "", "entry", "Lcom/fitnessultra/data/db/entity/WeightEntry;", "(Lcom/fitnessultra/data/db/entity/WeightEntry;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllWeightEntries", "Lkotlinx/coroutines/flow/Flow;", "", "insertWeightEntry", "app_debug"})
@androidx.room.Dao()
public abstract interface WeightDao {
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertWeightEntry(@org.jetbrains.annotations.NotNull()
    com.fitnessultra.data.db.entity.WeightEntry entry, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteWeightEntry(@org.jetbrains.annotations.NotNull()
    com.fitnessultra.data.db.entity.WeightEntry entry, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM weight_entries ORDER BY dateTimestamp ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.fitnessultra.data.db.entity.WeightEntry>> getAllWeightEntries();
}