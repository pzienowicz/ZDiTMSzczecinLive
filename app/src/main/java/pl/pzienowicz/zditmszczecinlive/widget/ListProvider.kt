package pl.pzienowicz.zditmszczecinlive.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.widget.RemoteViews
import android.widget.RemoteViewsService

import java.util.ArrayList

import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.model.WidgetLine
import org.jsoup.Jsoup
import android.util.Log
import pl.pzienowicz.zditmszczecinlive.Config
import java.io.IOException
import java.lang.Exception

class ListProvider(val context: Context, intent: Intent) : RemoteViewsService.RemoteViewsFactory {

    private val listItemList = ArrayList<WidgetLine>()
    private val appWidgetId: Int = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)

    init {

    }

    override fun onCreate() {
        Log.d(Config.LOG_TAG, "ListProvider:onCreate")
    }

    override fun onDataSetChanged() {
        val appWidgetAlarm = AppWidgetAlarm(context)
        appWidgetAlarm.startAlarm()

        Log.d(Config.LOG_TAG, "ListProvider:onDataSetChanged")

        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val busStopId = preferences.getString(Config.WIDGET_PREFIX + appWidgetId, null)

        val tempList: ArrayList<WidgetLine> = ArrayList()

        try {
            val doc = Jsoup.connect("http://www.zditm.szczecin.pl/json/tablica.inc.php?slupek=" + busStopId).get()

            Log.d(Config.LOG_TAG, doc.text())

            val elements = doc.getElementsByTag("tbody")

            if(elements.size > 0) {
                val lines = elements[0].getElementsByTag("tr")
                for (line in lines) {
                    val lineName = line.getElementsByClass("gmvlinia")
                    val direction = line.getElementsByClass("gmvkierunek")
                    val time = line.getElementsByClass("gmvgodzina")

                    try {
                        tempList.add(WidgetLine(lineName[0].text(), direction[0].text(), time[0].text()))
                    }catch (e: Exception) {
                        Log.e(Config.LOG_TAG, e.message)
                        Log.d(Config.LOG_TAG, line.toString())

                        val error = line.getElementsByClass("gmvblad")
                        if(error[0] != null) {
                            tempList.add(WidgetLine("", error[0].text(), ""))
                            break
                        }
                    }
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()

            //Log.e(Config.LOG_TAG, e.message)
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

        if(this.count <= position) {
            return remoteView
        }

        val line = listItemList[position]
        remoteView.setTextViewText(R.id.lineNumberTv, line.lineNumber)
        remoteView.setTextViewText(R.id.directionTv, line.direction)
        remoteView.setTextViewText(R.id.timeLeftTv, line.timeLeft)

        return remoteView
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1
}
