# FitnessUltra

A personal Android fitness tracking app built with Kotlin. Track outdoor runs with GPS, monitor your weight and BMI, replay past routes, and review detailed per-run stats and charts — all without any cloud dependency or API keys.

---

## Screenshots

> *(coming soon)*

---

## Features

### Running
- Real-time GPS tracking with live map (OpenStreetMap, no API key)
- Distance, speed, pace, elapsed time displayed during run
- Step counter via device accelerometer
- Voice announcements at configurable distance milestones (1 / 2 / 5 km)
- Calories burned estimation
- Foreground service keeps tracking alive in the background

### History
- Full run log with swipe-to-delete and undo
- Steps bar chart per day / week / month
- Tap any run → detailed charts (speed, elevation, pace)

### Run Replay
- Animated playback of your GPS route on a map
- Adjustable speed: 1× / 2× / 5× / 10×
- Live stats (time, distance, speed) during playback

### User Info
- Personal data (height, age) stored locally
- Weight history log with trend chart
- BMI history chart
- BMI gauge — semicircle dial with colour-coded zones (underweight / normal / overweight / obese)

### Settings
- **Language:** English, Ελληνικά, Español, Deutsch
- **Theme:** System default / Light / Dark
- **Distance unit:** km / miles
- **Weight unit:** kg / lbs
- **Voice feedback:** on/off, frequency

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
```

---

## Roadmap

- [ ] Goals screen (weekly distance / time targets with progress bar)
- [ ] Wire km/miles unit toggle to all display screens
- [ ] Replay scrubber (progress bar to jump to any point)
- [ ] Supabase integration (optional cloud sync, leaderboards)
