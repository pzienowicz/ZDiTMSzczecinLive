package pl.pzienowicz.zditmszczecinlive.dialog

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.support.design.widget.Snackbar
import android.view.Window
import android.widget.Toast
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import org.acra.ACRA
import pl.pzienowicz.zditmszczecinlive.BuildConfig

import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.data.BusStops
import pl.pzienowicz.zditmszczecinlive.listener.BusStopSelectedListener

class ScanBusStopDialog(val activity: Activity, val listener: BusStopSelectedListener) : Dialog(activity) {

    var barcodeView: CompoundBarcodeView? = null

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_scan_bus_stop)

        checkPermission()

        findViewById(R.id.cancelBtn).setOnClickListener({
            dismiss()
        })
    }

    private fun initBarcodeView() {
        barcodeView = findViewById(R.id.zxing_barcode_scanner) as CompoundBarcodeView
        barcodeView!!.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                barcodeView!!.pause()

                val busStopUrl = result!!.text
                var busStopNumber = ""

                try {
                    busStopNumber = busStopUrl.substring(60)
                } catch (e: StringIndexOutOfBoundsException) {
                    ACRA.getErrorReporter().putCustomData("url", busStopUrl)
                    ACRA.getErrorReporter().handleException(e)
                }

                if(BuildConfig.DEBUG) {
                    Toast.makeText(activity, busStopUrl, Toast.LENGTH_LONG).show()
                }

                val busStop = BusStops.getInstance(context).getById(busStopNumber)
                if (busStop == null) {
                    Toast.makeText(context, R.string.incorrect_bus_stop, Toast.LENGTH_LONG).show()
                    barcodeView!!.resume()
                    return
                }

                dismiss()
                listener.onBusStopSelected(busStop)
            }

            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {}
        })
        barcodeView!!.resume()
    }

    private fun checkPermission() {
        Dexter
                .withActivity(activity)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(object : PermissionListener {
                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                        token!!.continuePermissionRequest()
                    }

                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        initBarcodeView()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        Snackbar.make(activity.findViewById(R.id.swiperefresh), R.string.camera_permission_description, Snackbar.LENGTH_LONG).show()
                        dismiss()
                    }
                })
                .check()
    }
}
