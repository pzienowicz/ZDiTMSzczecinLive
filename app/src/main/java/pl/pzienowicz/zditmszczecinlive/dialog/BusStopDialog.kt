package pl.pzienowicz.zditmszczecinlive.dialog

import android.app.Activity
import android.app.Dialog
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast

import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.data.BusStops
import pl.pzienowicz.zditmszczecinlive.listener.BusStopSelectedListener

class BusStopDialog(activity: Activity, listener: BusStopSelectedListener, currentBusStop: String?) : Dialog(activity) {

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
                Toast.makeText(context, R.string.incorrect_bus_stop, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            listener.onBusStopSelected(busStop)
        }

        findViewById<Button>(R.id.cancelBtn).setOnClickListener {
            dismiss()
        }

        findViewById<Button>(R.id.scanCodeBtn).setOnClickListener {
            dismiss()

            val dialog = ScanBusStopDialog(activity, listener)
            dialog.window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            dialog.show()
        }
    }
}
