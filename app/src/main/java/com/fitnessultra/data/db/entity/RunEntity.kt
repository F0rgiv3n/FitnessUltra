package com.fitnessultra.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "runs")
data class RunEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateTimestamp: Long,
    val avgSpeedKmh: Float,
    val distanceMeters: Float,
    val durationMillis: Long,
    val caloriesBurned: Int,
    val elevationGainMeters: Float = 0f,
    val stepCount: Int = 0
)
