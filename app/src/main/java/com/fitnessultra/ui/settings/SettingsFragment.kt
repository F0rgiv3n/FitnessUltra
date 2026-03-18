package com.fitnessultra.ui.settings

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.fitnessultra.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        // Show app version
        findPreference<Preference>("pref_version")?.summary = try {
            requireContext().packageManager
                .getPackageInfo(requireContext().packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            "1.0"
        }

        // Apply theme immediately when changed
        findPreference<ListPreference>("pref_theme")?.setOnPreferenceChangeListener { _, newValue ->
            applyTheme(newValue as String)
            true
        }
    }

    private fun applyTheme(value: String) {
        val mode = when (value) {
            "light"  -> AppCompatDelegate.MODE_NIGHT_NO
            "dark"   -> AppCompatDelegate.MODE_NIGHT_YES
            else     -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}
