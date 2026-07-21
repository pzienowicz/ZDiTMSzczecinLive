package pl.pzienowicz.zditmszczecinlive.dialog

import android.app.Activity
import android.app.AlarmManager
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.R as MaterialR
import com.google.android.material.bottomsheet.BottomSheetDialog
import pl.pzienowicz.zditmszczecinlive.Config
import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.adapter.WidgetTableDataAdapter
import pl.pzienowicz.zditmszczecinlive.billing.GooglePlayBillingClient
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
) : BottomSheetDialog(activity) {

    private val adapter: WidgetTableDataAdapter
    private var bcr: BroadcastReceiver? = null
    private val records = ArrayList<Widget>()
    private var widgetId: String? = initialWidgetId
    private var widgetBusStopNumber: String? = null
    private var pendingWidgetEditId: String? = initialWidgetId
    private var billingInitialized = false
    private var isShown = false
    private val billingClient: GooglePlayBillingClient
    private val binding = DialogWidgetsBinding.inflate(layoutInflater)

    init {
        setContentView(binding.root)

        setOnShowListener {
            isShown = true
            findViewById<View>(MaterialR.id.design_bottom_sheet)
                ?.setBackgroundResource(R.drawable.bg_bottom_sheet)
            openPendingWidgetEditIfReady()
        }

        adapter = WidgetTableDataAdapter(activity, records)
        billingClient = GooglePlayBillingClient(
            activity,
            onInitialized = {
                billingInitialized = true
                openPendingWidgetEditIfReady()
            },
            onPurchased = {
                activity.showBar(R.string.payment_success)

                if (widgetId != null) {
                    openBusStopDialog(widgetId, widgetBusStopNumber)
                }
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.adapter = adapter

        reloadWidgets()

        bcr = activity.registerReceiver(
            listOf(
                Config.INTENT_REFRESH_WIDGETS_LIST,
                Config.INTENT_OPEN_BUS_STOP_EDIT
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
        if (!billingClient.areWidgetsUnlocked()) {
            this.widgetId = widgetId
            widgetBusStopNumber = currentBusStop
            billingClient.unlockWidgets()
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

            if (position != -1) {
                binding.recyclerView.scrollToPosition(position)
            }

            openBusStopDialog(pendingWidgetId, widget?.busStop?.number)
        }
    }

    private fun reloadWidgets() {
        val appWidgetManager = AppWidgetManager.getInstance(activity)
        val thisAppWidget = ComponentName(activity.packageName, WidgetProvider::class.java.name)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)

        records.clear()

        appWidgetIds.forEach { appWidgetId ->
            Log.d(Config.LOG_TAG, "widgetId: $appWidgetId")
            val busStopId = activity.prefs.getString(Config.WIDGET_PREFIX + appWidgetId)

            if (busStopId != null) {
                BusStops.getInstance(activity).loadByNumber(busStopId, callback = { busStop ->
                    records.add(Widget(appWidgetId.toString(), busStop))
                    adapter.notifyDataSetChanged()
                    openPendingWidgetEditIfReady()
                })
            } else {
                records.add(Widget(appWidgetId.toString(), null))
                adapter.notifyDataSetChanged()
                openPendingWidgetEditIfReady()
            }
        }
    }
}
