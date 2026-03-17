package com.fitnessultra.data.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.fitnessultra.data.db.dao.RunDao;
import com.fitnessultra.data.db.dao.RunDao_Impl;
import com.fitnessultra.data.db.dao.WeightDao;
import com.fitnessultra.data.db.dao.WeightDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile RunDao _runDao;

  private volatile WeightDao _weightDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `runs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `dateTimestamp` INTEGER NOT NULL, `avgSpeedKmh` REAL NOT NULL, `distanceMeters` REAL NOT NULL, `durationMillis` INTEGER NOT NULL, `caloriesBurned` INTEGER NOT NULL, `elevationGainMeters` REAL NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `location_points` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `runId` INTEGER NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `altitude` REAL NOT NULL, `speedMs` REAL NOT NULL, `timestamp` INTEGER NOT NULL, FOREIGN KEY(`runId`) REFERENCES `runs`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_location_points_runId` ON `location_points` (`runId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `weight_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `weightKg` REAL NOT NULL, `dateTimestamp` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'ee6919113ed58c79a6463d58ab7fc47a')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `runs`");
        db.execSQL("DROP TABLE IF EXISTS `location_points`");
        db.execSQL("DROP TABLE IF EXISTS `weight_entries`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsRuns = new HashMap<String, TableInfo.Column>(7);
        _columnsRuns.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRuns.put("dateTimestamp", new TableInfo.Column("dateTimestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRuns.put("avgSpeedKmh", new TableInfo.Column("avgSpeedKmh", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRuns.put("distanceMeters", new TableInfo.Column("distanceMeters", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRuns.put("durationMillis", new TableInfo.Column("durationMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRuns.put("caloriesBurned", new TableInfo.Column("caloriesBurned", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRuns.put("elevationGainMeters", new TableInfo.Column("elevationGainMeters", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRuns = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRuns = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRuns = new TableInfo("runs", _columnsRuns, _foreignKeysRuns, _indicesRuns);
        final TableInfo _existingRuns = TableInfo.read(db, "runs");
        if (!_infoRuns.equals(_existingRuns)) {
          return new RoomOpenHelper.ValidationResult(false, "runs(com.fitnessultra.data.db.entity.RunEntity).\n"
                  + " Expected:\n" + _infoRuns + "\n"
                  + " Found:\n" + _existingRuns);
        }
        final HashMap<String, TableInfo.Column> _columnsLocationPoints = new HashMap<String, TableInfo.Column>(7);
        _columnsLocationPoints.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationPoints.put("runId", new TableInfo.Column("runId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationPoints.put("latitude", new TableInfo.Column("latitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationPoints.put("longitude", new TableInfo.Column("longitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationPoints.put("altitude", new TableInfo.Column("altitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationPoints.put("speedMs", new TableInfo.Column("speedMs", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationPoints.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLocationPoints = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysLocationPoints.add(new TableInfo.ForeignKey("runs", "CASCADE", "NO ACTION", Arrays.asList("runId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesLocationPoints = new HashSet<TableInfo.Index>(1);
        _indicesLocationPoints.add(new TableInfo.Index("index_location_points_runId", false, Arrays.asList("runId"), Arrays.asList("ASC")));
        final TableInfo _infoLocationPoints = new TableInfo("location_points", _columnsLocationPoints, _foreignKeysLocationPoints, _indicesLocationPoints);
        final TableInfo _existingLocationPoints = TableInfo.read(db, "location_points");
        if (!_infoLocationPoints.equals(_existingLocationPoints)) {
          return new RoomOpenHelper.ValidationResult(false, "location_points(com.fitnessultra.data.db.entity.LocationPoint).\n"
                  + " Expected:\n" + _infoLocationPoints + "\n"
                  + " Found:\n" + _existingLocationPoints);
        }
        final HashMap<String, TableInfo.Column> _columnsWeightEntries = new HashMap<String, TableInfo.Column>(3);
        _columnsWeightEntries.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeightEntries.put("weightKg", new TableInfo.Column("weightKg", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsWeightEntries.put("dateTimestamp", new TableInfo.Column("dateTimestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysWeightEntries = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesWeightEntries = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoWeightEntries = new TableInfo("weight_entries", _columnsWeightEntries, _foreignKeysWeightEntries, _indicesWeightEntries);
        final TableInfo _existingWeightEntries = TableInfo.read(db, "weight_entries");
        if (!_infoWeightEntries.equals(_existingWeightEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "weight_entries(com.fitnessultra.data.db.entity.WeightEntry).\n"
                  + " Expected:\n" + _infoWeightEntries + "\n"
                  + " Found:\n" + _existingWeightEntries);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "ee6919113ed58c79a6463d58ab7fc47a", "9f37c1a17ab5f81c0416f6fcfc7a81d5");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "runs","location_points","weight_entries");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `runs`");
      _db.execSQL("DELETE FROM `location_points`");
      _db.execSQL("DELETE FROM `weight_entries`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(RunDao.class, RunDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(WeightDao.class, WeightDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public RunDao runDao() {
    if (_runDao != null) {
      return _runDao;
    } else {
      synchronized(this) {
        if(_runDao == null) {
          _runDao = new RunDao_Impl(this);
        }
        return _runDao;
      }
    }
  }

  @Override
  public WeightDao weightDao() {
    if (_weightDao != null) {
      return _weightDao;
    } else {
      synchronized(this) {
        if(_weightDao == null) {
          _weightDao = new WeightDao_Impl(this);
        }
        return _weightDao;
      }
    }
  }
}
