# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Status

App **builds and runs on device**. All core features implemented. Build is clean on `main`.
Open in Android Studio — Gradle sync and dependency download happens automatically.

## Tech Stack

- **Language:** Kotlin
- **Maps:** OSMDroid 6.1.18 (OpenStreetMap — no API key required)
- **GPS:** Fused Location Provider API (`play-services-location:21.2.0`)
- **Step Counter:** `Sensor.TYPE_STEP_DETECTOR` via `SensorManager`
- **Local DB:** Room 2.6.1 (version 2, migration 1→2 adds `stepCount` to `runs`)
- **Charts:** MPAndroidChart v3.1.0 (via JitPack)
- **Voice:** Android TextToSpeech (milestones + workout coaching)
- **Architecture:** MVVM, Navigation Component, ViewBinding, Coroutines + Flow

## Package Structure

```
com.fitnessultra
├── data/
│   ├── db/           ← AppDatabase (v2, singleton + MIGRATION_1_2), entities, DAOs
│   └── repository/   ← RunRepository, WeightRepository
├── service/
│   └── TrackingService.kt   ← Foreground service: GPS, timer, step counter, LiveData
├── ui/
│   ├── run/          ← RunFragment, RunViewModel, WorkoutSetupBottomSheet, WorkoutConfig
│   ├── history/      ← HistoryFragment, RunAdapter (swipe-to-delete + Undo, tap→charts)
│   ├── charts/       ← ChartsFragment, ChartsViewModel (per-run: speed/elevation/pace)
│   ├── replay/       ← ReplayFragment (animated route replay, scrubber)
│   ├── goals/        ← GoalsFragment, GoalsViewModel (weekly distance/time/steps)
│   └── weight/       ← WeightFragment, WeightViewModel, BmiGaugeView
├── util/
│   ├── TrackingUtils.kt   ← All user-visible string functions require Context (see below)
│   └── SettingsManager.kt ← Central settings helper
└── MainActivity.kt   ← BottomNavigationView (5 tabs: Run · History · Charts · Goals · User Info)
```

## Navigation

**Bottom nav has 5 tabs:** Run · History · Charts · Goals · User Info (`weightFragment`)
`nav_graph.xml`: `historyFragment` → `chartsFragment` → `replayFragment`, all passing `runId: Long`.

## Key Architecture Decisions

- **TrackingService** extends `LifecycleService`, exposes data via companion `MutableLiveData`: `isTracking`, `pathPoints`, `timeRunInMillis`, `currentSpeedKmh`, `totalDistanceMeters`, `elevationGainMeters`, `stepCount`.
- **TrackingUtils string functions all require `Context`** — added in latest session:
  - `formatDistance(meters, useMiles, context)`
  - `formatSpeedKmh(kmh, useMiles, context)`
  - `calculatePace(meters, ms, useMiles = false, context)`
  - `distanceUnitLabel(useMiles, context)`
  - `speedUnitLabel(useMiles, context)`
  - Context-free: `formatTime`, `calculatePaceSec`, `calculateCalories`, `toKm`, `fromKm`
- **WorkoutConfig** sealed class: `FreeRun` / `Intervals(runSeconds, walkSeconds, reps)` / `TargetPace(paceSecPerUnit, toleranceSec=30)`
- **Interval timer** uses `waitActiveSeconds()` coroutine — only counts elapsed when `isTracking == true`. Uses `for` loop (not `repeat`) inside `launch` so `isActive` is accessible. Requires `import kotlinx.coroutines.isActive`.
- **Map tile sources:** CYCLEMAP/PUBLIC_TRANSPORT removed from OSMDroid 6.x. Use `SettingsManager.tileSource(context)` which returns custom `XYTileSource` for CyclOSM/HOT OSM.
- **Room DB v2:** 3 tables: `runs`, `location_points` (FK → runs CASCADE), `weight_entries`. Migration 1→2 adds `stepCount INTEGER NOT NULL DEFAULT 0`.
- **SharedPreferences** (`user_prefs`): `weight_kg`, `height_cm`, `height_m`, `age`.
- **BMI gauge** (`BmiGaugeView`): custom `View`, semicircle Canvas, 4 color zones, rotating needle.
- **Replay scrubber:** `isScrubbing` flag prevents feedback loop between SeekBar and `updatePositionAt()`.
- **Language switching:** `AppCompatDelegate.setApplicationLocales()` for runtime locale change.
- **Auto-pause:** 3 consecutive GPS updates < 1 km/h → pause. Uses `slowUpdateCount` field in `TrackingService`.

## i18n Rules

- **No hardcoded string literals in any `.text =` or `.setText()` call** — all user-visible strings use `getString(R.string.*)`.
- Format/unit strings (`format_distance_km`, `unit_km`, etc.) are in `values/strings.xml` with `translatable="false"`.
- All features translated: English (default) · Greek (`values-el`) · Spanish (`values-es`) · German (`values-de`).

## Permissions (AndroidManifest)

`ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`, `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_LOCATION`, `POST_NOTIFICATIONS`, `INTERNET`, `ACTIVITY_RECOGNITION`

## Build Notes

- Min SDK: 24, Target SDK: 34, Gradle 8.4
- OSMDroid: set `Configuration.getInstance().userAgentValue` before map use (done in `RunFragment.onViewCreated` and `ReplayFragment`)
- `fallbackToDestructiveMigration` is **not used** — proper `MIGRATION_1_2` is defined in `AppDatabase`

## What Still Needs Building

- Supabase integration (future — user accounts, cloud sync, leaderboards)
