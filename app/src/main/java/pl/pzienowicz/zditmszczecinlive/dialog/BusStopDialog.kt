package pl.pzienowicz.zditmszczecinlive.dialog

import android.app.Activity
import android.app.Dialog
import android.view.Window

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
) : Dialog(activity) {

    private var binding: DialogBusStopBinding

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogBusStopBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
