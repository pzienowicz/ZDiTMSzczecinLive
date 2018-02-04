package pl.pzienowicz.zditmszczecinlive.dialog

import android.app.AlertDialog.Builder
import android.content.Context
import android.content.DialogInterface
import android.text.InputType
import android.widget.EditText
import android.widget.Toast

import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.data.BusStops
import pl.pzienowicz.zditmszczecinlive.listener.BusStopSelectedListener

class BusStopDialog(context: Context, listener: BusStopSelectedListener, currentBusStop: String?) : Builder(context) {

    init {
        val txtUrl = EditText(context)
        txtUrl.inputType = InputType.TYPE_CLASS_NUMBER

        setTitle(R.string.type_bus_stop_number)
        setMessage(R.string.bus_stop_description)
        setView(txtUrl)

        if(currentBusStop != null) {
            txtUrl.setText(currentBusStop)
        }

        setPositiveButton(R.string.ok, DialogInterface.OnClickListener { dialog, which ->
            val busStopNumber = txtUrl.text.toString()

            val busStop = BusStops.getInstance(context).getByNumber(busStopNumber)
            if (busStop == null) {
                Toast.makeText(context, R.string.incorrect_bus_stop, Toast.LENGTH_LONG).show()
                return@OnClickListener
            }

            listener.onBusStopSelected(busStop)
        })

        setNegativeButton(R.string.cancel) { dialog, which -> dialog.dismiss() }
    }
}
