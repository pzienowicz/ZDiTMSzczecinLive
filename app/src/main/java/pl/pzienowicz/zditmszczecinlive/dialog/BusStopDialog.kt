package pl.pzienowicz.zditmszczecinlive.dialog

import android.app.Activity
import android.view.View
import com.google.android.material.R as MaterialR
import com.google.android.material.bottomsheet.BottomSheetDialog

import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.data.BusStops
import pl.pzienowicz.zditmszczecinlive.databinding.DialogBusStopBinding
import pl.pzienowicz.zditmszczecinlive.model.BusStop
import pl.pzienowicz.zditmszczecinlive.setFullWidth
import pl.pzienowicz.zditmszczecinlive.showToast

class BusStopDialog(
    activity: Activity,
    onSelected: (busStop: BusStop) -> Unit,
    currentBusStop: String?
) : BottomSheetDialog(activity) {

    private var binding: DialogBusStopBinding

    init {
        binding = DialogBusStopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setOnShowListener {
            findViewById<View>(MaterialR.id.design_bottom_sheet)
                ?.setBackgroundResource(R.drawable.bg_bottom_sheet)
        }

        val txtUrl = binding.numberInput

        currentBusStop?.let {
            txtUrl.setText(it)
        }

        binding.okBtn.setOnClickListener {
            dismiss()

            val busStopNumber = txtUrl.text.toString()

            BusStops.getInstance(context)
                .loadByNumber(busStopNumber, callback = { busStop ->
                    if (busStop == null) {
                        context.showToast(R.string.incorrect_bus_stop)
                        return@loadByNumber
                    }
                    onSelected(busStop)
                })
        }

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }

        binding.scanCodeBtn.setOnClickListener {
            dismiss()

            val dialog = ScanBusStopDialog(activity, onSelected)
            dialog.setFullWidth()
            dialog.show()
        }
    }
}
