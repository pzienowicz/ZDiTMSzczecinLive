package pl.pzienowicz.zditmszczecinlive.dialog

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.google.android.material.R as MaterialR
import com.google.android.material.bottomsheet.BottomSheetDialog

import pl.pzienowicz.zditmszczecinlive.Config
import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.databinding.DialogSettingsBinding
import pl.pzienowicz.zditmszczecinlive.prefs

class SettingsDialog(context: Context) : BottomSheetDialog(context) {

    private var binding: DialogSettingsBinding

    init {
        binding = DialogSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setOnShowListener {
            findViewById<View>(MaterialR.id.design_bottom_sheet)
                ?.setBackgroundResource(R.drawable.bg_bottom_sheet)
        }

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
