package pl.pzienowicz.zditmszczecinlive.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.Window
import android.webkit.WebViewClient
import pl.pzienowicz.zditmszczecinlive.databinding.DialogCameraBinding
import pl.pzienowicz.zditmszczecinlive.model.Camera

@SuppressLint("SetJavaScriptEnabled")
class CameraDialog(context: Context, camera: Camera) : Dialog(context) {

    private var binding: DialogCameraBinding

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webView
            .apply {
                webViewClient = object : WebViewClient() {}
                settings.javaScriptEnabled = true
                loadUrl(camera.url)
            }

        binding.closeBtn.setOnClickListener { dismiss() }
    }
}