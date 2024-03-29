package pl.pzienowicz.zditmszczecinlive.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import pl.pzienowicz.zditmszczecinlive.Config
import java.util.*
import android.os.Build
import android.util.Log
import pl.pzienowicz.zditmszczecinlive.alarmManager
import pl.pzienowicz.zditmszczecinlive.createPendingIntent
import pl.pzienowicz.zditmszczecinlive.prefs

class AppWidgetAlarm(private val mContext: Context) {

    private val ALARM_ID = 4325
    private val INTERVAL_MILLIS = 1000

    fun startAlarm() {
        val widgetsEnabled = mContext.prefs.refreshWidgets
        val seconds = mContext.prefs.refreshWidgetsTime
        val secondsInt: Int

        try {
            secondsInt = seconds.toInt()
        } catch(e: NumberFormatException) {
            return
        }

        if(!widgetsEnabled || secondsInt == 0) {
            return
        }

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MILLISECOND, secondsInt * INTERVAL_MILLIS)

        val alarmIntent = Intent(Config.ACTION_AUTO_UPDATE)
        alarmIntent.setClass(mContext, WidgetProvider::class.java)
        val pendingIntent = mContext.createPendingIntent(ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        val am = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmType = AlarmManager.RTC_WAKEUP
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                    am.setExactAndAllowWhileIdle(alarmType, calendar.timeInMillis, pendingIntent)
                } else {
                    if (am.canScheduleExactAlarms()) {
                        am.setExactAndAllowWhileIdle(alarmType, calendar.timeInMillis, pendingIntent)
                    } else {
                        Log.w(Config.LOG_TAG, "no permission to set alarm")
                    }
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                -> am.setExact(alarmType, calendar.timeInMillis, pendingIntent)
            else -> am.set(alarmType, calendar.timeInMillis, pendingIntent)
        }

        Log.d(Config.LOG_TAG, "alarm set: " + calendar.timeInMillis)
    }

    fun stopAlarm() {
        val alarmIntent = Intent(Config.ACTION_AUTO_UPDATE)
        val pendingIntent = mContext.createPendingIntent(ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        mContext.alarmManager.cancel(pendingIntent)
    }
}