package pl.pzienowicz.zditmszczecinlive.dialog

import android.app.Activity
import android.view.View

import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.data.BusStops
import pl.pzienowicz.zditmszczecinlive.databinding.DialogBusStopBinding
import pl.pzienowicz.zditmszczecinlive.model.BusStop
import pl.pzienowicz.zditmszczecinlive.setFullWidth

class BusStopDialog(
    activity: Activity,
    onSelected: (busStop: BusStop) -> Unit,
    currentBusStop: String?
) : AdaptiveSheetDialog(activity) {

    private var binding: DialogBusStopBinding

    init {
        binding = DialogBusStopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val txtUrl = binding.numberInput

        currentBusStop?.let {
            txtUrl.setText(it)
        }

        binding.okBtn.setOnClickListener {
            val busStopNumber = txtUrl.text.toString()
            binding.errorText.visibility = View.GONE

            BusStops.getInstance(context)
                .loadByNumber(
                    busStopNumber,
                    onError = { showError(R.string.stops_request_error) },
                    callback = { busStop ->
                        if (busStop == null) {
                            showError(R.string.incorrect_bus_stop)
                            return@loadByNumber
                        }
                        dismiss()
                        onSelected(busStop)
                    }
                )
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

    private fun showError(message: Int) {
        binding.errorText.setText(message)
        binding.errorText.visibility = View.VISIBLE
    }
}
