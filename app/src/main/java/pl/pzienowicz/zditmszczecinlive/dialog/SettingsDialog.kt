package pl.pzienowicz.zditmszczecinlive.dialog

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatDelegate

import pl.pzienowicz.zditmszczecinlive.Config
import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.billing.GooglePlayBillingClient
import pl.pzienowicz.zditmszczecinlive.databinding.DialogSettingsBinding
import pl.pzienowicz.zditmszczecinlive.prefs
import pl.pzienowicz.zditmszczecinlive.sendLocalBroadcast
import pl.pzienowicz.zditmszczecinlive.setFullWidth
import pl.pzienowicz.zditmszczecinlive.showBar
import pl.pzienowicz.zditmszczecinlive.widget.WidgetProvider

class SettingsDialog(context: Context) : AdaptiveSheetDialog(context) {

    private var binding: DialogSettingsBinding
    private var premiumUnlockedAfterPurchase = false
    private val billingClient = GooglePlayBillingClient(
        activity = context as android.app.Activity,
        onInitialized = {},
        onPurchased = {
            premiumUnlockedAfterPurchase = true
            context.showBar(R.string.payment_success)
            enableDarkMode(context)
        }
    )

    init {
        binding = DialogSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.refreshWidgetsCheckbox.setOnCheckedChangeListener { _, isChecked ->
            context.prefs.refreshWidgets = isChecked
            context.sendWidgetUpdateBroadcast()
        }

        binding.refreshWidgetsCheckbox.isChecked = context.prefs.refreshWidgets

        binding.openLinksExternalBrowserCheckbox.setOnCheckedChangeListener { _, isChecked ->
            context.prefs.openLinksInExternalBrowser = isChecked
        }
        binding.openLinksExternalBrowserCheckbox.isChecked = context.prefs.openLinksInExternalBrowser

        binding.darkModeCheckbox.isChecked = context.prefs.darkMode
        binding.darkModeCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && !isPremiumUnlocked()) {
                binding.darkModeCheckbox.isChecked = false
                showPremiumDialog(context)
                return@setOnCheckedChangeListener
            }

            setDarkMode(context, isChecked)
        }

        binding.widgetsRefresh.setText(context.prefs.refreshWidgetsTime)
        binding.widgetsRefresh.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                context.prefs.refreshWidgetsTime = binding.widgetsRefresh.text.toString()
                context.sendWidgetUpdateBroadcast()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun isPremiumUnlocked(): Boolean =
        premiumUnlockedAfterPurchase || billingClient.isPremiumUnlocked()

    private fun showPremiumDialog(context: Context) {
        val dialog = PremiumDialog(context as android.app.Activity) {
            premiumUnlockedAfterPurchase = true
            enableDarkMode(context)
        }
        dialog.setFullWidth()
        dialog.show()
    }

    private fun enableDarkMode(context: Context) {
        binding.darkModeCheckbox.isChecked = true
        setDarkMode(context, true)
    }

    private fun setDarkMode(context: Context, enabled: Boolean) {
        context.prefs.darkMode = enabled
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        context.sendLocalBroadcast(Intent(Config.INTENT_REFRESH_SETTINGS))
    }

    private fun Context.sendWidgetUpdateBroadcast() {
        val intent = Intent(this, WidgetProvider::class.java)
        intent.action = Config.ACTION_AUTO_UPDATE
        sendBroadcast(intent)
    }
}
