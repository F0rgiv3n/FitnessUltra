<div align="center">

# 🏃 FitnessUltra

**A full-featured, offline-first Android run tracker built with Kotlin.**

Track outdoor runs with GPS, monitor weight & BMI, replay past routes, and review detailed per-run analytics — with **no cloud, no accounts and no API keys**.

![Platform](https://img.shields.io/badge/Platform-Android%207.0%2B-3DDC84?logo=android&logoColor=white)
![Language](https://img.shields.io/badge/Kotlin-100%25-7F52FF?logo=kotlin&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-MVVM-0EA5E9)
![Maps](https://img.shields.io/badge/Maps-OpenStreetMap-7EBC6F)
![Offline](https://img.shields.io/badge/Works-100%25%20Offline-success)
![License](https://img.shields.io/badge/License-MIT-yellow)

**English** · [Ελληνικά](README.el.md)

</div>

---

## 🛜 Runs fully offline

FitnessUltra needs **no internet to track a run**. GPS, the timer, sensors, calorie/elevation math and all your data are 100% on-device (Room database). **Pre-download map tiles** for any area beforehand and the live map keeps rendering your route even with mobile data off — ideal for trails, mountains, or saving battery & data. No Google Maps, no API key, no sign-in.

---

## 📥 Download

Two ways to get the app:

| | Option | How |
|---|---|---|
| 📦 | **Install the APK** | Download the latest APK from the **[Releases page](../../releases/latest)** and sideload it (see [Install](#-install)). |
| 🛠️ | **Build from source** | Open the project in **Android Studio** and press **Run ▶** (see [Install](#-install)). |

---

## 📸 Screenshots

> Demo runs recorded in **Trikala, Greece** (Άι Γιώργης → Μύλος Ματσόπουλου), routed along real streets.

| Live GPS Tracking | History & PRs | Per-run Charts |
|:---:|:---:|:---:|
| <img src="docs/screenshots/01_run_tracking.png" width="230"/> | <img src="docs/screenshots/02_history.png" width="230"/> | <img src="docs/screenshots/03_charts.png" width="230"/> |
| **Weekly Goals** | **Weight & BMI** | **Route Replay** |
| <img src="docs/screenshots/04_goals.png" width="230"/> | <img src="docs/screenshots/05_weight_bmi.png" width="230"/> | <img src="docs/screenshots/08_replay.png" width="230"/> |

---

## ✨ Full feature list

### 🏃 Run tracking
- Real-time **GPS tracking** on a live **OpenStreetMap** map (no API key), with an auto-centering position marker and the route drawn live
- Live **distance · speed · pace · elapsed time · elevation gain · cadence** (steps/min)
- **Hardware step counter** (`TYPE_STEP_COUNTER`, `TYPE_STEP_DETECTOR` fallback), speed-gated to ignore false counts while stationary
- **Calorie calculation** using the di Prampero running formula **+ elevation cost**
- **Voice coaching** — spoken announcements at configurable distance milestones, in your chosen language
- **Foreground service + WakeLock** — tracking continues with the screen off / app in background
- Live **notification** (time / distance / pace) with **Pause · Resume · Finish** actions
- **3-2-1 countdown** with beeps before tracking starts
- **Auto-pause** when you stop (< 1 km/h) and optional **auto-resume** on movement
- **Home-screen widget** showing live timer, distance and pace

### 🎯 Workout modes
| Mode | Description |
|---|---|
| **Free Run** | Standard run, no guidance |
| **Interval Training** | Alternating run/walk phases — configurable durations & reps; TTS + a distinct beep at every transition; the interval timer only counts active (non-paused) time |
| **Target Pace** | Set a goal pace; spoken alerts + colour feedback on the pace when you drift too fast or too slow |

### 📜 History
- Full run log with **swipe-to-delete + Undo** (restores the complete route, splits and thumbnail)
- **Route thumbnails** — a rendered mini-map of each run's GPS track
- **Personal Record (⭐ PR)** badges — longest distance & fastest pace, detected automatically
- **Weekly summary** card with delta vs. the previous week
- **Step history** bar chart (last 7 days / 4 weeks / 6 months)

### 📊 Run analysis (Charts)
- Summary: distance, duration, calories, steps, average cadence, average speed
- **Speed**, **elevation** and **pace** charts over time
- **Per-kilometre split** table with the best split highlighted
- **GPX export** — share a `.gpx` compatible with Strava, Garmin Connect, etc.
- **Share run** — a generated summary image (Instagram Stories or any app)

### ▶️ Run Replay
- **Animated playback** of the route on a live map
- Adjustable speed: **1× / 2× / 5×**
- **Scrubber bar** — drag to any moment; playback pauses while scrubbing and resumes after
- Live elapsed time, distance covered and speed update as you scrub

### 🏆 Goals
- Weekly targets for **distance · time · steps**
- Progress bars with colour feedback (gray → primary → green at 100%)
- Day-dot row showing which days of the week you were active

### ⚖️ Weight & BMI
- Personal data (height, age) stored locally
- **Weight history** log with a colour-coded trend chart (kg or lbs)
- **BMI history** chart and a **BMI gauge** — a semicircle dial with WHO colour zones (Underweight / Normal / Overweight / Obese)

### 🗺️ Offline maps
- **Download map tiles** for any area to use the app with **zero internet**
- Detail levels: Normal (zoom 10–14) · Detailed (10–16) · HD (10–17)
- Tile estimate (count + MB) shown before download
- Parallel download (8 connections) with a progress bar
- Saved areas listed with name, date and tile count; tap to preview on the map

### ⚙️ Settings & personalization
| Category | Options |
|---|---|
| **Units** | Distance km / miles · Weight kg / lbs · Gender (for calories) |
| **Run** | GPS accuracy · Auto-pause · Auto-resume · Keep screen on · Countdown |
| **Map** | Style: Standard / CyclOSM / HOT · Auto-center on position |
| **Voice** | Enable · frequency · language (device / English / Greek / Spanish / German) |
| **Appearance** | Theme: System / Light / Dark · App language **EN / EL / ES / DE** |
| **Offline maps** | Download, manage and preview saved areas |

---

## 🎯 Sensor accuracy

| Sensor | Implementation |
|---|---|
| **GPS** | 1 Hz updates, ≤20 m accuracy filter, fast first fix, 1 m jitter suppression, teleport guard (>120 km/h rejected) |
| **Speed** | GPS Doppler speed, EMA-smoothed (α=0.5) |
| **Distance** | Accumulated only from ≤20 m fixes; first fix accepted at ≤50 m for instant map display |
| **Elevation** | Barometer (`TYPE_PRESSURE`, ±1–2 m) when available, EMA-smoothed; GPS-altitude fallback |
| **Steps** | Hardware counter, speed-gated (ignored below 0.5 km/h), gate active only after the first GPS fix |
| **Calories** | di Prampero formula (1.036 / 0.945 kcal·kg⁻¹·km⁻¹) + elevation cost (0.009 kcal·kg⁻¹·m⁻¹) |

---

## 🛠️ Tech stack

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

## 📲 Install

### Option 1 — Download the APK (no build tools needed)
1. On your Android phone, open the **[Releases page](../../releases/latest)** and download `FitnessUltra-v1.0.apk`.
2. Open the file. If prompted, allow your browser/file-manager to **install unknown apps**.
3. Tap **Install → Open**.
4. Grant **Location**, **Physical activity** and **Notifications** permissions, then press **START**.

### Option 2 — Build & run from Android Studio
1. Clone the repo and open it in **Android Studio** (Hedgehog or newer).
2. Let Gradle sync & download dependencies automatically (no API keys required).
3. Connect a device or start an emulator (API 24+) and press **Run ▶**.

<details>
<summary>Command-line build</summary>

```bash
# Debug APK
./gradlew assembleDebug      # → app/build/outputs/apk/debug/app-debug.apk

# Install onto a connected device / running emulator
./gradlew installDebug
```
</details>

---

## 🗄️ Database

Room v3 — four tables: `runs`, `location_points` (FK→runs CASCADE), `weight_entries`, `run_splits` (FK→runs CASCADE).
Migrations: **1→2** adds `stepCount`; **2→3** creates `run_splits` + index. Schema is exported to `app/schemas/` for migration testing.

---

## 🔒 Permissions

`ACCESS_FINE_LOCATION` · `ACCESS_COARSE_LOCATION` · `FOREGROUND_SERVICE` · `FOREGROUND_SERVICE_LOCATION` · `POST_NOTIFICATIONS` · `INTERNET` · `ACTIVITY_RECOGNITION` · `WAKE_LOCK`

> `INTERNET` is used only to fetch map tiles online; with pre-downloaded offline maps the app works with it effectively unused.

---

## 🗺️ Roadmap

- [ ] Optional cloud sync & leaderboards (Supabase)
- [ ] Wear OS companion
- [ ] Unit tests for `TrackingUtils` & Room migrations

---

## 📄 License

Released under the [MIT License](LICENSE).
