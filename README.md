# FitnessUltra

A personal Android fitness tracking app built with Kotlin. Track outdoor runs with GPS, monitor your weight and BMI, replay past routes, and review detailed per-run stats and charts — all without any cloud dependency or API keys.

---

## Screenshots

> *(coming soon)*

---

## Features

### Running
- Real-time GPS tracking with live map (OpenStreetMap, no API key)
- **Live position marker** — blue dot on the map always centered at your current location during a run
- Distance, speed, pace, elapsed time, and live elevation gain displayed during run
- Step counter via device accelerometer
- Voice announcements at configurable distance milestones (1 / 2 / 5 km) in your chosen language
- Calories burned estimation (adjusts for gender)
- Foreground service with WakeLock — keeps tracking alive with screen off
- Live notification showing time, distance, pace + Pause/Resume button
- 3-2-1 countdown overlay before tracking starts

### Workout Modes
Choose a workout type before each run via a setup sheet:

| Mode | Description |
|---|---|
| **Free Run** | Standard run with no additional guidance |
| **Interval Training** | Alternates run/walk segments with configurable durations and reps; TTS announces each phase |
| **Target Pace** | Set a target pace (min/km or min/mi); TTS alerts and pace color feedback when too fast or too slow |

Interval timer correctly pauses when the run is paused — only counts active tracking time.

### History
- Full run log with swipe-to-delete and undo
- Steps bar chart per day / week / month
- Tap any run → detailed charts (speed, elevation, pace)

### Run Replay
- Animated playback of your GPS route on a map
- Adjustable speed: 1× / 2× / 5× / 10×
- Scrubber bar — drag to jump to any point in the route
- Live stats (time, distance, speed) update while scrubbing

### Goals
- Weekly targets for distance, time, and steps
- Progress bars with colour feedback (gray / primary / green at 100%)
- Day-dot row showing which days of the week you were active

### User Info
- Personal data (height, age) stored locally
- Weight history log with trend chart (kg or lbs)
- BMI history chart
- BMI gauge — semicircle dial with colour-coded zones (underweight / normal / overweight / obese)

### Settings

| Category | Options |
|---|---|
| **Units** | Distance: km / miles · Weight: kg / lbs · Gender: for calorie calculation |
| **Run** | GPS accuracy (high / battery saving) · Auto-pause · Keep screen on · Countdown |
| **Map** | Style: Standard / Cycle / Transport · Auto-center on position |
| **Voice** | Enable / frequency / language (device default, English, Greek, Spanish, German) |
| **Appearance** | Theme: system / light / dark · App language |

---

## Tech Stack

| Layer | Library / Tool |
|---|---|
| Language | Kotlin |
| Architecture | MVVM, Navigation Component, ViewBinding, Coroutines + Flow |
| Maps | OSMDroid 6.1.18 (OpenStreetMap) |
| GPS | Fused Location Provider (play-services-location 21.2.0) |
| Step Counter | `Sensor.TYPE_STEP_DETECTOR` via SensorManager |
| Database | Room 2.6.1 |
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

Room database, version 2. Three tables:

| Table | Description |
|---|---|
| `runs` | One row per run (distance, duration, calories, steps, avg speed, timestamp) |
| `location_points` | GPS points per run (lat, lng, altitude, speed, timestamp) — FK → runs CASCADE |
| `weight_entries` | Weight log entries with timestamps |

Migration 1→2 adds `stepCount INTEGER` to `runs` without data loss.

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
