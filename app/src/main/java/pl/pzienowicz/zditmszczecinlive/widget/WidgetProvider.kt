package pl.pzienowicz.zditmszczecinlive.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import android.content.Intent
import android.net.Uri
import android.view.View
import pl.pzienowicz.zditmszczecinlive.Config
import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.data.BusStops
import android.app.PendingIntent
import android.util.Log
import pl.pzienowicz.zditmszczecinlive.activity.WidgetsActivity
import android.content.ComponentName
import androidx.preference.PreferenceManager

class WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        for (widget in appWidgetIds!!) {
            val remoteViews = updateWidgetListView(context, widget)
            appWidgetManager!!.notifyAppWidgetViewDataChanged(widget, R.id.listView)
            appWidgetManager.updateAppWidget(widget, remoteViews)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    private fun updateWidgetListView(context: Context, appWidgetId: Int): RemoteViews {
        //which layout to show on widget
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_layout)

        //RemoteViews Service needed to provide adapter for ListView
        val svcIntent = Intent(context, WidgetService::class.java)
        //passing app widget id to that RemoteViews Service
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        svcIntent.data = Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME))

        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val busStopId = preferences.getString(Config.WIDGET_PREFIX + appWidgetId, null)

        if(busStopId != null) {
            BusStops.getInstance(context).loadByNumber(busStopId) { busStop ->
                svcIntent.putExtra(Config.EXTRA_BUS_STOP_NUMBER, busStop!!.numer)

                remoteViews.setTextViewText(
                    R.id.busStopTv, busStop.nazwa + " " + busStop.numer.substring(3, 5)
                )
                remoteViews.setViewVisibility(R.id.listView, View.VISIBLE)
                remoteViews.setViewVisibility(R.id.inputLayout, View.GONE)
                remoteViews.setRemoteAdapter(appWidgetId, R.id.listView, svcIntent)
            }
        } else {
            remoteViews.setViewVisibility(R.id.inputLayout, View.VISIBLE)
            remoteViews.setViewVisibility(R.id.listView, View.GONE)

            val intent = Intent(context, javaClass)
            intent.action = Config.CLICK_WIDGET_BUTTON
            intent.putExtra(Config.EXTRA_WIDGET_ID, appWidgetId.toString())

            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            remoteViews.setOnClickPendingIntent(R.id.setBusStopBtn, pendingIntent)
        }

        val intent = Intent(context, javaClass)
        intent.action = Config.ACTION_AUTO_UPDATE

        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        remoteViews.setOnClickPendingIntent(R.id.refreshBtn, pendingIntent)

        return remoteViews
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        Log.d(Config.LOG_TAG, "action: " + intent.action)

        when(intent.action) {
            Config.CLICK_WIDGET_BUTTON -> {
                val intent1 = Intent(context, WidgetsActivity::class.java)
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent1.putExtra(Config.EXTRA_WIDGET_ID, intent.getStringExtra(Config.EXTRA_WIDGET_ID))
                context.startActivity(intent1)
            }
            Config.ACTION_AUTO_UPDATE -> {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val thisAppWidget = ComponentName(context.packageName, WidgetProvider::class.java.name)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)

                onUpdate(context, appWidgetManager, appWidgetIds)
            }
            Config.INTENT_WIDGET_DATA_LOADED -> { }
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)

        Log.d(Config.LOG_TAG, "alarm started")
        val appWidgetAlarm = AppWidgetAlarm(context.applicationContext)
        appWidgetAlarm.startAlarm()
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)

        Log.d(Config.LOG_TAG, "alarm stopped")
        val appWidgetAlarm = AppWidgetAlarm(context.applicationContext)
        appWidgetAlarm.stopAlarm()
    }
}