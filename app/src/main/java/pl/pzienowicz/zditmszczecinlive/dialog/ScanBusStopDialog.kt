package pl.pzienowicz.zditmszczecinlive.dialog

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.view.Window
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.data.BusStops
import pl.pzienowicz.zditmszczecinlive.databinding.DialogScanBusStopBinding
import pl.pzienowicz.zditmszczecinlive.model.BusStop
import pl.pzienowicz.zditmszczecinlive.showBar
import pl.pzienowicz.zditmszczecinlive.showToast

class ScanBusStopDialog(
    val activity: Activity,
    val onSelected: (busStop: BusStop) -> Unit
) : Dialog(activity) {

    private var binding: DialogScanBusStopBinding

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogScanBusStopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermission()

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }
    }

    override fun dismiss() {
        binding.zxingBarcodeScanner.pause()
        super.dismiss()
    }

    private fun initBarcodeView() {
        binding.zxingBarcodeScanner.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                binding.zxingBarcodeScanner.pause()

                try {
                    val busStopUrl = result!!.text
                    val busStopId = busStopUrl.substring(60)
                    val busStopNumber = busStopUrl.substring(26)

                    BusStops.getInstance(context)
                        .loadByIdOrNumber(busStopId, busStopNumber) { busStop ->
                            if (busStop == null) {
                                context.showToast(R.string.incorrect_bus_stop)
                                binding.zxingBarcodeScanner.resume()

//                              ACRA.getErrorReporter().putCustomData("busStopId", busStopId)
//                              ACRA.getErrorReporter().putCustomData("busStopNumber", busStopNumber)
//                              ACRA.getErrorReporter().putCustomData("busStopUrl", busStopUrl)
//                              ACRA.getErrorReporter().handleException(IncorrectBusStopException())
                                return@loadByIdOrNumber
                            }

                            dismiss()
                            onSelected(busStop)
                        }
                } catch (e: StringIndexOutOfBoundsException) {}
            }

            override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {}
        })
        binding.zxingBarcodeScanner.resume()
    }

    private fun checkPermission() {
        Dexter
                .withContext(activity)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(object : PermissionListener {
                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                        token!!.continuePermissionRequest()
                    }

                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        initBarcodeView()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        activity.showBar(R.string.camera_permission_description)
                        dismiss()
                    }
                })
                .check()
    }
}
