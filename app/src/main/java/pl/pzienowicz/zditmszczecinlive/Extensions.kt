package pl.pzienowicz.zditmszczecinlive

import android.app.Activity
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar

val Context.inflater: LayoutInflater
    get() = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

val Context.alarmManager: AlarmManager
    get() = getSystemService(Context.ALARM_SERVICE) as AlarmManager

val Context.connectivityManager: ConnectivityManager
    get() = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

val Context.locationManager: LocationManager
    get() = getSystemService(Context.LOCATION_SERVICE) as LocationManager

fun Context.createPendingIntent(
    requestCode: Int,
    intent: Intent,
    flags: Int
): PendingIntent {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.getBroadcast(this, requestCode, intent, flags or PendingIntent.FLAG_MUTABLE)
    } else {
        PendingIntent.getBroadcast(this, requestCode, intent, flags)
    }
}

fun Dialog.setFullWidth() {
    window?.setLayout(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
}

fun Activity.showBar(
    @StringRes text: Int,
    @StringRes actionText: Int? = null,
    action: View.OnClickListener? = null
) {
    Snackbar.make(
        findViewById(R.id.swiperefresh),
        text,
        Snackbar.LENGTH_LONG
    ).apply {
        if (actionText != null) {
            setAction(actionText, action)
        }
    }.show()
}

fun Context.showToast(@StringRes text: Int) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}

val Context.isNetworkAvailable
    get() = connectivityManager.activeNetworkInfo != null

val Context.isGpsProviderAvailable
    get() = packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION)

val Context.isLandscape
    get() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

fun Context.registerReceiver(
    actions: List<String>,
    onReceive: (intent: Intent?) -> Unit
): BroadcastReceiver {
    val filter = IntentFilter().apply {
        actions.forEach {
            addAction(it)
        }
    }

    val bcr = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            onReceive(p1)
        }
    }
    registerReceiver(bcr, filter)
    return bcr
}