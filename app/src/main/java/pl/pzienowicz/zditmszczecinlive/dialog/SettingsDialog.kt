package pl.pzienowicz.zditmszczecinlive.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.view.Window
import android.widget.CheckBox

import pl.pzienowicz.zditmszczecinlive.Config
import pl.pzienowicz.zditmszczecinlive.R

class SettingsDialog(context: Context) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_settings)

        val preferences = PreferenceManager.getDefaultSharedPreferences(context)

        val locationCheckbox = findViewById(R.id.locationCheckbox) as CheckBox
        locationCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            preferences.edit().putBoolean(Config.PREFERENCE_USE_LOCATION, isChecked).apply()
            val intent = Intent(Config.INTENT_REFRESH_SETTINGS)
            context.sendBroadcast(intent)
        }

        locationCheckbox.isChecked = preferences.getBoolean(Config.PREFERENCE_USE_LOCATION, true)

        val zoomMapCheckbox = findViewById(R.id.zoomMapCheckbox) as CheckBox
        zoomMapCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            preferences.edit().putBoolean(Config.PREFERENCE_ZOOM_MAP, isChecked).apply()
            val intent = Intent(Config.INTENT_REFRESH_SETTINGS)
            context.sendBroadcast(intent)
        }

        zoomMapCheckbox.isChecked = preferences.getBoolean(Config.PREFERENCE_ZOOM_MAP, true)
    }
}
