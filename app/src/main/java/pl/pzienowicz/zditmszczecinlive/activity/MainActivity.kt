package pl.pzienowicz.zditmszczecinlive.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

import com.getbase.floatingactionbutton.FloatingActionButton
import com.getbase.floatingactionbutton.FloatingActionsMenu
import com.google.android.material.snackbar.Snackbar
import com.onesignal.OneSignal
import pl.pzienowicz.zditmszczecinlive.*

import java.util.Timer
import java.util.TimerTask

import pl.pzienowicz.zditmszczecinlive.dialog.*
import pl.pzienowicz.zditmszczecinlive.widget.AppWidgetAlarm

class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var myWebView: WebView
    private var bcr: BroadcastReceiver? = null
    private var locationManager: LocationManager? = null
    private var currentLocation: Location? = null
    private lateinit var myTimer: Timer
    private var zoomMap = false
    private var currentUrl = Config.URL

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        OneSignal.startInit(this).init()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        prefs.edit().putInt(Config.PREFERENCE_SELECTED_LINE, 0).apply()

        val floatingActionsMenu = findViewById<FloatingActionsMenu>(R.id.multiple_actions)

        val setFavouriteBtn = findViewById<FloatingActionButton>(R.id.set_favourite)
        setFavouriteBtn.setOnClickListener {
            prefs.edit().putString(Config.PREFERENCE_FAVOURITE_MAP, currentUrl).apply()
            Snackbar.make(
                findViewById(R.id.swiperefresh),
                R.string.set_favourite,
                Snackbar.LENGTH_LONG
            ).show()
            floatingActionsMenu.collapse()
        }

        val showInfoBtn = findViewById<FloatingActionButton>(R.id.show_info)
        showInfoBtn.setOnClickListener {
            val dialog = InfoDialog(this)
            dialog.setFullWidth()
            dialog.show()
            floatingActionsMenu.collapse()
        }

        val dashboardButton = findViewById<FloatingActionButton>(R.id.show_dashboard)
        dashboardButton.setOnClickListener {
            val dialog = BusStopDialog(this, { busStop ->
                val boardDialog = ScheduleBoardDialog(this, busStop)
                boardDialog.setFullWidth()
                boardDialog.show()
            }, null)
            dialog.setFullWidth()
            dialog.show()
            floatingActionsMenu.collapse()
        }

        val actionButton = findViewById<FloatingActionButton>(R.id.show_lines)
        actionButton.setOnClickListener {
            val dialog = LineDialog(this)
            dialog.setFullWidth()
            dialog.show()
            floatingActionsMenu.collapse()
        }

        val camerasButton = findViewById<FloatingActionButton>(R.id.show_cameras)
        camerasButton.setOnClickListener {
            val dialog = CamerasDialog(this)
            dialog.setFullWidth()
            dialog.show()
            floatingActionsMenu.collapse()
        }

        val settingsButton = findViewById<FloatingActionButton>(R.id.settings)
        settingsButton.setOnClickListener {
            val dialog = SettingsDialog(this)
            dialog.setFullWidth()
            dialog.show()
            floatingActionsMenu.collapse()
        }

        val widgetsBtn = findViewById<FloatingActionButton>(R.id.widgets)
        widgetsBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, WidgetsActivity::class.java)
            startActivity(intent)
            floatingActionsMenu.collapse()
        }

        val forumBtn = findViewById<FloatingActionButton>(R.id.forum)
        forumBtn.setOnClickListener {
            val facebookIntent = Intent(Intent.ACTION_VIEW)
            facebookIntent.data = Uri.parse(Config.FB_GROUP_URL)
            startActivity(facebookIntent)
            floatingActionsMenu.collapse()
        }

        myWebView = findViewById(R.id.webView)
        myWebView.settings.javaScriptEnabled = true
        myWebView.webViewClient = object : WebViewClient() {}

        val filter = IntentFilter().apply {
            addAction(Config.INTENT_LOAD_NEW_URL)
            addAction(Config.INTENT_REFRESH_SETTINGS)
            addAction(Config.INTENT_NO_INTERNET_CONNETION)
        }

        bcr = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    Config.INTENT_LOAD_NEW_URL -> {
                        val lineId = intent.getIntExtra(Config.EXTRA_LINE_ID, 0)
                        currentUrl = if (lineId == 0) {
                            Config.URL
                        } else {
                            Config.LINE_URL + lineId
                        }
                        myWebView.loadUrl(currentUrl)
                    }
                    Config.INTENT_REFRESH_SETTINGS -> refreshSettings()
                    Config.INTENT_NO_INTERNET_CONNETION -> showNoInternetSnackbar()
                }
            }
        }
        registerReceiver(bcr, filter)
        refreshSettings()

        if (savedInstanceState != null) {
            myWebView.restoreState(savedInstanceState)
        } else {
            loadPage()
        }

        myTimer = Timer()
        myTimer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (currentLocation != null) {
                    runOnUiThread {
                        var newUrl = currentUrl + "?lat=" + currentLocation!!.latitude + "&lon=" + currentLocation!!.longitude

                        if (zoomMap) {
                            newUrl += "&zoom=16"
                        }

                        myWebView.loadUrl(newUrl)
                    }
                }
            }
        }, 0, (30 * 1000).toLong())

        val alarm = AppWidgetAlarm(this)
        alarm.startAlarm()
    }

    private fun refreshSettings() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (Functions.isGpsProviderAvailable(this)
            && prefs.getBoolean(Config.PREFERENCE_USE_LOCATION, true)) {
            if (
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                locationManager?.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0, 0f, this
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
        } else {
            if (locationManager != null) {
                locationManager!!.removeUpdates(this)
                locationManager = null
            }
            currentLocation = null
        }

        zoomMap = prefs.getBoolean(Config.PREFERENCE_ZOOM_MAP, true)
    }

    public override fun onDestroy() {
        myTimer.cancel()
        unregisterReceiver(bcr)
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        myWebView.saveState(outState)
        return super.onSaveInstanceState(outState)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun loadPage() {
        if (Functions.isNetworkAvailable(this)) {
            val favouriteMap = prefs.getString(Config.PREFERENCE_FAVOURITE_MAP, Config.URL)

            myWebView.loadUrl(favouriteMap)
            showInitDialog()
        } else {
            showNoInternetSnackbar()
        }
    }

    private fun showInitDialog() {
        if (!prefs.getBoolean(Config.PREFERENCE_SHOW_DIALOG, true)) {
            return
        }

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(R.string.dialog_title)
        alertDialogBuilder
                .setMessage(R.string.info)
                .setCancelable(false)
                .setPositiveButton(R.string.close) { dialog, id -> dialog.cancel() }
                .setNegativeButton(R.string.do_not_show_more) { dialog, id ->
                    prefs.edit().putBoolean(Config.PREFERENCE_SHOW_DIALOG, false).apply()
                    dialog.cancel()
                }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        prefs.edit().putBoolean(Config.PREFERENCE_ZOOM_MAP, false).apply()
                        prefs.edit().putBoolean(Config.PREFERENCE_USE_LOCATION, false).apply()
                    }
                    locationManager?.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0,
                        0f,
                        this
                    )
                } else {
                    prefs.edit().putBoolean(Config.PREFERENCE_ZOOM_MAP, false).apply()
                    prefs.edit().putBoolean(Config.PREFERENCE_USE_LOCATION, false).apply()
                }
            }
        }
    }


    private fun showNoInternetSnackbar() {
        Snackbar.make(
            findViewById(R.id.swiperefresh),
            R.string.no_internet,
            Snackbar.LENGTH_INDEFINITE
        ).setAction(R.string.refresh) { loadPage() }
            .show()
    }

    override fun onLocationChanged(location: Location?) {
        location?.let {
            currentLocation = it
        }
    }

    override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
    override fun onProviderEnabled(s: String) {}
    override fun onProviderDisabled(s: String) {}

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 1443
    }
}
