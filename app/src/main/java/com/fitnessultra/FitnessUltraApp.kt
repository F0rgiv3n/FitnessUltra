package com.fitnessultra

import android.app.Application
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration

class FitnessUltraApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val osmConfig = Configuration.getInstance()
        osmConfig.load(this, PreferenceManager.getDefaultSharedPreferences(this))
        osmConfig.userAgentValue = packageName
        osmConfig.osmdroidTileCache = getDir("osm_tiles", MODE_PRIVATE)
    }
}
