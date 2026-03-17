package com.fitnessultra.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fitnessultra.data.db.dao.RunDao
import com.fitnessultra.data.db.dao.WeightDao
import com.fitnessultra.data.db.entity.LocationPoint
import com.fitnessultra.data.db.entity.RunEntity
import com.fitnessultra.data.db.entity.WeightEntry

@Database(
    entities = [RunEntity::class, LocationPoint::class, WeightEntry::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun runDao(): RunDao
    abstract fun weightDao(): WeightDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fitness_ultra.db"
                ).build().also { INSTANCE = it }
            }
    }
}
