package pl.pzienowicz.zditmszczecinlive

import android.content.Context
import android.net.ConnectivityManager
import android.content.pm.PackageManager

object Functions {

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null
    }

    fun isGpsProviderAvailable(context: Context): Boolean {
        val pm = context.packageManager
        return pm.hasSystemFeature(PackageManager.FEATURE_LOCATION)
    }
}
