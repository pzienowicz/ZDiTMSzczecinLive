package pl.pzienowicz.zditmszczecinlive.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.Window

import pl.pzienowicz.zditmszczecinlive.Config
import pl.pzienowicz.zditmszczecinlive.databinding.DialogSettingsBinding
import pl.pzienowicz.zditmszczecinlive.prefs

class SettingsDialog(context: Context) : Dialog(context) {

    private var binding: DialogSettingsBinding

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.locationCheckbox.setOnCheckedChangeListener { _, isChecked ->
            context.prefs.edit().putBoolean(Config.PREFERENCE_USE_LOCATION, isChecked).apply()
            val intent = Intent(Config.INTENT_REFRESH_SETTINGS)
            context.sendBroadcast(intent)
        }

        binding.locationCheckbox.isChecked = context.prefs.getBoolean(Config.PREFERENCE_USE_LOCATION, true)

        binding.zoomMapCheckbox.setOnCheckedChangeListener { _, isChecked ->
            context.prefs.edit().putBoolean(Config.PREFERENCE_ZOOM_MAP, isChecked).apply()
            val intent = Intent(Config.INTENT_REFRESH_SETTINGS)
            context.sendBroadcast(intent)
        }

        binding.zoomMapCheckbox.isChecked = context.prefs.getBoolean(Config.PREFERENCE_ZOOM_MAP, true)

        binding.refreshWidgetsCheckbox.setOnCheckedChangeListener { _, isChecked ->
            context.prefs.edit().putBoolean(Config.PREFERENCE_WIDGETS_REFRESH, isChecked).apply()
            val intent = Intent(Config.ACTION_AUTO_UPDATE)
            context.sendBroadcast(intent)
        }

        binding.refreshWidgetsCheckbox.isChecked = context.prefs.getBoolean(Config.PREFERENCE_WIDGETS_REFRESH, true)

        binding.widgetsRefresh.setText(context.prefs.getString(Config.PREFERENCE_WIDGETS_REFRESH_TIME, "30"))
        binding.widgetsRefresh.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                context.prefs.edit().putString(
                    Config.PREFERENCE_WIDGETS_REFRESH_TIME,
                    binding.widgetsRefresh.text.toString()
                ).apply()
                val intent = Intent(Config.ACTION_AUTO_UPDATE)
                context.sendBroadcast(intent)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}
