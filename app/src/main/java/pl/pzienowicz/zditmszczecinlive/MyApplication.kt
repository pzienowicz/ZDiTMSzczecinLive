package pl.pzienowicz.zditmszczecinlive

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        applyDarkModePreference()
    }

    private fun applyDarkModePreference() {
        AppCompatDelegate.setDefaultNightMode(
            if (prefs.darkMode) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
