package pl.pzienowicz.zditmszczecinlive.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import pl.pzienowicz.zditmszczecinlive.Config
import java.util.*
import android.os.Build
import android.util.Log
import androidx.preference.PreferenceManager

class AppWidgetAlarm(private val mContext: Context) {

    private val ALARM_ID = 4325
    private val INTERVAL_MILLIS = 1000

    fun startAlarm() {
        val widgetsEnabled = PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(Config.PREFERENCE_WIDGETS_REFRESH,true)
        val seconds = PreferenceManager.getDefaultSharedPreferences(mContext).getString(Config.PREFERENCE_WIDGETS_REFRESH_TIME, "30") ?: "30"
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
        val pendingIntent = PendingIntent.getBroadcast(mContext, ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        val am = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmType = AlarmManager.RTC_WAKEUP
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> am.setExactAndAllowWhileIdle(alarmType, calendar.timeInMillis, pendingIntent)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> am.setExact(alarmType, calendar.timeInMillis, pendingIntent)
            else -> am.set(alarmType, calendar.timeInMillis, pendingIntent)
        }

        Log.d(Config.LOG_TAG, "alarm set: " + calendar.timeInMillis)
    }

    fun stopAlarm() {
        val alarmIntent = Intent(Config.ACTION_AUTO_UPDATE)
        val pendingIntent = PendingIntent.getBroadcast(mContext, ALARM_ID, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        val alarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}