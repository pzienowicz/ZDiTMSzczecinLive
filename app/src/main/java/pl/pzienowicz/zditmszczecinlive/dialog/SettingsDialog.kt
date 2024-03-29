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

        binding.refreshWidgetsCheckbox.setOnCheckedChangeListener { _, isChecked ->
            context.prefs.refreshWidgets = isChecked
            val intent = Intent(Config.ACTION_AUTO_UPDATE)
            context.sendBroadcast(intent)
        }

        binding.refreshWidgetsCheckbox.isChecked = context.prefs.refreshWidgets

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
