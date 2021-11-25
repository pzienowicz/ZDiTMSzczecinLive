package pl.pzienowicz.zditmszczecinlive.billing

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.*
import pl.pzienowicz.zditmszczecinlive.Config
import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.showBar

class GooglePlayBillingClient(
    private val activity: Activity,
    val onInitialized: () -> Unit,
    val onPurchased: () -> Unit
) {

    private var isConnected = false
    private var areWidgetsUnlocked = false
    var skuMap: HashMap<String, SkuDetails> = HashMap()

    private var billingClient: BillingClient = BillingClient
        .newBuilder(activity)
        .setListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        if (!purchase.isAcknowledged) {
                            ackPurchase(purchase)
                        }
                    }
                }
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                activity.showBar(R.string.payment_cancel)
            } else {
                activity.showBar(R.string.payment_error)
            }
        }
        .enablePendingPurchases()
        .build()

    init {
        Log.d(Config.LOG_TAG, "GooglePlayBillingClient start connecting...")
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    isConnected = true
                    loadDetails()

                    Log.d(Config.LOG_TAG, "GooglePlayBillingClient connected!")
                }
            }
            override fun onBillingServiceDisconnected() {
                Log.e(Config.LOG_TAG, "GooglePlayBillingClient disconnected!")
            }
        })
    }

    private fun ackPurchase(purchase: Purchase) {
        val acknowledgePurchaseParams =
            AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

        billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
            val billingResponseCode = billingResult.responseCode
            val billingDebugMessage = billingResult.debugMessage

            Log.v(Config.LOG_TAG, "response code: $billingResponseCode")
            Log.v(Config.LOG_TAG, "debugMessage : $billingDebugMessage")
        }

        if(purchase.skus.first().equals(Config.PRODUCT_WIDGETS_UNLOCK)) {
            areWidgetsUnlocked = true
            onPurchased()
        }
    }

    fun unlockWidgets() {
        skuMap[Config.PRODUCT_WIDGETS_UNLOCK]?.let {
            val flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(it)
                .build()

            billingClient.launchBillingFlow(activity, flowParams)
        }
    }

    fun areWidgetsUnlocked()= areWidgetsUnlocked

    fun loadDetails() {
        val skuList = listOf(
            Config.PRODUCT_WIDGETS_UNLOCK
        )

        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)

        billingClient.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                for (skuDetails in skuDetailsList) {
                    skuMap[skuDetails.sku] = skuDetails
                }
            }

            billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP) { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    purchases.forEach {
                        if (it.purchaseState == Purchase.PurchaseState.PURCHASED
                            && it.skus.first().equals(Config.PRODUCT_WIDGETS_UNLOCK)
                        ) {
                            Log.d(Config.LOG_TAG, "areWidgetsUnlocked")
                            areWidgetsUnlocked = true
                        }
                    }
                }

                onInitialized()
            }
        }
    }
}