# FitnessUltra

A personal Android fitness tracking app built with Kotlin. Track outdoor runs with GPS, monitor your weight and BMI, replay past routes, and review detailed per-run stats and charts — all without any cloud dependency or API keys.

---

## Screenshots

> *(coming soon)*

---

## Features

### Running
- Real-time GPS tracking with live map (OpenStreetMap, no API key)
- Live position marker on map, auto-centered during run
- Distance, speed, pace, elapsed time, elevation gain, and **cadence (steps/min)** displayed live
- Step counter via `TYPE_STEP_COUNTER` (with `TYPE_STEP_DETECTOR` fallback), speed-gated to ignore false counts when stationary
- Voice announcements at configurable distance milestones in your chosen language
- **Calorie calculation** accounting for both running distance and elevation gain
- Foreground service with WakeLock — keeps tracking alive with screen off
- Live notification with time, distance, pace + Pause / Resume / Finish actions
- 3-2-1 countdown overlay with beep before tracking starts
- **Auto-pause** when speed drops below 1 km/h (3 consecutive updates); optional **auto-resume** when movement is detected
- Home screen widget showing live timer, distance, and pace

### Workout Modes

| Mode | Description |
|---|---|
| **Free Run** | Standard run with no additional guidance |
| **Interval Training** | Alternates run/walk phases with configurable durations and reps; TTS announces each phase with a distinct high-pitched beep at every transition |
| **Target Pace** | Set a target pace; TTS alerts and color feedback on the pace display when too fast or too slow |

- Interval timer correctly pauses with the run — only counts active tracking time
- Countdown beeps at the last 3 seconds of each phase; a longer, higher-pitched beep marks every transition

### History
- Full run log with swipe-to-delete and Undo
- **Route thumbnails** — each run shows a rendered minimap of the GPS route
- **Personal Record (⭐ PR) badges** — longest distance and fastest pace highlighted automatically
- Weekly summary card at the top with delta vs. the previous week
- Tap any run → detailed charts and route map

### Run Analysis (Charts)
Per-run detail screen:
- Summary card: distance, duration, calories, steps, average cadence, avg speed
- Speed, elevation, and pace charts over time
- Per-km split table with best split highlighted
- **GPX export** — share as `.gpx` compatible with Strava, Garmin Connect, etc.

### Run Replay
- Animated playback of the GPS route on a live map
- Adjustable speed: 1× / 2× / 5×
- Scrubber bar — drag to any point; playback pauses during scrubbing and resumes after
- Live elapsed time, distance covered, and current speed update while scrubbing

### Goals
- Weekly targets for distance, time, and steps
- Progress bars with colour feedback (gray → primary → green at 100%)
- Day-dot row showing which days of the week you were active

### Weight & BMI
- Personal data (height, age) stored locally
- Weight history log with colour-coded trend chart (kg or lbs)
- BMI history chart
- BMI gauge — semicircle dial with WHO colour-coded zones:

| Zone | BMI |
|---|---|
| Underweight | < 18.5 |
| Normal | 18.5 – 24.9 |
| Overweight | 25 – 29.9 |
| Obese | ≥ 30 |

### Offline Maps
- Download map tiles for any area for use without internet
- Configurable detail level: Normal (zoom 10–14), Detailed (10–16), HD (10–17)
- Tile estimate (count + MB) shown before downloading
- Parallel download (8 connections) with progress bar
- Saved areas listed with name, date, tile count; tap to preview on map

### Settings

| Category | Options |
|---|---|
| **Units** | Distance: km / miles · Weight: kg / lbs · Gender for calorie calculation |
| **Run** | GPS accuracy · Auto-pause · Auto-resume · Keep screen on · Countdown |
| **Map** | Style: Standard / CyclOSM / HOT · Auto-center on position |
| **Voice** | Enable / frequency / language (device default, English, Greek, Spanish, German) |
| **Appearance** | Theme: system / light / dark · App language (EN / EL / ES / DE) |
| **Offline Maps** | Download, manage, and preview downloaded map areas |

---

## Sensor Accuracy

| Sensor | Implementation |
|---|---|
| **GPS** | 1 Hz updates, ≤20m accuracy filter, `setWaitForAccurateLocation(false)` for fast first fix, `setMinUpdateDistanceMeters(1m)` to suppress jitter |
| **Speed** | GPS Doppler speed (more accurate than position-derived), exponential moving average (α=0.5) to smooth display |
| **Distance** | Accumulated only from ≤20m accuracy fixes; first fix accepted at ≤50m for instant map display without counting toward distance |
| **Elevation** | Barometric pressure sensor (`TYPE_PRESSURE`) when available (±1–2m vs ±15–30m for GPS altitude), EMA smoothed; falls back to GPS altitude |
| **Steps** | Hardware step counter, speed-gated (discards steps when GPS speed < 0.5 km/h); gate only activates after first GPS fix to avoid missing steps at start |
| **Calories** | di Prampero formula (1.036 kcal/kg/km male, 0.945 female) + elevation cost (0.009 kcal/kg/m) |

---

## Tech Stack

| Layer | Library / Tool |
|---|---|
| Language | Kotlin |
| Architecture | MVVM, Navigation Component, ViewBinding, Coroutines + Flow |
| Maps | OSMDroid 6.1.18 (OpenStreetMap) |
| GPS | Fused Location Provider (play-services-location 21.2.0) |
| Step Counter | `Sensor.TYPE_STEP_COUNTER` / `TYPE_STEP_DETECTOR` |
| Elevation | `Sensor.TYPE_PRESSURE` (barometer) with GPS fallback |
| Database | Room 2.6.1 (v3, proper migrations) |
| Charts | MPAndroidChart v3.1.0 |
| Voice | Android TextToSpeech |
| UI | Material Components 1.11.0 |
| Settings | AndroidX Preference (PreferenceFragmentCompat) |

---

## Requirements

- Android 7.0+ (API 24)
- GPS / Location permission
- Activity Recognition permission (step counter, API 29+)
- Notification permission (API 33+)

---

## Building

1. Clone the repo
2. Open in **Android Studio** (Hedgehog or newer)
3. Let Gradle sync and download dependencies automatically
4. Run on a physical device or emulator with API 24+

No API keys or external accounts required.

---

## Database

Room database, version 3. Four tables:

| Table | Description |
|---|---|
| `runs` | One row per run (distance, duration, calories, steps, avg speed, elevation gain, timestamp) |
| `location_points` | GPS points per run (lat, lon, altitude, speed, timestamp) — FK → runs CASCADE |
| `weight_entries` | Weight log entries with timestamps |
| `run_splits` | Per-km split times — FK → runs CASCADE |

Migrations:
- 1→2: adds `stepCount INTEGER` to `runs`
- 2→3: creates `run_splits` table with index on `runId`

---

## Permissions

```
ACCESS_FINE_LOCATION
ACCESS_COARSE_LOCATION
FOREGROUND_SERVICE
FOREGROUND_SERVICE_LOCATION
POST_NOTIFICATIONS
INTERNET
ACTIVITY_RECOGNITION
WAKE_LOCK
```

---

## Roadmap

- [ ] Supabase integration (optional cloud sync, leaderboards)
