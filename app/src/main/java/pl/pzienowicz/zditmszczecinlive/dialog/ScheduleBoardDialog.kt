package pl.pzienowicz.zditmszczecinlive.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.widget.FrameLayout
import android.webkit.WebViewClient
import com.google.android.material.R as MaterialR
import com.google.android.material.bottomsheet.BottomSheetBehavior

import pl.pzienowicz.zditmszczecinlive.Config
import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.databinding.DialogBoardBinding
import pl.pzienowicz.zditmszczecinlive.model.BusStop

@SuppressLint("SetJavaScriptEnabled")
class ScheduleBoardDialog(context: Context, busStop: BusStop) : AdaptiveSheetDialog(context) {

    private var binding: DialogBoardBinding

    init {
        binding = DialogBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setOnShowListener {
            findViewById<FrameLayout>(MaterialR.id.design_bottom_sheet)
                ?.let { bottomSheet ->
                    bottomSheet.setBackgroundResource(R.drawable.bg_bottom_sheet)
                    bottomSheet.layoutParams = bottomSheet.layoutParams.apply {
                        height = (context.resources.displayMetrics.heightPixels * 0.9f).toInt()
                    }

                    BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
                }
        }

        binding.webView
            .apply {
                webViewClient = object : WebViewClient() {}
                settings.javaScriptEnabled = true
                loadUrl(Config.BUS_STOP_URL + busStop.id)
            }

        binding.closeBtn.setOnClickListener { dismiss() }
    }
}
