package pl.pzienowicz.zditmszczecinlive.dialog

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import pl.pzienowicz.zditmszczecinlive.Config
import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.billing.GooglePlayBillingClient
import pl.pzienowicz.zditmszczecinlive.createPendingIntent
import pl.pzienowicz.zditmszczecinlive.data.BusStops
import pl.pzienowicz.zditmszczecinlive.data.Widget
import pl.pzienowicz.zditmszczecinlive.databinding.DialogWidgetsBinding
import pl.pzienowicz.zditmszczecinlive.prefs
import pl.pzienowicz.zditmszczecinlive.registerReceiver
import pl.pzienowicz.zditmszczecinlive.sendLocalBroadcast
import pl.pzienowicz.zditmszczecinlive.setFullWidth
import pl.pzienowicz.zditmszczecinlive.showBar
import pl.pzienowicz.zditmszczecinlive.widget.WidgetProvider
import java.util.ArrayList

class WidgetsDialog(
    private val activity: Activity,
    initialWidgetId: String? = null
) : AdaptiveSheetDialog(activity) {

    private var bcr: BroadcastReceiver? = null
    private val records = ArrayList<Widget>()
    private var widgetId: String? = initialWidgetId
    private var widgetBusStopNumber: String? = null
    private var pendingWidgetEditId: String? = initialWidgetId
    private var billingInitialized = false
    private var premiumUnlockedAfterPurchase = false
    private var isShown = false
    private val billingClient: GooglePlayBillingClient
    private val binding = DialogWidgetsBinding.inflate(layoutInflater)

    init {
        setContentView(binding.root)

        setOnShowListener {
            isShown = true
            openPendingWidgetEditIfReady()
        }

        billingClient = GooglePlayBillingClient(
            activity,
            onInitialized = {
                billingInitialized = true
                openPendingWidgetEditIfReady()
            },
            onPurchased = {
                premiumUnlockedAfterPurchase = true
                activity.showBar(R.string.payment_success)

                if (widgetId != null) {
                    openBusStopDialog(widgetId, widgetBusStopNumber)
                }
            }
        )

        binding.addWidgetButton.setOnClickListener {
            requestPinWidget()
        }

        reloadWidgets()

        bcr = activity.registerReceiver(
            listOf(
                Config.INTENT_REFRESH_WIDGETS_LIST,
                Config.INTENT_OPEN_BUS_STOP_EDIT,
                Config.INTENT_WIDGET_PINNED
            )
        ) { intent ->
            when (intent?.action) {
                Config.INTENT_OPEN_BUS_STOP_EDIT -> {
                    openBusStopDialog(
                        intent.extras?.getString(Config.EXTRA_WIDGET_ID),
                        intent.extras?.getString(Config.EXTRA_BUS_STOP_NUMBER)
                    )
                }
                Config.INTENT_REFRESH_WIDGETS_LIST -> {
                    reloadWidgets()
                }
                Config.INTENT_WIDGET_PINNED -> {
                    reloadWidgets()
                }
            }
        }
    }

    override fun dismiss() {
        bcr?.let {
            activity.unregisterReceiver(it)
            bcr = null
        }
        super.dismiss()
    }

    private fun openBusStopDialog(widgetId: String?, currentBusStop: String? = null) {
        if (!isPremiumUnlocked()) {
            this.widgetId = widgetId
            widgetBusStopNumber = currentBusStop
            val dialog = PremiumDialog(activity) {
                premiumUnlockedAfterPurchase = true
                openBusStopDialog(widgetId, currentBusStop)
            }
            dialog.setFullWidth()
            dialog.show()
            return
        }

        val am = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
            val dialog = AlarmDialog(activity)
            dialog.setFullWidth()
            dialog.show()
            return
        }

        val dialog = BusStopDialog(activity, { busStop ->
            activity.prefs.putString(Config.WIDGET_PREFIX + widgetId, busStop.number)

            val intent = Intent(Config.INTENT_REFRESH_WIDGETS_LIST)
            activity.sendLocalBroadcast(intent)

            val intent2 = Intent(activity, WidgetProvider::class.java)
            intent2.action = Config.ACTION_AUTO_UPDATE
            intent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            activity.sendBroadcast(intent2)

            Log.d(Config.LOG_TAG, "events sent!")
        }, currentBusStop)
        dialog.show()
    }

    private fun isPremiumUnlocked(): Boolean =
        premiumUnlockedAfterPurchase || billingClient.isPremiumUnlocked()

    private fun openPendingWidgetEditIfReady() {
        if (!isShown || !billingInitialized) {
            return
        }

        val pendingWidgetId = pendingWidgetEditId ?: return
        binding.root.post {
            if (pendingWidgetEditId != pendingWidgetId) {
                return@post
            }

            val position = records.indexOfFirst { it.widgetId == pendingWidgetId }
            val widget = records.getOrNull(position)

            pendingWidgetEditId = null

            openBusStopDialog(pendingWidgetId, widget?.busStop?.number)
        }
    }

    private fun reloadWidgets() {
        val appWidgetManager = AppWidgetManager.getInstance(activity)
        val thisAppWidget = ComponentName(activity.packageName, WidgetProvider::class.java.name)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)

        records.clear()
        renderWidgets()

        appWidgetIds.forEach { appWidgetId ->
            Log.d(Config.LOG_TAG, "widgetId: $appWidgetId")
            val busStopId = activity.prefs.getString(Config.WIDGET_PREFIX + appWidgetId)

            if (busStopId != null) {
                BusStops.getInstance(activity).loadByNumber(
                    busStopId,
                    onError = { showError(R.string.stops_request_error) },
                    callback = { busStop ->
                        records.add(Widget(appWidgetId.toString(), busStop))
                        renderWidgets()
                        openPendingWidgetEditIfReady()
                    }
                )
            } else {
                records.add(Widget(appWidgetId.toString(), null))
                renderWidgets()
                openPendingWidgetEditIfReady()
            }
        }
    }

    private fun renderWidgets() {
        binding.widgetsContainer.removeAllViews()
        binding.emptyWidgetsText.visibility = if (records.isEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }

        records.forEach { widget ->
            binding.widgetsContainer.addView(createWidgetCard(widget))
        }
    }

    private fun requestPinWidget() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            activity.showBar(R.string.pin_widget_not_supported)
            return
        }

        val appWidgetManager = AppWidgetManager.getInstance(activity)
        if (!appWidgetManager.isRequestPinAppWidgetSupported) {
            activity.showBar(R.string.pin_widget_not_supported)
            return
        }

        val provider = ComponentName(activity, WidgetProvider::class.java)
        val successCallback = activity.createPendingIntent(
            PIN_WIDGET_REQUEST_CODE,
            Intent(Config.INTENT_WIDGET_PINNED).setPackage(activity.packageName),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        appWidgetManager.requestPinAppWidget(provider, null, successCallback)
    }

    private fun createWidgetCard(widget: Widget): View {
        val row = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            isClickable = true
            isFocusable = true
            setBackgroundResource(R.drawable.bg_snackbar)
            setPadding(dp(12), dp(10), dp(8), dp(10))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(8)
            }
            setOnClickListener {
                openBusStopDialog(widget.widgetId, widget.busStop?.number)
            }
        }

        val textContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }
        textContainer.addView(
            createTitleText(
                widget.busStop?.name
                    ?: context.getString(R.string.widget_without_stop)
            )
        )
        textContainer.addView(
            createSubtitleText(
                widget.busStop?.number
                    ?: context.getString(R.string.tap_to_configure_widget)
            )
        )

        val editButton = ImageButton(context).apply {
            setImageResource(R.drawable.ic_edit_24dp)
            background = null
            setBackgroundColor(Color.TRANSPARENT)
            contentDescription = context.getString(R.string.edit)
            setPadding(dp(8), dp(8), dp(8), dp(8))
            layoutParams = LinearLayout.LayoutParams(dp(48), dp(48))
            setOnClickListener {
                openBusStopDialog(widget.widgetId, widget.busStop?.number)
            }
        }

        row.addView(textContainer)
        row.addView(editButton)
        return row
    }

    private fun createTitleText(text: String): TextView =
        TextView(context).apply {
            this.text = text
            setTextColor(ContextCompat.getColor(context, R.color.app_text))
            setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 17f)
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }

    private fun createSubtitleText(text: String): TextView =
        TextView(context).apply {
            this.text = text
            setTextColor(ContextCompat.getColor(context, R.color.app_text_secondary))
            setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 13f)
        }

    private fun dp(value: Int): Int =
        (value * context.resources.displayMetrics.density).toInt()

    private companion object {
        const val PIN_WIDGET_REQUEST_CODE = 7441
    }

    private fun showError(message: Int) {
        binding.errorText.setText(message)
        binding.errorText.visibility = View.VISIBLE
    }
}
