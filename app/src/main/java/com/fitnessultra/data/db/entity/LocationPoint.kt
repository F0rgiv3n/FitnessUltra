package com.fitnessultra.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "location_points",
    foreignKeys = [ForeignKey(
        entity = RunEntity::class,
        parentColumns = ["id"],
        childColumns = ["runId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("runId")]
)
data class LocationPoint(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val runId: Long,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val speedMs: Float,
    val timestamp: Long
)
