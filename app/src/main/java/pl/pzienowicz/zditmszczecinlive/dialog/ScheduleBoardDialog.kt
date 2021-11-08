package pl.pzienowicz.zditmszczecinlive.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.Window
import android.webkit.WebViewClient

import pl.pzienowicz.zditmszczecinlive.Config
import pl.pzienowicz.zditmszczecinlive.databinding.DialogBoardBinding
import pl.pzienowicz.zditmszczecinlive.model.BusStop

@SuppressLint("SetJavaScriptEnabled")
class ScheduleBoardDialog(context: Context, busStop: BusStop) : Dialog(context) {

    private var binding: DialogBoardBinding

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webView
            .apply {
                webViewClient = object : WebViewClient() {}
                settings.javaScriptEnabled = true
                loadUrl(Config.BUS_STOP_URL + busStop.id)
            }

        binding.closeBtn.setOnClickListener { dismiss() }
    }
}
