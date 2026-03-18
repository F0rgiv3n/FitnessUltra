package com.fitnessultra.data.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.fitnessultra.data.db.entity.LocationPoint;
import com.fitnessultra.data.db.entity.RunEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@SuppressWarnings({"unchecked", "deprecation"})
public final class RunDao_Impl implements RunDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RunEntity> __insertionAdapterOfRunEntity;

  private final EntityInsertionAdapter<LocationPoint> __insertionAdapterOfLocationPoint;

  private final EntityDeletionOrUpdateAdapter<RunEntity> __deletionAdapterOfRunEntity;

  public RunDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRunEntity = new EntityInsertionAdapter<RunEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `runs` (`id`,`dateTimestamp`,`avgSpeedKmh`,`distanceMeters`,`durationMillis`,`caloriesBurned`,`elevationGainMeters`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RunEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDateTimestamp());
        statement.bindDouble(3, entity.getAvgSpeedKmh());
        statement.bindDouble(4, entity.getDistanceMeters());
        statement.bindLong(5, entity.getDurationMillis());
        statement.bindLong(6, entity.getCaloriesBurned());
        statement.bindDouble(7, entity.getElevationGainMeters());
      }
    };
    this.__insertionAdapterOfLocationPoint = new EntityInsertionAdapter<LocationPoint>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `location_points` (`id`,`runId`,`latitude`,`longitude`,`altitude`,`speedMs`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final LocationPoint entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getRunId());
        statement.bindDouble(3, entity.getLatitude());
        statement.bindDouble(4, entity.getLongitude());
        statement.bindDouble(5, entity.getAltitude());
        statement.bindDouble(6, entity.getSpeedMs());
        statement.bindLong(7, entity.getTimestamp());
      }
    };
    this.__deletionAdapterOfRunEntity = new EntityDeletionOrUpdateAdapter<RunEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `runs` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RunEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
  }

  @Override
  public Object insertRun(final RunEntity run, final Continuation<? super Long> arg1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfRunEntity.insertAndReturnId(run);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, arg1);
  }

  @Override
  public Object insertLocationPoints(final List<LocationPoint> points,
      final Continuation<? super Unit> arg1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfLocationPoint.insert(points);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, arg1);
  }

  @Override
  public Object deleteRun(final RunEntity run, final Continuation<? super Unit> arg1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfRunEntity.handle(run);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, arg1);
  }

  @Override
  public Flow<List<RunEntity>> getAllRuns() {
    final String _sql = "SELECT * FROM runs ORDER BY dateTimestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"runs"}, new Callable<List<RunEntity>>() {
      @Override
      @NonNull
      public List<RunEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDateTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "dateTimestamp");
          final int _cursorIndexOfAvgSpeedKmh = CursorUtil.getColumnIndexOrThrow(_cursor, "avgSpeedKmh");
          final int _cursorIndexOfDistanceMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "distanceMeters");
          final int _cursorIndexOfDurationMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMillis");
          final int _cursorIndexOfCaloriesBurned = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesBurned");
          final int _cursorIndexOfElevationGainMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "elevationGainMeters");
          final List<RunEntity> _result = new ArrayList<RunEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RunEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDateTimestamp;
            _tmpDateTimestamp = _cursor.getLong(_cursorIndexOfDateTimestamp);
            final float _tmpAvgSpeedKmh;
            _tmpAvgSpeedKmh = _cursor.getFloat(_cursorIndexOfAvgSpeedKmh);
            final float _tmpDistanceMeters;
            _tmpDistanceMeters = _cursor.getFloat(_cursorIndexOfDistanceMeters);
            final long _tmpDurationMillis;
            _tmpDurationMillis = _cursor.getLong(_cursorIndexOfDurationMillis);
            final int _tmpCaloriesBurned;
            _tmpCaloriesBurned = _cursor.getInt(_cursorIndexOfCaloriesBurned);
            final float _tmpElevationGainMeters;
            _tmpElevationGainMeters = _cursor.getFloat(_cursorIndexOfElevationGainMeters);
            _item = new RunEntity(_tmpId,_tmpDateTimestamp,_tmpAvgSpeedKmh,_tmpDistanceMeters,_tmpDurationMillis,_tmpCaloriesBurned,_tmpElevationGainMeters);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getRunById(final long runId, final Continuation<? super RunEntity> arg1) {
    final String _sql = "SELECT * FROM runs WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, runId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<RunEntity>() {
      @Override
      @Nullable
      public RunEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDateTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "dateTimestamp");
          final int _cursorIndexOfAvgSpeedKmh = CursorUtil.getColumnIndexOrThrow(_cursor, "avgSpeedKmh");
          final int _cursorIndexOfDistanceMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "distanceMeters");
          final int _cursorIndexOfDurationMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "durationMillis");
          final int _cursorIndexOfCaloriesBurned = CursorUtil.getColumnIndexOrThrow(_cursor, "caloriesBurned");
          final int _cursorIndexOfElevationGainMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "elevationGainMeters");
          final RunEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDateTimestamp;
            _tmpDateTimestamp = _cursor.getLong(_cursorIndexOfDateTimestamp);
            final float _tmpAvgSpeedKmh;
            _tmpAvgSpeedKmh = _cursor.getFloat(_cursorIndexOfAvgSpeedKmh);
            final float _tmpDistanceMeters;
            _tmpDistanceMeters = _cursor.getFloat(_cursorIndexOfDistanceMeters);
            final long _tmpDurationMillis;
            _tmpDurationMillis = _cursor.getLong(_cursorIndexOfDurationMillis);
            final int _tmpCaloriesBurned;
            _tmpCaloriesBurned = _cursor.getInt(_cursorIndexOfCaloriesBurned);
            final float _tmpElevationGainMeters;
            _tmpElevationGainMeters = _cursor.getFloat(_cursorIndexOfElevationGainMeters);
            _result = new RunEntity(_tmpId,_tmpDateTimestamp,_tmpAvgSpeedKmh,_tmpDistanceMeters,_tmpDurationMillis,_tmpCaloriesBurned,_tmpElevationGainMeters);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, arg1);
  }

  @Override
  public Object getLocationPointsForRun(final long runId,
      final Continuation<? super List<LocationPoint>> arg1) {
    final String _sql = "SELECT * FROM location_points WHERE runId = ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, runId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<LocationPoint>>() {
      @Override
      @NonNull
      public List<LocationPoint> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRunId = CursorUtil.getColumnIndexOrThrow(_cursor, "runId");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAltitude = CursorUtil.getColumnIndexOrThrow(_cursor, "altitude");
          final int _cursorIndexOfSpeedMs = CursorUtil.getColumnIndexOrThrow(_cursor, "speedMs");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<LocationPoint> _result = new ArrayList<LocationPoint>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LocationPoint _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpRunId;
            _tmpRunId = _cursor.getLong(_cursorIndexOfRunId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final double _tmpAltitude;
            _tmpAltitude = _cursor.getDouble(_cursorIndexOfAltitude);
            final float _tmpSpeedMs;
            _tmpSpeedMs = _cursor.getFloat(_cursorIndexOfSpeedMs);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new LocationPoint(_tmpId,_tmpRunId,_tmpLatitude,_tmpLongitude,_tmpAltitude,_tmpSpeedMs,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, arg1);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
