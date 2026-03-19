# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Status

App **builds and runs on device**. All core features implemented. Build is clean on `main`.
Open in Android Studio — Gradle sync and dependency download happens automatically.

## Tech Stack

- **Language:** Kotlin
- **Maps:** OSMDroid 6.1.18 (OpenStreetMap — no API key required)
- **GPS:** Fused Location Provider API (`play-services-location:21.2.0`)
- **Step Counter:** `Sensor.TYPE_STEP_COUNTER` (with `TYPE_STEP_DETECTOR` fallback) via `SensorManager`
- **Local DB:** Room 2.6.1 (version 3, migrations 1→2→3)
- **Charts:** MPAndroidChart v3.1.0 (via JitPack)
- **Voice:** Android TextToSpeech (milestones + workout coaching)
- **Architecture:** MVVM, Navigation Component, ViewBinding, Coroutines + Flow

## Package Structure

```
com.fitnessultra
├── data/
│   ├── db/           ← AppDatabase (v3, singleton + MIGRATION_1_2 + MIGRATION_2_3), entities, DAOs
│   └── repository/   ← RunRepository, WeightRepository
├── service/
│   └── TrackingService.kt   ← Foreground service: GPS, timer, step counter, LiveData
├── ui/
│   ├── run/          ← RunFragment, RunViewModel, WorkoutSetupBottomSheet, WorkoutConfig, RunWidgetProvider
│   ├── history/      ← HistoryFragment, RunAdapter (swipe-to-delete + Undo, tap→charts, PR badges, thumbnails)
│   ├── charts/       ← ChartsFragment, ChartsViewModel (per-run: speed/elevation/pace/splits/cadence/GPX)
│   ├── replay/       ← ReplayFragment (animated route replay, scrubber)
│   ├── goals/        ← GoalsFragment, GoalsViewModel (weekly distance/time/steps)
│   └── weight/       ← WeightFragment, WeightViewModel, BmiGaugeView
├── util/
│   ├── TrackingUtils.kt   ← All user-visible string functions require Context (see below)
│   ├── SettingsManager.kt ← Central settings helper
│   ├── GpxExporter.kt     ← Generates GPX XML from LocationPoint list
│   └── ThumbnailUtils.kt  ← Renders route Canvas→Bitmap (128×128 px, dark background, blue route)
└── MainActivity.kt   ← BottomNavigationView (5 tabs: Run · History · Charts · Goals · User Info)
```

## Navigation

**Bottom nav has 5 tabs:** Run · History · Charts · Goals · User Info (`weightFragment`)
`nav_graph.xml`: `historyFragment` → `chartsFragment` → `replayFragment`, all passing `runId: Long`.

## Theme / UI

- **Palette:** Sky-blue `#0EA5E9` (primary) + emerald `#10B981` (secondary)
- **Theme:** `Theme.MaterialComponents.DayNight.NoActionBar`
- **Dark mode:** `values-night/colors.xml` (slate-900 surfaces) + `values-night/themes.xml`
- **Run screen:** 52sp timer in `?attr/colorPrimary`, uppercase 10sp stat labels, 14dp pill buttons, vertical dividers between stat columns

## Key Architecture Decisions

- **TrackingService** extends `LifecycleService`, exposes data via companion `MutableLiveData`: `isTracking`, `pathPoints`, `rawLocations` (full `Location` objects), `timeRunInMillis`, `currentSpeedKmh`, `totalDistanceMeters`, `elevationGainMeters`, `stepCount`, `kmSplits`.
- **`rawLocations`**: companion `MutableLiveData<MutableList<Location>>` — stores full `android.location.Location` objects alongside GeoPoints. Used to build `LocationPoint` rows with real `location.time`, `location.altitude`, `location.speed`.
- **`onStartCommand` must call `super.onStartCommand(intent, flags, startId)`** — LifecycleService requires it.
- **NotificationChannel creation** wrapped in `if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)` — min SDK is 24.
- **TrackingUtils string functions all require `Context`:**
  - `formatDistance(meters, useMiles, context)`
  - `formatSpeedKmh(kmh, useMiles, context)`
  - `calculatePace(meters, ms, useMiles = false, context)`
  - `distanceUnitLabel(useMiles, context)`
  - `speedUnitLabel(useMiles, context)`
  - Context-free: `formatTime`, `calculatePaceSec`, `calculateCalories`, `toKm`, `fromKm`
- **WorkoutConfig** sealed class: `FreeRun` / `Intervals(runSeconds, walkSeconds, reps)` / `TargetPace(paceSecPerUnit, toleranceSec=30)`
- **Run screen workout selection:** START button starts immediately with current `workoutConfig` (default `FreeRun`). CONFIG button (outlined) shows `PopupMenu` anchored above it — sets default configs directly (`Intervals(60,30,5)` or `TargetPace(360)`), no bottom sheet. CONFIG button goes filled/primary when non-FreeRun is selected; resets to outlined when run stops. `WorkoutSetupBottomSheet` still exists but is NOT triggered from the popup.
- **Interval timer** uses `waitActiveSeconds()` coroutine — only counts elapsed when `isTracking == true`. Uses `run { repeat(...) }` block inside `launch` so `isActive` is accessible. Requires `import kotlinx.coroutines.isActive`.
- **Notification actions:** [PAUSE/RESUME] + [FINISH] buttons. FINISH sends `ACTION_STOP_AND_SAVE` which calls `saveRunToDb()` inline before stopping the service.
- **`saveRunToDb()`** in `TrackingService`: `suspend fun` with entire body wrapped in `withContext(Dispatchers.IO)` — safe from any coroutine context. Mirrors `RunViewModel.saveRun()` logic.
- **Map tile sources:** CYCLEMAP/PUBLIC_TRANSPORT removed from OSMDroid 6.x. Use `SettingsManager.tileSource(context)` which returns custom `XYTileSource` for CyclOSM/HOT OSM.
- **Room DB v3:** 4 tables: `runs`, `location_points` (FK → runs CASCADE), `weight_entries`, `run_splits` (FK → runs CASCADE). Migrations: 1→2 adds `stepCount`, 2→3 creates `run_splits` + index.
- **SharedPreferences** (`user_prefs`): `weight_kg`, `height_cm`, `height_m`, `age`.
- **BMI gauge** (`BmiGaugeView`): custom `View`, semicircle Canvas, 4 color zones, rotating needle.
- **Replay scrubber:** `isScrubbing` flag prevents feedback loop between SeekBar and `updatePositionAt()`.
- **Language switching:** `AppCompatDelegate.setApplicationLocales()` for runtime locale change.
- **Auto-pause:** 3 consecutive GPS updates < 1 km/h → pause. Uses `slowUpdateCount` field in `TrackingService`.
- **Personal Records (PRs):** `HistoryViewModel.prRunIds: LiveData<Set<Long>>` — longest-distance and fastest-pace run IDs. `RunAdapter` shows gold `⭐ PR` badge.
- **Route thumbnails:** `ThumbnailUtils.render(List<Location>)` → `filesDir/thumbnails/{runId}.png`. Loaded in `RunAdapter` (72dp ImageView).
- **Cadence:** `steps * 60000 / durationMs`. Shown live in RunFragment and post-run in ChartsFragment.
- **GPX export:** `GpxExporter.generate(run, points)` → `cacheDir/gpx/`, shared via `FileProvider` + `Intent.ACTION_SEND`. FileProvider authority: `{packageName}.fileprovider`. Paths config: `res/xml/file_paths.xml`.
- **Weekly summary:** `WeeklySummary` data class in `HistoryViewModel`, derived from `allRuns` Flow. Shown as card at top of HistoryFragment with delta vs last week.
- **Home screen widget:** `RunWidgetProvider` + `widget_run.xml`. Updated every second from `TrackingService.startTimer()`. Shows live timer/distance/pace or idle state.

## i18n Rules

- **No hardcoded string literals in any `.text =` or `.setText()` call** — all user-visible strings use `getString(R.string.*)`.
- Format/unit strings (`format_distance_km`, `unit_km`, etc.) are in `values/strings.xml` with `translatable="false"`.
- All features translated: English (default) · Greek (`values-el`) · Spanish (`values-es`) · German (`values-de`).

## Permissions (AndroidManifest)

`ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`, `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_LOCATION`, `POST_NOTIFICATIONS`, `INTERNET`, `ACTIVITY_RECOGNITION`, `WAKE_LOCK`

## Build Notes

- Min SDK: 24, Target SDK: 34, Gradle 8.4
- OSMDroid: set `Configuration.getInstance().userAgentValue` before map use (done in `FitnessUltraApp.onCreate`)
- `fallbackToDestructiveMigration` is **not used** — proper migrations defined in `AppDatabase`
- Inside `TrackingService` (a `Context` subclass), use `NOTIFICATION_SERVICE`, `SENSOR_SERVICE`, `POWER_SERVICE`, `MODE_PRIVATE` directly — no `Context.` qualifier needed

## What Still Needs Building

- Supabase integration (future — user accounts, cloud sync, leaderboards)
