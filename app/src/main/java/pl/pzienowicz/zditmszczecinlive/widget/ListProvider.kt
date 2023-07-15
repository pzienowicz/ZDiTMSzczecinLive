package pl.pzienowicz.zditmszczecinlive.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService

import java.util.ArrayList

import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.model.WidgetLine
import android.util.Log
import android.view.View
import androidx.preference.PreferenceManager
import pl.pzienowicz.zditmszczecinlive.Config
import pl.pzienowicz.zditmszczecinlive.model.Board
import pl.pzienowicz.zditmszczecinlive.rest.RetrofitClient
import pl.pzienowicz.zditmszczecinlive.rest.ZDiTMService
import java.lang.Exception

class ListProvider(
    val context: Context, intent: Intent
) : RemoteViewsService.RemoteViewsFactory {

    private val listItemList = ArrayList<WidgetLine>()
    private val appWidgetId: Int = intent.getIntExtra(
        AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
    )

    override fun onCreate() {
        Log.d(Config.LOG_TAG, "ListProvider:onCreate")
    }

    override fun onDataSetChanged() {
        val appWidgetAlarm = AppWidgetAlarm(context)
        appWidgetAlarm.startAlarm()

        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val busStopId = preferences.getString(Config.WIDGET_PREFIX + appWidgetId, null) ?: return

        val tempList: ArrayList<WidgetLine> = ArrayList()

        val service = RetrofitClient.getRetrofit().create(ZDiTMService::class.java)
        try {
            val boardResponse = service.getBoard(busStopId).execute()
            if (boardResponse.isSuccessful && boardResponse.body() != null) {

                val board = boardResponse.body()
                board?.let {
                    if (!it.departures.isNullOrEmpty()) {
                        for (departure in it.departures) {
                            try {
                                tempList.add(
                                    WidgetLine(departure.line_number, departure.direction, getTime(departure))
                                )
                            } catch (e: Exception) {
                                Log.e(Config.LOG_TAG, e.message ?: "Unknown error")
                                Log.d(Config.LOG_TAG, departure.toString())
                            }
                        }
                    }

                    it.message?.let { message ->
                        tempList.add(WidgetLine("", message, ""))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            tempList.add(WidgetLine("", context.getString(R.string.error_occurred), ""))
        }

        listItemList.clear()
        listItemList.addAll(tempList)

        val intent = Intent(Config.INTENT_WIDGET_DATA_LOADED)
        context.sendBroadcast(intent)
    }

    override fun onDestroy() {}

    override fun getCount(): Int = listItemList.size

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = false

    override fun getViewAt(position: Int): RemoteViews {
        val remoteView = RemoteViews(context.packageName, R.layout.row_widget_line)

        if (this.count <= position) {
            return remoteView
        }

        val line = listItemList[position]

        if (line.lineNumber == "") {
            remoteView.setViewVisibility(R.id.lineNumberTv, View.GONE)
            remoteView.setViewVisibility(R.id.timeLeftTv, View.GONE)
        } else {
            remoteView.setViewVisibility(R.id.lineNumberTv, View.VISIBLE)
            remoteView.setViewVisibility(R.id.timeLeftTv, View.VISIBLE)
        }

        remoteView.setTextViewText(R.id.lineNumberTv, line.lineNumber)
        remoteView.setTextViewText(R.id.directionTv, line.direction)
        remoteView.setTextViewText(R.id.timeLeftTv, line.timeLeft)

        return remoteView
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    private fun getTime(departure: Board.Departure): String {
        departure.time_real?.let {
            return "za $it min"
        }
        departure.time_scheduled?.let {
            return it
        }
        return ""
    }
}
