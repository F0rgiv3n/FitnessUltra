package com.fitnessultra.data.db.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.fitnessultra.data.db.entity.WeightEntry;
import java.lang.Class;
import java.lang.Exception;
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
public final class WeightDao_Impl implements WeightDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WeightEntry> __insertionAdapterOfWeightEntry;

  private final EntityDeletionOrUpdateAdapter<WeightEntry> __deletionAdapterOfWeightEntry;

  public WeightDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWeightEntry = new EntityInsertionAdapter<WeightEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `weight_entries` (`id`,`weightKg`,`dateTimestamp`) VALUES (nullif(?, 0),?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WeightEntry entity) {
        statement.bindLong(1, entity.getId());
        statement.bindDouble(2, entity.getWeightKg());
        statement.bindLong(3, entity.getDateTimestamp());
      }
    };
    this.__deletionAdapterOfWeightEntry = new EntityDeletionOrUpdateAdapter<WeightEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `weight_entries` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WeightEntry entity) {
        statement.bindLong(1, entity.getId());
      }
    };
  }

  @Override
  public Object insertWeightEntry(final WeightEntry entry,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfWeightEntry.insert(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteWeightEntry(final WeightEntry entry,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfWeightEntry.handle(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<WeightEntry>> getAllWeightEntries() {
    final String _sql = "SELECT * FROM weight_entries ORDER BY dateTimestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"weight_entries"}, new Callable<List<WeightEntry>>() {
      @Override
      @NonNull
      public List<WeightEntry> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWeightKg = CursorUtil.getColumnIndexOrThrow(_cursor, "weightKg");
          final int _cursorIndexOfDateTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "dateTimestamp");
          final List<WeightEntry> _result = new ArrayList<WeightEntry>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WeightEntry _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final float _tmpWeightKg;
            _tmpWeightKg = _cursor.getFloat(_cursorIndexOfWeightKg);
            final long _tmpDateTimestamp;
            _tmpDateTimestamp = _cursor.getLong(_cursorIndexOfDateTimestamp);
            _item = new WeightEntry(_tmpId,_tmpWeightKg,_tmpDateTimestamp);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
