# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Status

App **runs on device**. All core features implemented and tested. Open in Android Studio — Gradle sync and dependency download happens automatically.

## Tech Stack

- **Language:** Kotlin
- **Maps:** OSMDroid 6.1.18 (OpenStreetMap — no API key required)
- **GPS:** Fused Location Provider API (`play-services-location:21.2.0`)
- **Step Counter:** `Sensor.TYPE_STEP_DETECTOR` via `SensorManager`
- **Local DB:** Room 2.6.1 (version 2, migration 1→2 adds `stepCount` to `runs`)
- **Charts:** MPAndroidChart v3.1.0 (via JitPack)
- **Voice:** Android TextToSpeech (1km milestones during run)
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
│   ├── run/          ← RunFragment + RunViewModel (OSMDroid map, TTS, step counter display)
│   ├── history/      ← HistoryFragment + RunAdapter (swipe-to-delete + Undo, tap→charts)
│   ├── charts/       ← ChartsFragment + ChartsViewModel (per-run: speed/elevation/pace)
│   └── weight/       ← WeightFragment + BmiGaugeView (weight history, BMI gauge, charts)
├── util/
│   └── TrackingUtils.kt
└── MainActivity.kt   ← BottomNavigationView (3 tabs: Run, History, User Info)
```

## Navigation

**Bottom nav has 3 tabs:** Run · History · User Info (`weightFragment`)
Charts is **not** a bottom nav tab — it opens from History when a run is tapped.
`nav_graph.xml`: `historyFragment` → `chartsFragment` via `action_historyFragment_to_chartsFragment`, passing `runId: Long` as argument.

## Key Architecture Decisions

- **TrackingService** extends `LifecycleService`, exposes data via companion `MutableLiveData`: `isTracking`, `pathPoints`, `timeRunInMillis`, `currentSpeedKmh`, `totalDistanceMeters`, `elevationGainMeters`, `stepCount`. All observed through `RunViewModel`.
- **Step counter:** `TYPE_STEP_DETECTOR` registered in `TrackingService.startStepCounter()` on `ACTION_START_OR_RESUME`, unregistered on pause/stop. Requires `ACTIVITY_RECOGNITION` permission (API 29+, requested in `RunFragment`).
- **GeoPoint** (OSMDroid) used everywhere instead of Google's `LatLng`.
- **Room DB v2:** 3 tables: `runs` (includes `stepCount`), `location_points` (FK → runs CASCADE), `weight_entries`. Migration 1→2: `ALTER TABLE runs ADD COLUMN stepCount INTEGER NOT NULL DEFAULT 0` — preserves all data.
- **SharedPreferences** (`user_prefs`): `weight_kg`, `height_cm`, `height_m`, `age` — shared between RunViewModel (calories) and WeightViewModel (BMI).
- **BMI gauge** (`BmiGaugeView`): custom `View` drawn on Canvas, semicircle with 4 colored segments (blue/green/orange/red) + rotating needle. Located at the bottom of the User Info screen. Shown only when height + ≥1 weight entry exist.
- **Weight history chart:** requires ≥2 entries to draw (MPAndroidChart colored segments per direction).
- **Charts per run:** `ChartsFragment` reads `runId` from `arguments`, loads location points + run summary from DB in a single coroutine. No spinner.

## Permissions (AndroidManifest)

`ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`, `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_LOCATION`, `POST_NOTIFICATIONS`, `INTERNET`, `ACTIVITY_RECOGNITION`

## Build Notes

- Min SDK: 24, Target SDK: 34, Gradle 8.4, Room v2
- OSMDroid: set `Configuration.getInstance().userAgentValue` before map use (done in `RunFragment.onViewCreated`)
- `fallbackToDestructiveMigration` is **not used** — proper `MIGRATION_1_2` is defined in `AppDatabase`

## What Still Needs Building

- Run replay screen (load past route on OSMDroid map)
- Goals screen (distance/time targets with progress)
- Supabase integration (user accounts, leaderboards)
