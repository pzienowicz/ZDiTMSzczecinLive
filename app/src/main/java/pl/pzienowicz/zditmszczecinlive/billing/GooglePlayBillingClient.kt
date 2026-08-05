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
    private var isPremiumUnlocked = false
    private var productDetailsMap: HashMap<String, ProductDetails> = HashMap()

    private var billingClient: BillingClient = BillingClient
        .newBuilder(activity)
        .setListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        if (!purchase.isAcknowledged) {
                            ackPurchase(purchase)
                        }
                        handlePremiumPurchase(purchase)
                    }
                }
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                activity.showBar(R.string.payment_cancel)
            } else {
                activity.showBar(R.string.payment_error)
            }
        }
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
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

    }

    private fun handlePremiumPurchase(purchase: Purchase) {
        if (purchase.isPremiumPurchase()) {
            isPremiumUnlocked = true
            onPurchased()
        }
    }

    fun unlockPremium() {
        unlockProduct(Config.PRODUCT_PREMIUM_UNLOCK)
    }

    fun premiumPrice(): String? =
        productDetailsMap[Config.PRODUCT_PREMIUM_UNLOCK]
            ?.oneTimePurchaseOfferDetails
            ?.formattedPrice

    private fun unlockProduct(productId: String) {
        productDetailsMap[productId]?.let { productDetails ->
            val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()

            val flowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(listOf(productDetailsParams))
                .build()

            billingClient.launchBillingFlow(activity, flowParams)
        } ?: activity.showBar(R.string.payment_error)
    }

    fun isPremiumUnlocked() = isPremiumUnlocked

    fun consumePremiumProducts() {
        val purchasesParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        billingClient.queryPurchasesAsync(purchasesParams) { purchaseResult, purchases ->
            if (purchaseResult.responseCode != BillingClient.BillingResponseCode.OK) {
                activity.showBar(R.string.payment_error)
                return@queryPurchasesAsync
            }

            val purchasesToConsume = purchases.filter { purchase ->
                purchase.products.any { it in PREMIUM_PRODUCT_IDS }
            }
            if (purchasesToConsume.isEmpty()) {
                activity.showBar(R.string.debug_no_premium_products_to_consume)
                return@queryPurchasesAsync
            }

            purchasesToConsume.forEach { purchase ->
                val consumeParams = ConsumeParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                billingClient.consumeAsync(consumeParams) { billingResult, _ ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        isPremiumUnlocked = false
                        activity.showBar(R.string.debug_premium_products_consumed)
                    } else {
                        activity.showBar(R.string.payment_error)
                    }
                }
            }
        }
    }

    private fun inAppProduct(productId: String): QueryProductDetailsParams.Product =
        QueryProductDetailsParams.Product.newBuilder()
            .setProductId(productId)
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

    private fun Purchase.isPremiumPurchase(): Boolean =
        purchaseState == Purchase.PurchaseState.PURCHASED &&
            products.any { it in PREMIUM_PRODUCT_IDS }

    private companion object {
        val PREMIUM_PRODUCT_IDS = setOf(
            Config.PRODUCT_PREMIUM_UNLOCK,
            Config.PRODUCT_WIDGETS_UNLOCK
        )
    }

    fun loadDetails() {
        val productList = listOf(
            inAppProduct(Config.PRODUCT_PREMIUM_UNLOCK)
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                for (productDetails in productDetailsResult.productDetailsList) {
                    productDetailsMap[productDetails.productId] = productDetails
                }
            }

            val purchasesParams = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()

            billingClient.queryPurchasesAsync(purchasesParams) { purchaseResult, purchases ->
                if (purchaseResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    purchases.forEach {
                        if (it.isPremiumPurchase()) {
                            Log.d(Config.LOG_TAG, "isPremiumUnlocked")
                            isPremiumUnlocked = true
                        }
                    }
                }

                onInitialized()
            }
        }
    }
}
