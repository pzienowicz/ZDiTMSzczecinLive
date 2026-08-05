package pl.pzienowicz.zditmszczecinlive.dialog

import android.app.Activity
import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.billing.GooglePlayBillingClient
import pl.pzienowicz.zditmszczecinlive.databinding.DialogPremiumBinding
import pl.pzienowicz.zditmszczecinlive.showBar

class PremiumDialog(
    private val activity: Activity,
    private val onPremiumUnlocked: () -> Unit = {}
) : AdaptiveSheetDialog(activity) {

    private val binding = DialogPremiumBinding.inflate(layoutInflater)
    private val billingClient = GooglePlayBillingClient(
        activity = activity,
        onInitialized = {
            activity.runOnUiThread {
                updatePrice()
            }
        },
        onPurchased = {
            activity.runOnUiThread {
                activity.showBar(R.string.payment_success)
                onPremiumUnlocked()
                dismiss()
            }
        }
    )

    init {
        setContentView(binding.root)
        updatePrice()

        binding.buyPremiumButton.setOnClickListener {
            billingClient.unlockPremium()
        }
    }

    private fun updatePrice() {
        val price = billingClient.premiumPrice()
        if (price == null) {
            binding.premiumPriceText.setText(R.string.premium_price_loading)
            binding.buyPremiumButton.setText(R.string.buy_premium)
            return
        }

        binding.premiumPriceText.text = price
        binding.buyPremiumButton.text = context.getString(R.string.buy_premium_with_price, price)
    }
}
