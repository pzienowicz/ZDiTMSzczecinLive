package pl.pzienowicz.zditmszczecinlive

import android.app.AlarmManager
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.location.LocationManager
import android.net.ConnectivityManager
import android.view.LayoutInflater
import android.view.Window
import android.widget.LinearLayout
import androidx.preference.PreferenceManager

val Context.prefs: SharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(this)

val Context.inflater: LayoutInflater
    get() = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

val Context.alarmManager: AlarmManager
    get() = getSystemService(Context.ALARM_SERVICE) as AlarmManager

val Context.connectivityManager: ConnectivityManager
    get() = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

val Context.locationManager: LocationManager
    get() = getSystemService(Context.LOCATION_SERVICE) as LocationManager

fun Dialog.setFullWidth() {
    window?.setLayout(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
}