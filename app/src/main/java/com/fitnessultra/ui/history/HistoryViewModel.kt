package com.fitnessultra.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fitnessultra.data.db.AppDatabase
import com.fitnessultra.data.db.entity.LocationPoint
import com.fitnessultra.data.db.entity.RunEntity
import com.fitnessultra.data.db.entity.RunSplit
import com.fitnessultra.data.repository.RunRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Calendar

/** Full snapshot of a deleted run so swipe-to-delete can be undone without losing the route. */
private data class RunSnapshot(
    val run: RunEntity,
    val points: List<LocationPoint>,
    val splits: List<RunSplit>
)

data class WeeklySummary(
    val thisWeekRuns: Int,
    val thisWeekKm: Float,
    val thisWeekMinutes: Long,
    val lastWeekRuns: Int,
    val lastWeekKm: Float,
    val lastWeekMinutes: Long
)

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RunRepository(
        AppDatabase.getInstance(application).runDao()
    )

    val runs = repository.allRuns.asLiveData()

    val prRunIds = repository.allRuns.map { runs ->
        val ids = mutableSetOf<Long>()
        if (runs.isNotEmpty()) {
            runs.maxByOrNull { it.distanceMeters }?.let { ids.add(it.id) }
            runs.filter { it.avgSpeedKmh > 0 }.maxByOrNull { it.avgSpeedKmh }?.let { ids.add(it.id) }
        }
        ids
    }.asLiveData()

    val weeklySummary = repository.allRuns.map { runs ->
        val thisWeekStart = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        val lastWeekStart = (thisWeekStart.clone() as Calendar).apply {
            add(Calendar.WEEK_OF_YEAR, -1)
        }

        val thisWeek = runs.filter { it.dateTimestamp >= thisWeekStart.timeInMillis }
        val lastWeek = runs.filter {
            it.dateTimestamp in lastWeekStart.timeInMillis until thisWeekStart.timeInMillis
        }

        WeeklySummary(
            thisWeekRuns     = thisWeek.size,
            thisWeekKm       = thisWeek.sumOf { it.distanceMeters.toDouble() }.toFloat() / 1000f,
            thisWeekMinutes  = thisWeek.sumOf { it.durationMillis } / 60_000L,
            lastWeekRuns     = lastWeek.size,
            lastWeekKm       = lastWeek.sumOf { it.distanceMeters.toDouble() }.toFloat() / 1000f,
            lastWeekMinutes  = lastWeek.sumOf { it.durationMillis } / 60_000L
        )
    }.asLiveData()

    // Holds the most recently deleted run so it can be fully restored on Undo.
    private var lastDeleted: RunSnapshot? = null

    fun deleteRun(run: RunEntity) {
        viewModelScope.launch {
            // Snapshot route + splits before the FK CASCADE delete removes them.
            lastDeleted = RunSnapshot(
                run = run,
                points = repository.getLocationPointsForRun(run.id),
                splits = repository.getSplitsForRun(run.id)
            )
            repository.deleteRun(run)
        }
    }

    fun restoreLastDeleted() {
        val snapshot = lastDeleted ?: return
        lastDeleted = null
        viewModelScope.launch {
            // Same primary key → thumbnail file and PR logic stay valid.
            repository.insertRun(snapshot.run)
            if (snapshot.points.isNotEmpty()) repository.insertLocationPoints(snapshot.points)
            if (snapshot.splits.isNotEmpty()) repository.insertSplits(snapshot.splits)
        }
    }
}
