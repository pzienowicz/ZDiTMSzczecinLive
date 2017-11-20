package pl.pzienowicz.zditmszczecinlive.dialog

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.webkit.WebView
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

        val closeBtn = findViewById(R.id.closeBtn) as ImageButton
        closeBtn.setOnClickListener { dismiss() }
    }
}
