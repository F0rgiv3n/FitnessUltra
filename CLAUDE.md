# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Status

This is a **pre-development** Android fitness tracking app. Only `idea.txt` (the requirements spec, written in Greek) exists. No code has been written yet.

## Planned Tech Stack

- **Language:** Kotlin
- **IDE:** Android Studio
- **Maps:** OSMDroid (OpenStreetMap — no API key, no billing)
- **GPS:** Fused Location Provider API
- **Local DB:** Room Database (storing runs)
- **Charts:** MPAndroidChart
- **Voice:** Android TextToSpeech
- **Backend (future):** Supabase (user accounts, challenges, leaderboards)

## Planned Architecture

The app is a running/workout tracker with these core modules:

### Tracking Engine
- Fused Location Provider polls GPS → stores `lat`, `lng`, `timestamp`, `speed` per point
- Speed: `location.getSpeed() * 3.6` (m/s → km/h)
- Distance: accumulate `lastLocation.distanceTo(newLocation)`
- Pace: `minutes / km` (e.g. 5:20/km)
- Calories: `Distance(km) × Weight(kg) × 1.036`
- Elevation: gain/loss from GPS altitude data
- Controls: Start / Pause / Resume / Stop

### Data Layer (Room)
- Persist each workout: date, km, duration, avg speed, calories, GPS point list
- Weight entries: value + date (for timeline chart)

### UI / Screens
- **Live run screen:** Map with polyline route, real-time stats overlay
- **Workout history:** List of past runs with summary stats
- **Run replay:** View past route on map with its stats
- **Goals:** User sets distance (5km, 10km) or time (30min) target with progress bar
- **Charts screen:** Speed vs time, elevation, pace (MPAndroidChart)
- **Weight tracker:** Entry form + timeline chart (red bar = weight gain, green = weight loss), BMI display

### Feedback
- `Android TextToSpeech` announces every 1km: *"1 kilometer completed – pace 5:30"*

### Future: Supabase Integration
- User authentication
- Sync run data per user
- Challenges and leaderboards
