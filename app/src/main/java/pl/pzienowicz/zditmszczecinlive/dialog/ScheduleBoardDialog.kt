package pl.pzienowicz.zditmszczecinlive.dialog

import android.app.Dialog
import android.content.Context
import android.net.http.SslError
import android.view.Window
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton

import pl.pzienowicz.zditmszczecinlive.Config
import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.model.BusStop

class ScheduleBoardDialog(context: Context, busStop: BusStop) : Dialog(context) {

    init {

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_board)

        val webView = findViewById(R.id.webView) as WebView
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(Config.BUS_STOP_URL + busStop.id)
        webView.setWebViewClient(object: WebViewClient() {
            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                handler?.proceed()
            }
        })

        val closeBtn = findViewById(R.id.closeBtn) as ImageButton
        closeBtn.setOnClickListener { dismiss() }
    }
}
