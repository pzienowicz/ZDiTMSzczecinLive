package pl.pzienowicz.zditmszczecinlive.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.Window
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton

import pl.pzienowicz.zditmszczecinlive.Config
import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.model.BusStop

@SuppressLint("SetJavaScriptEnabled")
class ScheduleBoardDialog(context: Context, busStop: BusStop) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_board)

        findViewById<WebView>(R.id.webView)
            .apply {
                webViewClient = object : WebViewClient() {}
                settings.javaScriptEnabled = true
                loadUrl(Config.BUS_STOP_URL + busStop.id)
            }

        findViewById<ImageButton>(R.id.closeBtn)
            .setOnClickListener { dismiss() }
    }
}
