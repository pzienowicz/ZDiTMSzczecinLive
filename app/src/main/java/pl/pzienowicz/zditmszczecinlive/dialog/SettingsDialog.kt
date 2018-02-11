package pl.pzienowicz.zditmszczecinlive.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import android.widget.CheckBox
import android.widget.EditText

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

        val refreshWidgetsCheckbox = findViewById(R.id.refreshWidgetsCheckbox) as CheckBox
        refreshWidgetsCheckbox.setOnCheckedChangeListener({ checkbox, isChecked ->
            preferences.edit().putBoolean(Config.PREFERENCE_WIDGETS_REFRESH, isChecked).apply()
            val intent = Intent(Config.ACTION_AUTO_UPDATE)
            context.sendBroadcast(intent)
        })

        refreshWidgetsCheckbox.isChecked = preferences.getBoolean(Config.PREFERENCE_WIDGETS_REFRESH, true)

        val widgetsRefreshText = findViewById(R.id.widgetsRefresh) as EditText
        widgetsRefreshText.setText(preferences.getString(Config.PREFERENCE_WIDGETS_REFRESH_TIME, "30"))
        widgetsRefreshText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                preferences.edit().putString(Config.PREFERENCE_WIDGETS_REFRESH_TIME, widgetsRefreshText.text.toString()).apply()
                val intent = Intent(Config.ACTION_AUTO_UPDATE)
                context.sendBroadcast(intent)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }
}
