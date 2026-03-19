package com.fitnessultra.ui.settings

data class DownloadedMapArea(
    val id: String,
    val description: String,
    val latNorth: Double,
    val latSouth: Double,
    val lonWest: Double,
    val lonEast: Double,
    val downloadedAt: Long,
    val tileCount: Int
)
