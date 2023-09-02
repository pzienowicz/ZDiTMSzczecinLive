package pl.pzienowicz.zditmszczecinlive.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import pl.pzienowicz.zditmszczecinlive.*
import pl.pzienowicz.zditmszczecinlive.databinding.ActivityMainBinding
import pl.pzienowicz.zditmszczecinlive.dialog.*
import pl.pzienowicz.zditmszczecinlive.timer.MapTimer
import pl.pzienowicz.zditmszczecinlive.widget.AppWidgetAlarm

class MainActivity : AppCompatActivity() {

    private var bcr: BroadcastReceiver? = null
    private var currentLocation: Location? = null
    private lateinit var mapTimer: MapTimer
    private var currentUrl = Config.URL
    var mGeoLocationCallback: GeolocationPermissions.Callback? = null
    var mGeoLocationRequestOrigin: String? = null

    private lateinit var binding: ActivityMainBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs.selectedLine = 0

        binding.setFavourite.setOnClickListener {
            prefs.favouriteMap = currentUrl
            showBar(R.string.set_favourite)
            binding.multipleActions.collapse()
        }

        binding.showInfo.setOnClickListener {
            showDialog(InfoDialog(this))
        }

        binding.showDashboard.setOnClickListener {
            val dialog = BusStopDialog(this, { busStop ->
                showDialog(ScheduleBoardDialog(this, busStop))
            }, null)
            showDialog(dialog)
        }

        binding.showLines.setOnClickListener {
            showDialog(LineDialog(this))
        }

        binding.showCameras.setOnClickListener {
            showDialog(CamerasDialog(this))
        }

        binding.settings.setOnClickListener {
            showDialog(SettingsDialog(this))
        }

        binding.widgets.setOnClickListener {
            val intent = Intent(this, WidgetsActivity::class.java)
            startActivity(intent)
            binding.multipleActions.collapse()
        }

        binding.forum.setOnClickListener {
            val facebookIntent = Intent(Intent.ACTION_VIEW)
            facebookIntent.data = Uri.parse(Config.FB_GROUP_URL)
            startActivity(facebookIntent)
            binding.multipleActions.collapse()
        }

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.domStorageEnabled = true
        binding.webView.settings.setGeolocationEnabled(true)
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onGeolocationPermissionsShowPrompt(
                origin: String, callback: GeolocationPermissions.Callback
            ) {
                if (
                    ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    mGeoLocationCallback = callback
                    mGeoLocationRequestOrigin = origin
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        MY_PERMISSIONS_REQUEST_LOCATION
                    )
                } else {
                    callback.invoke(origin, true, false)
                }
            }
        }

        bcr = registerReceiver(listOf(
            Config.INTENT_LOAD_NEW_URL,
            Config.INTENT_REFRESH_SETTINGS,
            Config.INTENT_NO_INTERNET_CONNECTION
        )) { intent ->
            when (intent?.action) {
                Config.INTENT_LOAD_NEW_URL -> {
                    val lineId = intent.getStringExtra(Config.EXTRA_LINE_ID)
                    currentUrl = if (lineId.isNullOrEmpty()) {
                        Config.URL
                    } else {
                        Config.LINE_URL + lineId
                    }
                    Log.d(Config.LOG_TAG, currentUrl)
                    binding.webView.loadUrl(currentUrl)
                }
                Config.INTENT_NO_INTERNET_CONNECTION -> showNoInternetSnackbar()
            }
        }

        if (savedInstanceState != null) {
            binding.webView.restoreState(savedInstanceState)
        } else {
            loadPage()
        }

        mapTimer = MapTimer {
            runOnUiThread {
                currentLocation?.let {
                    val url = currentUrl
                        .plus("?lat=" + it.latitude)
                        .plus("&lon=" + it.longitude)
                    Log.d(Config.LOG_TAG, url)
                    binding.webView.loadUrl(url)
                }
            }
        }
        mapTimer.start()

        val alarm = AppWidgetAlarm(this)
        alarm.startAlarm()
    }

    private fun showDialog(dialog: Dialog) {
        dialog.setFullWidth()
        dialog.show()
        binding.multipleActions.collapse()
    }

    public override fun onDestroy() {
        mapTimer.stop()
        unregisterReceiver(bcr)
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        binding.webView.saveState(outState)
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
        if (isNetworkAvailable) {
            binding.webView.loadUrl(prefs.favouriteMap)
            showInitDialog()
        } else {
            showNoInternetSnackbar()
        }
    }

    private fun showInitDialog() {
        if (!prefs.showInitDialog) {
            return
        }

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(R.string.dialog_title)
        alertDialogBuilder
                .setMessage(R.string.info)
                .setCancelable(false)
                .setPositiveButton(R.string.close) { dialog, id -> dialog.cancel() }
                .setNegativeButton(R.string.do_not_show_more) { dialog, id ->
                    prefs.showInitDialog = false
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mGeoLocationCallback?.invoke(mGeoLocationRequestOrigin, true, false)
                }
            }
        }

    }

    private fun showNoInternetSnackbar() {
        showBar(R.string.no_internet, R.string.refresh) { loadPage() }
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 1443
    }
}
