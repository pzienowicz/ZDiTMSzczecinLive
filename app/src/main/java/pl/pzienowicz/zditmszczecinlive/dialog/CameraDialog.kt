package pl.pzienowicz.zditmszczecinlive.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.net.http.SslError
import android.view.Window
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.model.Camera

@SuppressLint("SetJavaScriptEnabled")
class CameraDialog(context: Context, camera: Camera) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_camera)

        val webView = findViewById<WebView>(R.id.webView)
        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                handler!!.proceed()
            }
        }
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(camera.url)

        val closeBtn = findViewById<ImageButton>(R.id.closeBtn)
        closeBtn.setOnClickListener { dismiss() }
    }
}