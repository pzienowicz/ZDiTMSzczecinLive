package pl.pzienowicz.zditmszczecinlive.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.GeolocationPermissions
import android.webkit.WebResourceRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import android.widget.PopupWindow
import pl.pzienowicz.zditmszczecinlive.BuildConfig
import pl.pzienowicz.zditmszczecinlive.*
import pl.pzienowicz.zditmszczecinlive.billing.GooglePlayBillingClient
import pl.pzienowicz.zditmszczecinlive.data.FavouritesRepository
import pl.pzienowicz.zditmszczecinlive.data.PremiumLimits
import pl.pzienowicz.zditmszczecinlive.databinding.ActivityMainBinding
import pl.pzienowicz.zditmszczecinlive.dialog.*
import pl.pzienowicz.zditmszczecinlive.model.FavouriteLine
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
    private lateinit var billingClient: GooglePlayBillingClient
    private val premiumLimits = PremiumLimits()
    private var premiumUnlockedAfterPurchase = false
    private var morePopupWindow: PopupWindow? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        @Suppress("DEPRECATION")
        window.statusBarColor = Color.TRANSPARENT
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyWindowInsets()

        WindowInsetsControllerCompat(window, window.decorView).hide(WindowInsetsCompat.Type.statusBars())

        prefs.selectedLine = 0
        migrateFavouriteMap()
        billingClient = GooglePlayBillingClient(
            activity = this,
            onInitialized = {},
            onPurchased = {
                premiumUnlockedAfterPurchase = true
                showBar(R.string.payment_success)
            }
        )

        binding.setFavourite.setOnClickListener {
            toggleFavouriteLine()
            updateFavouriteIcon()
        }
        updateFavouriteIcon()

        binding.navLines.setOnClickListener {
            openLineDialog()
        }

        binding.navInfo.setOnClickListener {
            openInfoDialog()
        }

        binding.navDashboard.setOnClickListener {
            openScheduleBoardDialog()
        }

        binding.navFavourites.setOnClickListener {
            openFavouritesDialog()
        }
        if (BuildConfig.DEBUG) {
            binding.navFavourites.setOnLongClickListener {
                billingClient.consumePremiumProducts()
                true
            }
        }

        binding.navMore.setOnClickListener { showMoreMenu() }

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.domStorageEnabled = true
        binding.webView.settings.setGeolocationEnabled(true)
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean = openLinkExternallyIfNeeded(request?.url?.toString())

            @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean =
                openLinkExternallyIfNeeded(url)

            override fun onPageFinished(view: WebView?, url: String?) {
                applyMapThemePreference()
            }
        }
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onGeolocationPermissionsShowPrompt(
                origin: String, callback: GeolocationPermissions.Callback
            ) {
                if (
                    ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    mGeoLocationCallback = callback
                    mGeoLocationRequestOrigin = origin
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ),
                        MY_PERMISSIONS_REQUEST_LOCATION
                    )
                } else {
                    callback.invoke(origin, true, false)
                }
            }
        }
        setupBackNavigation()

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
                    updateFavouriteIcon()
                }
                Config.INTENT_REFRESH_SETTINGS -> applyMapThemePreference()
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
    }

    private fun applyWindowInsets() {
        val navigationPaddingLeft = binding.bottomNavigation.paddingLeft
        val navigationPaddingTop = binding.bottomNavigation.paddingTop
        val navigationPaddingRight = binding.bottomNavigation.paddingRight
        val navigationPaddingBottom = binding.bottomNavigation.paddingBottom
        val navigationLayoutParams = binding.bottomNavigation.layoutParams
        val navigationWidth = navigationLayoutParams.width
        val favouriteLayoutParams = binding.setFavourite.layoutParams as ViewGroup.MarginLayoutParams
        val favouriteMarginRight = favouriteLayoutParams.rightMargin
        val favouriteMarginBottom = favouriteLayoutParams.bottomMargin

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            val navigationBars = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
            if (isLandscape && navigationWidth > 0) {
                navigationLayoutParams.width = navigationWidth + navigationBars.left
                binding.bottomNavigation.layoutParams = navigationLayoutParams
                binding.bottomNavigation.updatePadding(
                    left = navigationPaddingLeft + navigationBars.left,
                    top = navigationPaddingTop + navigationBars.top,
                    right = navigationPaddingRight,
                    bottom = navigationPaddingBottom + navigationBars.bottom
                )
            } else {
                binding.bottomNavigation.updatePadding(
                    left = navigationPaddingLeft + navigationBars.left,
                    top = navigationPaddingTop,
                    right = navigationPaddingRight + navigationBars.right,
                    bottom = navigationPaddingBottom + navigationBars.bottom
                )
            }
            favouriteLayoutParams.rightMargin = favouriteMarginRight + navigationBars.right
            favouriteLayoutParams.bottomMargin = favouriteMarginBottom + navigationBars.bottom
            binding.setFavourite.layoutParams = favouriteLayoutParams

            windowInsets
        }
        ViewCompat.requestApplyInsets(binding.root)
    }

    public override fun onDestroy() {
        mapTimer.stop()
        unregisterReceiver(bcr)
        super.onDestroy()
    }

    private fun setupBackNavigation() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                    return
                }

                if (isSelectedMapUrl(binding.webView.url)) {
                    finish()
                    return
                }

                binding.webView.loadUrl(currentUrl)
            }
        })
    }

    private fun isSelectedMapUrl(url: String?): Boolean =
        normalizeUrl(url) == normalizeUrl(currentUrl)

    private fun openLinkExternallyIfNeeded(url: String?): Boolean {
        if (!prefs.openLinksInExternalBrowser || isSelectedMapUrl(url) || url.isNullOrBlank()) {
            return false
        }

        return try {
            startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
            true
        } catch (_: ActivityNotFoundException) {
            false
        }
    }

    private fun normalizeUrl(url: String?): String? =
        url
            ?.substringBefore('#')
            ?.substringBefore('?')
            ?.trimEnd('/')

    override fun onSaveInstanceState(outState: Bundle) {
        binding.webView.saveState(outState)
        return super.onSaveInstanceState(outState)
    }

    private fun loadPage() {
        if (isNetworkAvailable) {
            currentUrl = FavouritesRepository(this).getFavouriteLines().firstOrNull()?.url
                ?: Config.URL
            binding.webView.loadUrl(currentUrl)
            updateFavouriteIcon()
            showInitDialog()
        } else {
            showNoInternetSnackbar()
        }
    }

    private fun migrateFavouriteMap() {
        if (prefs.favouriteMapMigrated) {
            return
        }

        prefs.favouriteMapToMigrate()?.let { favouriteMap ->
            val oldFavouriteMap = normalizeUrl(favouriteMap) ?: Config.URL
            val repository = FavouritesRepository(this)
            if (!repository.isFavouriteLine(oldFavouriteMap)) {
                repository.addFavouriteLine(
                    FavouriteLine(
                        title = oldFavouriteMap.toFavouriteLineTitle(),
                        url = oldFavouriteMap
                    )
                )
            }
        }
        prefs.clearFavouriteMap()
        prefs.favouriteMapMigrated = true
    }

    private fun applyMapThemePreference() {
        binding.webView.applyThemePreference(prefs.darkMode)
    }

    private fun updateFavouriteIcon() {
        val icon = if (FavouritesRepository(this).isFavouriteLine(normalizeUrl(currentUrl) ?: currentUrl)) {
            R.drawable.ic_favorite_white_48dp
        } else {
            R.drawable.ic_favorite_border_white_48dp
        }
        binding.setFavourite.setImageResource(icon)
    }

    private fun toggleFavouriteLine() {
        val line = currentFavouriteLine()
        val repository = FavouritesRepository(this)
        if (repository.isFavouriteLine(line.url)) {
            repository.removeFavouriteLine(line.url)
            showBar(R.string.remove_favourite_line_success)
        } else {
            if (!premiumLimits.canAddFavouriteLine(repository.getFavouriteLines().size, isPremiumUnlocked())) {
                showDialog(
                    PremiumDialog(this) {
                        premiumUnlockedAfterPurchase = true
                    }
                )
                return
            }
            repository.addFavouriteLine(line)
            showBar(R.string.add_favourite_line_success)
        }
    }

    private fun isPremiumUnlocked(): Boolean =
        premiumUnlockedAfterPurchase || billingClient.isPremiumUnlocked()

    private fun currentFavouriteLine(): FavouriteLine {
        val url = normalizeUrl(currentUrl) ?: Config.URL
        return FavouriteLine(
            title = url.toFavouriteLineTitle(),
            url = url
        )
    }

    private fun String.toFavouriteLineTitle(): String {
        if (this == Config.URL) {
            return getString(R.string.default_map)
        }

        if (startsWith(Config.LINE_URL)) {
            val lineNumber = removePrefix(Config.LINE_URL)
                .trimEnd('/')
                .substringAfterLast('/')
            if (lineNumber.isNotBlank()) {
                return getString(R.string.favourite_line_title, lineNumber)
            }
        }

        return getString(R.string.map)
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
                val fineLocationIndex = permissions.indexOf(Manifest.permission.ACCESS_FINE_LOCATION)
                val coarseLocationIndex = permissions.indexOf(Manifest.permission.ACCESS_COARSE_LOCATION)
                val isLocationGranted = fineLocationIndex >= 0 &&
                    grantResults.getOrNull(fineLocationIndex) == PackageManager.PERMISSION_GRANTED ||
                    coarseLocationIndex >= 0 &&
                    grantResults.getOrNull(coarseLocationIndex) == PackageManager.PERMISSION_GRANTED

                mGeoLocationCallback?.invoke(mGeoLocationRequestOrigin, isLocationGranted, false)
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

    private fun openFavouritesDialog() {
        showDialog(
            FavouritesDialog(
                activity = this,
                onLineSelected = { line ->
                    currentUrl = line.url
                    binding.webView.loadUrl(line.url)
                    updateFavouriteIcon()
                }
            )
        )
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

        val menuLocation = IntArray(2)
        val anchorLocation = IntArray(2)
        val rootLocation = IntArray(2)
        binding.bottomNavigation.getLocationOnScreen(menuLocation)
        binding.navMore.getLocationOnScreen(anchorLocation)
        binding.root.getLocationOnScreen(rootLocation)

        val margin = resources.getDimensionPixelSize(R.dimen.more_menu_margin)
        val x: Int
        val y: Int
        if (isLandscape) {
            x = menuLocation[0] - rootLocation[0] + binding.bottomNavigation.width + margin / 2
            y = menuLocation[1] - rootLocation[1] + binding.bottomNavigation.height - popupView.measuredHeight - margin
        } else {
            x = anchorLocation[0] - rootLocation[0] + binding.navMore.width - popupView.measuredWidth
            y = anchorLocation[1] - rootLocation[1] - popupView.measuredHeight - margin
        }
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
