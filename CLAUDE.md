# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Status

Full Android project structure has been scaffolded. The app compiles but has **not yet been tested on a device**. Open in Android Studio — it will sync Gradle and download all dependencies automatically.

## Tech Stack

- **Language:** Kotlin
- **IDE:** Android Studio
- **Maps:** OSMDroid 6.1.18 (OpenStreetMap — no API key, no billing required)
- **GPS:** Fused Location Provider API (`play-services-location:21.2.0`)
- **Local DB:** Room 2.6.1
- **Charts:** MPAndroidChart v3.1.0 (via JitPack)
- **Voice:** Android TextToSpeech
- **Backend (future):** Supabase (user accounts, challenges, leaderboards)
- **Architecture:** MVVM, Navigation Component, ViewBinding, Coroutines + Flow

## Package Structure

```
com.fitnessultra
├── data/
│   ├── db/           ← AppDatabase (singleton), entities, DAOs
│   └── repository/   ← RunRepository, WeightRepository
├── service/
│   └── TrackingService.kt   ← Foreground service: GPS, timer, LiveData
├── ui/
│   ├── run/          ← RunFragment + RunViewModel (OSMDroid map, TTS)
│   ├── history/      ← HistoryFragment + RunAdapter (RecyclerView)
│   ├── charts/       ← ChartsFragment (speed / elevation / pace line charts)
│   └── weight/       ← WeightFragment (bar chart, BMI, weight diff)
├── util/
│   └── TrackingUtils.kt     ← formatTime, formatDistance, calculatePace, calculateCalories
└── MainActivity.kt          ← BottomNavigationView + NavHostFragment
```

## Key Architecture Decisions

- **TrackingService** extends `LifecycleService` and exposes data via companion object `MutableLiveData` (`isTracking`, `pathPoints`, `timeRunInMillis`, `currentSpeedKmh`, `totalDistanceMeters`, `elevationGainMeters`). Fragments observe these through `RunViewModel`.
- **GeoPoint** (OSMDroid) is used everywhere instead of Google's `LatLng`.
- **Room DB** has 3 tables: `runs`, `location_points` (FK → runs, CASCADE delete), `weight_entries`.
- **User weight** is persisted in `SharedPreferences` (`user_prefs` → `weight_kg`) when saved in WeightFragment, and read by RunFragment for calorie calculation.
- **Calorie formula:** `Distance(km) × Weight(kg) × 1.036`
- **Pace formula:** `durationMinutes / distanceKm` → formatted as `MM:SS / km`

## What Still Needs Building

- Run replay screen (load past route from DB and display on OSMDroid map)
- Goals screen (distance/time targets with progress bar)
- Supabase integration (future)
- Launcher icons (`@mipmap/ic_launcher` — currently missing, will cause build error)
- On-device testing and bug fixes

## Build Notes

- Minimum SDK: 24, Target SDK: 34, Gradle 8.4
- OSMDroid requires `INTERNET` permission (already in manifest) and setting `Configuration.getInstance().userAgentValue` before using the map (done in `RunFragment`)
- The Gradle wrapper JAR is not in the repo — Android Studio generates it on first open
