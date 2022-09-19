package pl.pzienowicz.zditmszczecinlive.activity

import android.appwidget.AppWidgetManager
import android.content.*
import android.graphics.Color
import android.os.Bundle
import de.codecrafters.tableview.TableView
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter
import pl.pzienowicz.zditmszczecinlive.adapter.WidgetTableDataAdapter
import pl.pzienowicz.zditmszczecinlive.data.BusStops
import pl.pzienowicz.zditmszczecinlive.data.Widget
import pl.pzienowicz.zditmszczecinlive.model.BusStop
import pl.pzienowicz.zditmszczecinlive.widget.WidgetProvider
import java.util.ArrayList
import de.codecrafters.tableview.model.TableColumnWeightModel
import pl.pzienowicz.zditmszczecinlive.dialog.BusStopDialog
import android.content.Intent
import android.util.Log
import android.content.ComponentName
import androidx.appcompat.app.AppCompatActivity
import pl.pzienowicz.zditmszczecinlive.*
import pl.pzienowicz.zditmszczecinlive.billing.GooglePlayBillingClient
import pl.pzienowicz.zditmszczecinlive.databinding.ActivityWidgetsBinding

class WidgetsActivity : AppCompatActivity() {

    private lateinit var adapter: WidgetTableDataAdapter
    private lateinit var bcr: BroadcastReceiver
    private val records = ArrayList<Widget>()
    private var widgetId: String? = null
    private lateinit var billingClient: GooglePlayBillingClient

    private lateinit var binding: ActivityWidgetsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWidgetsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        widgetId = intent.getStringExtra(Config.EXTRA_WIDGET_ID)

        adapter = WidgetTableDataAdapter(this, records)
        billingClient = GooglePlayBillingClient(this,
            onInitialized = {
                if(widgetId != null) {
                    openBusStopDialog(widgetId)
                }
            },
            onPurchased = {
                showBar(R.string.payment_success)

                if(widgetId != null) {
                    openBusStopDialog(widgetId)
                }
            }
        )

        val tableView = binding.tableView as TableView<*>

        val columnModel = TableColumnWeightModel(4)
            .apply {
                setColumnWeight(0, 1)
                setColumnWeight(1, 2)
                setColumnWeight(2, 5)
                setColumnWeight(3, 1)
            }

        val headerAdapter = SimpleTableHeaderAdapter(
            this,
            *resources.getStringArray(R.array.headers)
        )
        headerAdapter.setTextColor(Color.parseColor("#FFFFFF"))

        tableView.columnModel = columnModel
        tableView.headerAdapter = headerAdapter
        tableView.dataAdapter = adapter

        reloadWidgets()

        bcr = registerReceiver(listOf(
            Config.INTENT_REFRESH_WIDGETS_LIST,
            Config.INTENT_OPEN_BUSSTOP_EDIT
        )) { intent ->
            when (intent?.action) {
                Config.INTENT_OPEN_BUSSTOP_EDIT -> {
                    openBusStopDialog(intent.extras?.getString("widgetId"))
                }
                Config.INTENT_REFRESH_WIDGETS_LIST -> {
                    reloadWidgets()
                }
            }
        }
    }

    private fun openBusStopDialog(widgetId: String?) {

        if(!billingClient.areWidgetsUnlocked()) {
            this.widgetId = widgetId
            billingClient.unlockWidgets()
            return
        }

        val dialog = BusStopDialog(this@WidgetsActivity, { busStop ->

            prefs.putString(Config.WIDGET_PREFIX + widgetId, busStop.numer)

            val intent = Intent(Config.INTENT_REFRESH_WIDGETS_LIST)
            sendBroadcast(intent)

            val intent2 = Intent(this, WidgetProvider::class.java)
            intent2.action = Config.ACTION_AUTO_UPDATE
            intent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            sendBroadcast(intent2)

            Log.d(Config.LOG_TAG, "events sent!")
        }, null)
        dialog.show()
    }

    override fun onDestroy() {
        unregisterReceiver(bcr)
        super.onDestroy()
    }

    private fun reloadWidgets() {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val thisAppWidget = ComponentName(packageName, WidgetProvider::class.java.name)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)

        records.clear()

        appWidgetIds.forEach { appWidgetId ->
            prefs.getString(Config.WIDGET_PREFIX + appWidgetId)
                ?.let {
                    BusStops.getInstance(this).loadByNumber(it, callback = { busStop ->
                        records.add(Widget(appWidgetId.toString(), busStop))
                    })
                }
        }

        adapter.notifyDataSetChanged()
    }
}
