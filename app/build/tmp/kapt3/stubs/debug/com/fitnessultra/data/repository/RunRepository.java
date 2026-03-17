package com.fitnessultra.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\u000eJ\u001c\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\u00072\u0006\u0010\u0011\u001a\u00020\u0012H\u0086@\u00a2\u0006\u0002\u0010\u0013J\u0018\u0010\u0014\u001a\u0004\u0018\u00010\b2\u0006\u0010\u0011\u001a\u00020\u0012H\u0086@\u00a2\u0006\u0002\u0010\u0013J\u001c\u0010\u0015\u001a\u00020\f2\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00100\u0007H\u0086@\u00a2\u0006\u0002\u0010\u0017J\u0016\u0010\u0018\u001a\u00020\u00122\u0006\u0010\r\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\u000eR\u001d\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2 = {"Lcom/fitnessultra/data/repository/RunRepository;", "", "runDao", "Lcom/fitnessultra/data/db/dao/RunDao;", "(Lcom/fitnessultra/data/db/dao/RunDao;)V", "allRuns", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/fitnessultra/data/db/entity/RunEntity;", "getAllRuns", "()Lkotlinx/coroutines/flow/Flow;", "deleteRun", "", "run", "(Lcom/fitnessultra/data/db/entity/RunEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getLocationPointsForRun", "Lcom/fitnessultra/data/db/entity/LocationPoint;", "runId", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getRunById", "insertLocationPoints", "points", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertRun", "app_debug"})
public final class RunRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.fitnessultra.data.db.dao.RunDao runDao = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.fitnessultra.data.db.entity.RunEntity>> allRuns = null;
    
    public RunRepository(@org.jetbrains.annotations.NotNull()
    com.fitnessultra.data.db.dao.RunDao runDao) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.fitnessultra.data.db.entity.RunEntity>> getAllRuns() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insertRun(@org.jetbrains.annotations.NotNull()
    com.fitnessultra.data.db.entity.RunEntity run, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteRun(@org.jetbrains.annotations.NotNull()
    com.fitnessultra.data.db.entity.RunEntity run, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getRunById(long runId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.fitnessultra.data.db.entity.RunEntity> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insertLocationPoints(@org.jetbrains.annotations.NotNull()
    java.util.List<com.fitnessultra.data.db.entity.LocationPoint> points, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getLocationPointsForRun(long runId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.fitnessultra.data.db.entity.LocationPoint>> $completion) {
        return null;
    }
}