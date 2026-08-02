package pl.pzienowicz.zditmszczecinlive.dialog

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher

import pl.pzienowicz.zditmszczecinlive.Config
import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.databinding.DialogSettingsBinding
import pl.pzienowicz.zditmszczecinlive.prefs

class SettingsDialog(context: Context) : AdaptiveSheetDialog(context) {

    private var binding: DialogSettingsBinding

    init {
        binding = DialogSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.refreshWidgetsCheckbox.setOnCheckedChangeListener { _, isChecked ->
            context.prefs.refreshWidgets = isChecked
            val intent = Intent(Config.ACTION_AUTO_UPDATE)
            context.sendBroadcast(intent)
        }

        binding.refreshWidgetsCheckbox.isChecked = context.prefs.refreshWidgets

        binding.openLinksExternalBrowserCheckbox.setOnCheckedChangeListener { _, isChecked ->
            context.prefs.openLinksInExternalBrowser = isChecked
        }
        binding.openLinksExternalBrowserCheckbox.isChecked = context.prefs.openLinksInExternalBrowser

        binding.widgetsRefresh.setText(context.prefs.refreshWidgetsTime)
        binding.widgetsRefresh.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                context.prefs.refreshWidgetsTime = binding.widgetsRefresh.text.toString()
                val intent = Intent(Config.ACTION_AUTO_UPDATE)
                context.sendBroadcast(intent)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}
