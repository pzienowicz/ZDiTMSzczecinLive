package pl.pzienowicz.zditmszczecinlive.activity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.http.SslError
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout

import com.getbase.floatingactionbutton.FloatingActionButton
import com.getbase.floatingactionbutton.FloatingActionsMenu
import com.onesignal.OneSignal

import java.util.Timer
import java.util.TimerTask

import pl.pzienowicz.zditmszczecinlive.Config
import pl.pzienowicz.zditmszczecinlive.Functions
import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.dialog.*
import pl.pzienowicz.zditmszczecinlive.listener.BusStopSelectedListener
import pl.pzienowicz.zditmszczecinlive.widget.AppWidgetAlarm

class MainActivity : AppCompatActivity(), LocationListener {

    private var myWebView: WebView? = null
    private var sharedPreferences: SharedPreferences? = null
    private var context: Context? = null
    private var bcr: BroadcastReceiver? = null
    private var locationManager: LocationManager? = null
    private var currentLocation: Location? = null
    private var myTimer: Timer? = null
    private var zoomMap = false
    private var currentUrl = Config.URL

    override fun onCreate(savedInstanceState: Bundle?) {
        OneSignal.startInit(this).init()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this

        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences!!.edit().putInt(Config.PREFERENCE_SELECTED_LINE, 0).apply()

        val floatingActionsMenu = findViewById(R.id.multiple_actions) as FloatingActionsMenu

        val setFavouriteBtn = findViewById(R.id.set_favourite) as FloatingActionButton
        setFavouriteBtn.setOnClickListener {
            sharedPreferences!!.edit().putString(Config.PREFERENCE_FAVOURITE_MAP, currentUrl).apply()
            Snackbar.make(findViewById(R.id.swiperefresh), R.string.set_favourite, Snackbar.LENGTH_LONG).show()
            floatingActionsMenu.collapse()
        }

        val showInfoBtn = findViewById(R.id.show_info) as FloatingActionButton
        showInfoBtn.setOnClickListener {
            val dialog = InfoDialog(this@MainActivity)
            dialog.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            dialog.show()
            floatingActionsMenu.collapse()
        }

        val dashboardButton = findViewById(R.id.show_dashboard) as FloatingActionButton
        dashboardButton.setOnClickListener {
            val dialog = BusStopDialog(this@MainActivity, BusStopSelectedListener { busStop ->
                val boardDialog = ScheduleBoardDialog(this@MainActivity, busStop)
                boardDialog.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                boardDialog.show()
            }, null)
            dialog.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            dialog.show()
            floatingActionsMenu.collapse()
        }

        val actionButton = findViewById(R.id.show_lines) as FloatingActionButton
        actionButton.setOnClickListener {
            val dialog = LineDialog(context)
            dialog.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            dialog.show()
            floatingActionsMenu.collapse()
        }

        val settingsButton = findViewById(R.id.settings) as FloatingActionButton
        settingsButton.setOnClickListener {
            val dialog = SettingsDialog(context!!)
            dialog.window!!.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            dialog.show()
            floatingActionsMenu.collapse()
        }

        val widgetsBtn = findViewById(R.id.widgets) as FloatingActionButton
        widgetsBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, WidgetsActivity::class.java)
            startActivity(intent)
            floatingActionsMenu.collapse()
        }

        myWebView = findViewById(R.id.webView) as WebView
        myWebView!!.settings.javaScriptEnabled = true
        myWebView!!.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {}
            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                handler?.proceed()
            }
        })

        val filter = IntentFilter()
        filter.addAction(Config.INTENT_LOAD_NEW_URL)
        filter.addAction(Config.INTENT_REFRESH_SETTINGS)
        filter.addAction(Config.INTENT_NO_INTERNET_CONNETION)

        bcr = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    Config.INTENT_LOAD_NEW_URL -> {
                        val lineId = intent.getIntExtra(Config.EXTRA_LINE_ID, 0)
                        if (lineId == 0) {
                            currentUrl = Config.URL
                        } else {
                            currentUrl = Config.LINE_URL + lineId
                        }
                        myWebView!!.loadUrl(currentUrl)
                    }
                    Config.INTENT_REFRESH_SETTINGS -> refreshSettings()
                    Config.INTENT_NO_INTERNET_CONNETION -> showNoInternetSnackbar()
                }
            }
        }
        registerReceiver(bcr, filter)
        refreshSettings()

        if (savedInstanceState != null) {
            myWebView!!.restoreState(savedInstanceState)
        } else {
            loadPage()
        }

        myTimer = Timer()
        myTimer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (currentLocation != null) {
                    runOnUiThread {
                        var newUrl = currentUrl + "?lat=" + currentLocation!!.latitude + "&lon=" + currentLocation!!.longitude

                        if (zoomMap) {
                            newUrl += "&zoom=16"
                        }

                        myWebView!!.loadUrl(newUrl)
                    }
                }
            }
        }, 0, (30 * 1000).toLong())

        val alarm = AppWidgetAlarm(this)
        alarm.startAlarm()
    }

    private fun refreshSettings() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (sharedPreferences!!.getBoolean(Config.PREFERENCE_USE_LOCATION, true)) {
            if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this@MainActivity)
            } else {
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), MY_PERMISSIONS_REQUEST_LOCATION)
            }
        } else {
            if (locationManager != null) {
                locationManager!!.removeUpdates(this@MainActivity)
                locationManager = null
            }
            currentLocation = null
        }

        zoomMap = sharedPreferences!!.getBoolean(Config.PREFERENCE_ZOOM_MAP, true)
    }

    public override fun onDestroy() {
        if (myTimer != null) {
            myTimer!!.cancel()
        }
        unregisterReceiver(bcr)
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        myWebView!!.saveState(outState)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    finish()
                    return true
                }
            }

        }
        return super.onKeyDown(keyCode, event)
    }

    private fun loadPage() {
        if (Functions.isNetworkAvailable(this)) {
            val favouriteMap = sharedPreferences!!.getString(Config.PREFERENCE_FAVOURITE_MAP, Config.URL)

            myWebView!!.loadUrl(favouriteMap)
            showInitDialog()
        } else {
            showNoInternetSnackbar()
        }
    }

    private fun showInitDialog() {
        if (!sharedPreferences!!.getBoolean(Config.PREFERENCE_SHOW_DIALOG, true)) {
            return
        }

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(R.string.dialog_title)
        alertDialogBuilder
                .setMessage(R.string.info)
                .setCancelable(false)
                .setPositiveButton(R.string.close) { dialog, id -> dialog.cancel() }
                .setNegativeButton(R.string.do_not_show_more) { dialog, id ->
                    sharedPreferences!!.edit().putBoolean(Config.PREFERENCE_SHOW_DIALOG, false).apply()
                    dialog.cancel()
                }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        sharedPreferences!!.edit().putBoolean(Config.PREFERENCE_ZOOM_MAP, false).apply()
                        sharedPreferences!!.edit().putBoolean(Config.PREFERENCE_USE_LOCATION, false).apply()
                    }
                    locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this@MainActivity)
                } else {
                    sharedPreferences!!.edit().putBoolean(Config.PREFERENCE_ZOOM_MAP, false).apply()
                    sharedPreferences!!.edit().putBoolean(Config.PREFERENCE_USE_LOCATION, false).apply()
                }
            }
        }
    }


    private fun showNoInternetSnackbar() {
        Snackbar
                .make(findViewById(R.id.swiperefresh), R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.refresh) { loadPage() }
                .show()
    }

    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            currentLocation = location
        }
    }

    override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {

    }

    override fun onProviderEnabled(s: String) {

    }

    override fun onProviderDisabled(s: String) {

    }

    companion object {
        private val MY_PERMISSIONS_REQUEST_LOCATION = 1443
    }
}
