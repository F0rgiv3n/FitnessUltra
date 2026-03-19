package com.fitnessultra.ui.settings

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object DownloadedMapsManager {

    private const val PREFS = "downloaded_maps"
    private const val KEY_AREAS = "areas"

    fun getAll(context: Context): List<DownloadedMapArea> {
        val json = prefs(context).getString(KEY_AREAS, "[]") ?: "[]"
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { arr.getJSONObject(it).toArea() }
        } catch (_: Exception) { emptyList() }
    }

    fun save(context: Context, area: DownloadedMapArea) {
        val p = prefs(context)
        val arr = JSONArray(p.getString(KEY_AREAS, "[]") ?: "[]")
        arr.put(area.toJson())
        p.edit().putString(KEY_AREAS, arr.toString()).commit()
    }

    fun update(context: Context, area: DownloadedMapArea) {
        val p = prefs(context)
        val arr = JSONArray(p.getString(KEY_AREAS, "[]") ?: "[]")
        val newArr = JSONArray()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            if (obj.getString("id") == area.id) newArr.put(area.toJson()) else newArr.put(obj)
        }
        p.edit().putString(KEY_AREAS, newArr.toString()).commit()
    }

    fun delete(context: Context, id: String) {
        val p = prefs(context)
        val arr = JSONArray(p.getString(KEY_AREAS, "[]") ?: "[]")
        val newArr = JSONArray()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            if (obj.getString("id") != id) newArr.put(obj)
        }
        p.edit().putString(KEY_AREAS, newArr.toString()).commit()
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun DownloadedMapArea.toJson() = JSONObject().apply {
        put("id", id)
        put("description", description)
        put("latNorth", latNorth)
        put("latSouth", latSouth)
        put("lonWest", lonWest)
        put("lonEast", lonEast)
        put("downloadedAt", downloadedAt)
        put("tileCount", tileCount)
    }

    private fun JSONObject.toArea() = DownloadedMapArea(
        id = getString("id"),
        description = getString("description"),
        latNorth = getDouble("latNorth"),
        latSouth = getDouble("latSouth"),
        lonWest = getDouble("lonWest"),
        lonEast = getDouble("lonEast"),
        downloadedAt = getLong("downloadedAt"),
        tileCount = getInt("tileCount")
    )
}
