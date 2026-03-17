package com.fitnessultra.data.db.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0007\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\t0\bH\'J\u001c\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\t2\u0006\u0010\f\u001a\u00020\rH\u00a7@\u00a2\u0006\u0002\u0010\u000eJ\u0018\u0010\u000f\u001a\u0004\u0018\u00010\u00052\u0006\u0010\f\u001a\u00020\rH\u00a7@\u00a2\u0006\u0002\u0010\u000eJ\u001c\u0010\u0010\u001a\u00020\u00032\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u000b0\tH\u00a7@\u00a2\u0006\u0002\u0010\u0012J\u0016\u0010\u0013\u001a\u00020\r2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006\u00a8\u0006\u0014"}, d2 = {"Lcom/fitnessultra/data/db/dao/RunDao;", "", "deleteRun", "", "run", "Lcom/fitnessultra/data/db/entity/RunEntity;", "(Lcom/fitnessultra/data/db/entity/RunEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllRuns", "Lkotlinx/coroutines/flow/Flow;", "", "getLocationPointsForRun", "Lcom/fitnessultra/data/db/entity/LocationPoint;", "runId", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getRunById", "insertLocationPoints", "points", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertRun", "app_debug"})
@androidx.room.Dao()
public abstract interface RunDao {
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertRun(@org.jetbrains.annotations.NotNull()
    com.fitnessultra.data.db.entity.RunEntity run, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteRun(@org.jetbrains.annotations.NotNull()
    com.fitnessultra.data.db.entity.RunEntity run, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM runs ORDER BY dateTimestamp DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.fitnessultra.data.db.entity.RunEntity>> getAllRuns();
    
    @androidx.room.Query(value = "SELECT * FROM runs WHERE id = :runId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getRunById(long runId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.fitnessultra.data.db.entity.RunEntity> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertLocationPoints(@org.jetbrains.annotations.NotNull()
    java.util.List<com.fitnessultra.data.db.entity.LocationPoint> points, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM location_points WHERE runId = :runId ORDER BY timestamp ASC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getLocationPointsForRun(long runId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.fitnessultra.data.db.entity.LocationPoint>> $completion);
}