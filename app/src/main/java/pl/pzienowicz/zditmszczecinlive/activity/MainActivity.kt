package pl.pzienowicz.zditmszczecinlive.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import android.widget.PopupWindow
import pl.pzienowicz.zditmszczecinlive.*
import pl.pzienowicz.zditmszczecinlive.databinding.ActivityMainBinding
import pl.pzienowicz.zditmszczecinlive.dialog.*
import pl.pzienowicz.zditmszczecinlive.timer.MapTimer
import androidx.core.net.toUri

class MainActivity : AppCompatActivity() {

    private var bcr: BroadcastReceiver? = null
    private var currentLocation: Location? = null
    private lateinit var mapTimer: MapTimer
    private var currentUrl = Config.URL
    var mGeoLocationCallback: GeolocationPermissions.Callback? = null
    var mGeoLocationRequestOrigin: String? = null

    private lateinit var binding: ActivityMainBinding
    private var morePopupWindow: PopupWindow? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowInsetsControllerCompat(window, window.decorView).hide(WindowInsetsCompat.Type.statusBars())

        prefs.selectedLine = 0

        binding.setFavourite.setOnClickListener {
            prefs.favouriteMap = currentUrl
            showBar(R.string.set_favourite)
            binding.multipleActions.collapse()
        }

        binding.showInfo.setOnClickListener {
            openInfoDialog()
        }

        binding.showDashboard.setOnClickListener {
            openScheduleBoardDialog()
        }

        binding.showLines.setOnClickListener {
            openLineDialog()
        }

        binding.settings.setOnClickListener { openSettingsDialog() }

        binding.widgets.setOnClickListener { openWidgetsDialog() }

        binding.navLines.setOnClickListener {
            openLineDialog()
        }

        binding.navInfo.setOnClickListener {
            openInfoDialog()
        }

        binding.navDashboard.setOnClickListener {
            openScheduleBoardDialog()
        }

        binding.navMore.setOnClickListener { showMoreMenu() }

        binding.forum.setOnClickListener { openForum() }

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.domStorageEnabled = true
        binding.webView.settings.setGeolocationEnabled(true)
        binding.webView.webViewClient = WebViewClient()
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

        openWidgetsDialogIfRequested(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        openWidgetsDialogIfRequested(intent)
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

    private fun openInfoDialog() {
        showDialog(InfoDialog(this))
    }

    private fun openScheduleBoardDialog() {
        val dialog = BusStopDialog(this, { busStop ->
            showDialog(ScheduleBoardDialog(this, busStop))
        }, null)
        showDialog(dialog)
    }

    private fun openLineDialog() {
        showDialog(LineDialog(this))
    }

    private fun openWidgetsDialog() {
        showDialog(WidgetsDialog(this))
    }

    private fun openSettingsDialog() {
        showDialog(SettingsDialog(this))
    }

    private fun openForum() {
        val facebookIntent = Intent(Intent.ACTION_VIEW)
        facebookIntent.data = Config.FB_GROUP_URL.toUri()
        startActivity(facebookIntent)
        binding.multipleActions.collapse()
    }

    private fun showMoreMenu() {
        morePopupWindow?.dismiss()

        val popupView = layoutInflater.inflate(R.layout.view_more_menu, binding.root, false)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            isOutsideTouchable = true
            elevation = 0f
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        morePopupWindow = popupWindow

        popupView.findViewById<View>(R.id.more_widgets).setOnClickListener {
            popupWindow.dismiss()
            openWidgetsDialog()
        }
        popupView.findViewById<View>(R.id.more_settings).setOnClickListener {
            popupWindow.dismiss()
            openSettingsDialog()
        }
        popupView.findViewById<View>(R.id.more_forum).setOnClickListener {
            popupWindow.dismiss()
            openForum()
        }

        popupView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        val anchorLocation = IntArray(2)
        val rootLocation = IntArray(2)
        binding.navMore.getLocationOnScreen(anchorLocation)
        binding.root.getLocationOnScreen(rootLocation)

        val margin = resources.getDimensionPixelSize(R.dimen.more_menu_margin)
        val x = anchorLocation[0] - rootLocation[0] + binding.navMore.width - popupView.measuredWidth
        val y = anchorLocation[1] - rootLocation[1] - popupView.measuredHeight - margin
        popupWindow.showAtLocation(binding.root, Gravity.NO_GRAVITY, x.coerceAtLeast(margin), y)
    }

    private fun openWidgetsDialogIfRequested(intent: Intent?) {
        val widgetId = intent?.getStringExtra(Config.EXTRA_WIDGET_ID) ?: return
        showDialog(WidgetsDialog(this, widgetId))
        intent.removeExtra(Config.EXTRA_WIDGET_ID)
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 1443
    }
}
