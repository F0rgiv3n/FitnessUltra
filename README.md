<div align="center">

# 🏃 FitnessUltra

**A full-featured, offline-first Android run tracker built with Kotlin.**

Track outdoor runs with GPS, monitor weight & BMI, replay past routes, and review detailed per-run analytics — with **no cloud dependency and no API keys**.

![Platform](https://img.shields.io/badge/Platform-Android%207.0%2B-3DDC84?logo=android&logoColor=white)
![Language](https://img.shields.io/badge/Kotlin-100%25-7F52FF?logo=kotlin&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-MVVM-0EA5E9)
![Maps](https://img.shields.io/badge/Maps-OpenStreetMap-7EBC6F)
![License](https://img.shields.io/badge/License-MIT-yellow)

**English** · [Ελληνικά](README.el.md)

</div>

---

## 📥 Download

Grab the signed release APK and sideload it on any Android 7.0+ device:

➡️ **[release/FitnessUltra-v1.0.apk](release/FitnessUltra-v1.0.apk)**

> On your phone: enable **Settings → Security → Install unknown apps** for your browser/file manager, then open the APK. See [How to install](#-how-to-install-the-apk) below.

---

## 📸 Screenshots

> Demo runs recorded in **Trikala, Greece** (Άι Γιώργης → Μύλος Ματσόπουλου).

| Live GPS Tracking | History & PRs | Per-run Charts |
|:---:|:---:|:---:|
| <img src="docs/screenshots/01_run_tracking.png" width="230"/> | <img src="docs/screenshots/02_history.png" width="230"/> | <img src="docs/screenshots/03_charts.png" width="230"/> |
| **Weekly Goals** | **Weight & BMI** | **Interval Training** |
| <img src="docs/screenshots/04_goals.png" width="230"/> | <img src="docs/screenshots/05_weight_bmi.png" width="230"/> | <img src="docs/screenshots/07_workout_intervals.png" width="230"/> |

---

## ✨ Features

### 🏃 Running
- Real-time GPS tracking on a live **OpenStreetMap** map (no API key) with an auto-centering position marker
- Live **distance, speed, pace, time, elevation gain, and cadence** (steps/min)
- Hardware step counter (`TYPE_STEP_COUNTER`, with `TYPE_STEP_DETECTOR` fallback), speed-gated to ignore false counts while stationary
- **Voice announcements** at configurable distance milestones, in your chosen language
- **Calorie calculation** accounting for both distance and elevation gain
- Foreground service + WakeLock — keeps tracking alive with the screen off
- Live **notification** (time / distance / pace) with Pause · Resume · Finish actions
- 3-2-1 **countdown** with beep before tracking starts
- **Auto-pause** below 1 km/h and optional **auto-resume** on movement
- **Home screen widget** with live timer, distance and pace

### 🎯 Workout Modes
| Mode | Description |
|---|---|
| **Free Run** | Standard run, no guidance |
| **Interval Training** | Alternating run/walk phases with configurable durations & reps; TTS + beeps at each transition |
| **Target Pace** | Set a goal pace; TTS alerts and colour feedback when too fast/slow |

### 📜 History
- Full run log with **swipe-to-delete + Undo** (restores the full route, splits and thumbnail)
- **Route thumbnails** — a rendered minimap per run
- **Personal Record (⭐ PR)** badges — longest distance & fastest pace
- Weekly summary card with delta vs. the previous week
- Per-day step history chart (7 days / 4 weeks / 6 months)

### 📊 Run Analysis
- Summary: distance, duration, calories, steps, cadence, avg speed
- **Speed / elevation / pace** charts over time + per-km **split table**
- **GPX export** — share a `.gpx` compatible with Strava, Garmin Connect, etc.
- **Run Replay** — animated route playback (1× / 2× / 5×) with a draggable scrubber

### 🏆 Goals · ⚖️ Weight · 🗺️ Offline Maps
- Weekly targets for **distance, time and steps** with progress bars
- **Weight & BMI** tracking with trend charts and a WHO-zone BMI gauge
- **Offline map** tile downloads for use without internet (configurable detail level)

### 🌍 Localization & Theming
- Full UI in **English · Greek · Spanish · German**
- Light / Dark / System theme
- Units: km / miles, kg / lbs

---

## 🎯 Sensor Accuracy

| Sensor | Implementation |
|---|---|
| **GPS** | 1 Hz updates, ≤20 m accuracy filter, fast first fix, 1 m jitter suppression, teleport guard (>120 km/h rejected) |
| **Speed** | GPS Doppler speed, EMA-smoothed (α=0.5) |
| **Distance** | Accumulated only from ≤20 m fixes; first fix accepted at ≤50 m for instant map display |
| **Elevation** | Barometer (`TYPE_PRESSURE`, ±1–2 m) when available, EMA-smoothed; GPS-altitude fallback |
| **Steps** | Hardware counter, speed-gated (ignored below 0.5 km/h), gate active only after first GPS fix |
| **Calories** | di Prampero formula (1.036 / 0.945 kcal·kg⁻¹·km⁻¹) + elevation cost (0.009 kcal·kg⁻¹·m⁻¹) |

---

## 🛠️ Tech Stack

| Layer | Library / Tool |
|---|---|
| Language | **Kotlin** |
| Architecture | **MVVM**, Navigation Component, ViewBinding, Coroutines + Flow |
| Maps | OSMDroid 6.1.18 (OpenStreetMap) |
| GPS | Fused Location Provider (play-services-location 21.2.0) |
| Sensors | `TYPE_STEP_COUNTER` / `TYPE_STEP_DETECTOR` / `TYPE_PRESSURE` |
| Database | **Room 2.6.1** (v3, hand-written migrations, exported schema) |
| Charts | MPAndroidChart 3.1.0 |
| Voice | Android TextToSpeech |
| UI | Material Components 1.11.0 |

### Architecture at a glance
```
com.fitnessultra
├── data/            Room DB (runs · location_points · weight_entries · run_splits) + repositories
├── service/         TrackingService — foreground GPS/timer/sensors, exposes LiveData
├── ui/
│   ├── run/         Live tracking, workout modes, home-screen widget
│   ├── history/     Run log, PR badges, thumbnails, weekly summary
│   ├── charts/      Per-run analytics + GPX export
│   ├── replay/      Animated route replay
│   ├── goals/       Weekly distance/time/steps targets
│   ├── weight/      Weight log + BMI gauge
│   └── settings/    Preferences + offline-map manager
└── util/            TrackingUtils · SettingsManager · GpxExporter · ThumbnailUtils
```

---

## 🚀 Build & Run

### Option A — Android Studio (easiest)
1. Clone the repo
2. Open in **Android Studio** (Hedgehog or newer)
3. Let Gradle sync & download dependencies automatically
4. Press **Run ▶** on a device/emulator with API 24+

### Option B — Command line
```bash
# Debug build
./gradlew assembleDebug
# → app/build/outputs/apk/debug/app-debug.apk

# Install onto a connected device / running emulator
./gradlew installDebug
```

> No API keys or accounts required. To build a **signed release**, create a `keystore.properties` at the repo root (see [Release signing](#-release-signing)).

---

## 📲 How to install the APK

1. Download **[FitnessUltra-v1.0.apk](release/FitnessUltra-v1.0.apk)** to your Android phone.
2. Open it with a file manager. If prompted, allow your browser/file-manager to **install unknown apps**.
3. Tap **Install** → **Open**.
4. Grant **Location**, **Physical activity** and **Notifications** permissions when asked, then press **START** to track your first run.

---

## 🔐 Release signing

Release builds are signed from a local, git-ignored `keystore.properties`:
```properties
storeFile=release.keystore
storePassword=********
keyAlias=********
keyPassword=********
```
The keystore and this file are excluded from version control. Without them, debug builds still work normally.

---

## 🗄️ Database

Room v3 — four tables: `runs`, `location_points` (FK→runs CASCADE), `weight_entries`, `run_splits` (FK→runs CASCADE).
Migrations: **1→2** adds `stepCount`; **2→3** creates `run_splits` + index. Schema is exported to `app/schemas/` for migration testing.

---

## 🔒 Permissions

`ACCESS_FINE_LOCATION` · `ACCESS_COARSE_LOCATION` · `FOREGROUND_SERVICE` · `FOREGROUND_SERVICE_LOCATION` · `POST_NOTIFICATIONS` · `INTERNET` · `ACTIVITY_RECOGNITION` · `WAKE_LOCK`

---

## 🗺️ Roadmap

- [ ] Optional cloud sync & leaderboards (Supabase)
- [ ] Wear OS companion
- [ ] Unit tests for `TrackingUtils` & Room migrations

---

## 📄 License

Released under the [MIT License](LICENSE).
