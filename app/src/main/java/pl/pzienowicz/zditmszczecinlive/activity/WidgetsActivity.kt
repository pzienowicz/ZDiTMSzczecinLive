package pl.pzienowicz.zditmszczecinlive.activity

import android.appwidget.AppWidgetManager
import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import de.codecrafters.tableview.TableView
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter
import pl.pzienowicz.zditmszczecinlive.Config
import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.adapter.WidgetTableDataAdapter
import pl.pzienowicz.zditmszczecinlive.data.BusStops
import pl.pzienowicz.zditmszczecinlive.data.Widget
import pl.pzienowicz.zditmszczecinlive.model.BusStop
import pl.pzienowicz.zditmszczecinlive.widget.WidgetProvider
import java.util.ArrayList
import de.codecrafters.tableview.model.TableColumnWeightModel
import pl.pzienowicz.zditmszczecinlive.dialog.BusStopDialog
import pl.pzienowicz.zditmszczecinlive.listener.BusStopSelectedListener
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import android.content.Intent
import android.util.Log
import com.anjlab.android.iab.v3.Constants
import pl.pzienowicz.zditmszczecinlive.BuildConfig
import android.content.ComponentName
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class WidgetsActivity : AppCompatActivity(), BillingProcessor.IBillingHandler {

    private var adapter: WidgetTableDataAdapter? = null
    private val records = ArrayList<Widget>()
    private var bcr: BroadcastReceiver? = null
    private var bp: BillingProcessor? = null
    private var widgetId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widgets)

        adapter = WidgetTableDataAdapter(this, records)
        bp = BillingProcessor(this, Config.LICENSEE_KEY, this)

        val tableView = findViewById<TableView<*>>(R.id.tableView)

        val columnModel = TableColumnWeightModel(4)
        columnModel.setColumnWeight(0, 1)
        columnModel.setColumnWeight(1, 2)
        columnModel.setColumnWeight(2, 5)
        columnModel.setColumnWeight(3, 1)

        val headerAdapter = SimpleTableHeaderAdapter(this, *resources.getStringArray(R.array.headers))
        headerAdapter.setTextColor(Color.parseColor("#FFFFFF"))

        tableView.columnModel = columnModel
        tableView.headerAdapter = headerAdapter
        tableView.dataAdapter = adapter

        reloadWidgets()

        val intentFilter = IntentFilter()
        intentFilter.addAction(Config.INTENT_REFRESH_WIDGETS_LIST)
        intentFilter.addAction(Config.INTENT_OPEN_BUSSTOP_EDIT)
        bcr = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when(intent!!.action) {
                    Config.INTENT_OPEN_BUSSTOP_EDIT -> {
                        openBusStopDialog(intent.extras?.getString("widgetId"))
                    }
                    Config.INTENT_REFRESH_WIDGETS_LIST -> {
                        reloadWidgets()
                    }
                }
            }
        }
        registerReceiver(bcr, intentFilter)

        widgetId = intent.getStringExtra(Config.EXTRA_WIDGET_ID)
    }

    private fun openBusStopDialog(widgetId: String?) {

        if(!BuildConfig.DEBUG && !bp!!.isPurchased(Config.PRODUCT_WIDGETS_UNLOCK)) {
            this.widgetId = widgetId
            bp!!.purchase(this, Config.PRODUCT_WIDGETS_UNLOCK)
            return
        }

//        bp!!.consumePurchase(Config.PRODUCT_WIDGETS_UNLOCK)
//        return

        val dialog = BusStopDialog(this@WidgetsActivity, BusStopSelectedListener { busStop ->

            val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            preferences.edit().putString(Config.WIDGET_PREFIX + widgetId, busStop.numer).apply()

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!bp!!.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onDestroy() {
        if(bcr != null) {
            unregisterReceiver(bcr)
        }

        if (bp != null) {
            bp!!.release()
        }

        super.onDestroy()
    }

    private fun reloadWidgets() {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val thisAppWidget = ComponentName(packageName, WidgetProvider::class.java.name)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)

        records.clear()

        appWidgetIds.forEach { appWidgetId ->

            val preferences = PreferenceManager.getDefaultSharedPreferences(this)
            val busStopId = preferences.getString(Config.WIDGET_PREFIX + appWidgetId, null)
            var busStop: BusStop? = null

            if(busStopId != null) {
                busStop = BusStops.getInstance(this).getByNumber(busStopId)
            }

            records.add(Widget(appWidgetId.toString(), busStop))
        }

        adapter!!.notifyDataSetChanged()
    }

    override fun onBillingInitialized() {
        if(widgetId != null) {
            openBusStopDialog(widgetId!!)
        }
    }

    override fun onPurchaseHistoryRestored() {

    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
        Snackbar
                .make(findViewById(R.id.swiperefresh), R.string.payment_success, Snackbar.LENGTH_LONG)
                .show()

        if(widgetId != null) {
            openBusStopDialog(widgetId!!)
        }
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        if(errorCode ==  Constants.BILLING_RESPONSE_RESULT_USER_CANCELED) {
            Snackbar
                    .make(findViewById(R.id.swiperefresh), R.string.payment_cancel, Snackbar.LENGTH_LONG)
                    .show()
        } else {
            Snackbar
                    .make(findViewById(R.id.swiperefresh), R.string.payment_error, Snackbar.LENGTH_LONG)
                    .show()
        }
    }
}
