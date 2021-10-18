package pl.pzienowicz.zditmszczecinlive.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.Window
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

        findViewById<WebView>(R.id.webView)
            .apply {
                webViewClient = object : WebViewClient() {}
                settings.javaScriptEnabled = true
                loadUrl(camera.url)
            }

        findViewById<ImageButton>(R.id.closeBtn)
            .setOnClickListener { dismiss() }
    }
}