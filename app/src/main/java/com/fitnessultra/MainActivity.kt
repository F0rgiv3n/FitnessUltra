package com.fitnessultra

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.fitnessultra.databinding.ActivityMainBinding
import com.fitnessultra.util.SettingsManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Top-level destinations — no back arrow, settings icon visible
    private val topLevelDestinations = setOf(
        R.id.runFragment, R.id.historyFragment, R.id.weightFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply saved theme before setContentView
        applyTheme(SettingsManager.theme(this))

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(topLevelDestinations)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavigationView.setupWithNavController(navController)

        // Hide bottom nav and settings icon on non-top-level screens
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isTopLevel = destination.id in topLevelDestinations
            binding.bottomNavigationView.visibility =
                if (isTopLevel) View.VISIBLE else View.GONE
            invalidateOptionsMenu()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        val currentDestId = navHostFragment.navController.currentDestination?.id
        menu.findItem(R.id.action_settings)?.isVisible =
            currentDestId in topLevelDestinations
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.navHostFragment) as NavHostFragment
            navHostFragment.navController.navigate(R.id.settingsFragment)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        return navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun applyTheme(value: String) {
        val mode = when (value) {
            "light" -> AppCompatDelegate.MODE_NIGHT_NO
            "dark"  -> AppCompatDelegate.MODE_NIGHT_YES
            else    -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}
