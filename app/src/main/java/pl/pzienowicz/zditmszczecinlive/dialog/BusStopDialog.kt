package pl.pzienowicz.zditmszczecinlive.dialog

import android.app.Activity
import android.app.Dialog
import android.view.Window
import android.widget.Button
import android.widget.EditText

import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.data.BusStops
import pl.pzienowicz.zditmszczecinlive.model.BusStop
import pl.pzienowicz.zditmszczecinlive.setFullWidth
import pl.pzienowicz.zditmszczecinlive.showToast

class BusStopDialog(
    activity: Activity,
    onSelected: (busStop: BusStop) -> Unit,
    currentBusStop: String?
) : Dialog(activity) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_bus_stop)

        val txtUrl = findViewById<EditText>(R.id.numberInput)

        currentBusStop?.let {
            txtUrl.setText(it)
        }

        findViewById<Button>(R.id.okBtn).setOnClickListener {
            dismiss()

            val busStopNumber = txtUrl.text.toString()

            val busStop = BusStops.getInstance(context).getByNumber(busStopNumber)
            if (busStop == null) {
                context.showToast(R.string.incorrect_bus_stop)
                return@setOnClickListener
            }
            onSelected(busStop)
        }

        findViewById<Button>(R.id.cancelBtn).setOnClickListener {
            dismiss()
        }

        findViewById<Button>(R.id.scanCodeBtn).setOnClickListener {
            dismiss()

            val dialog = ScanBusStopDialog(activity, onSelected)
            dialog.setFullWidth()
            dialog.show()
        }
    }
}
