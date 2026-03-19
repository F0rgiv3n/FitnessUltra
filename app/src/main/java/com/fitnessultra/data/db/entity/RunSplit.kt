package com.fitnessultra.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "run_splits",
    foreignKeys = [ForeignKey(
        entity = RunEntity::class,
        parentColumns = ["id"],
        childColumns = ["runId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("runId")]
)
data class RunSplit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val runId: Long,
    val kmNumber: Int,
    val splitMs: Long
)
