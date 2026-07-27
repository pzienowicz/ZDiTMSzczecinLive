package pl.pzienowicz.zditmszczecinlive.dialog

import android.Manifest
import android.app.Activity
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

class ScanBusStopDialog(
    val activity: Activity,
    val onSelected: (busStop: BusStop) -> Unit
) : AdaptiveSheetDialog(activity) {

    private var binding: DialogScanBusStopBinding

    init {
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

                val busStopUrl = result!!.text
                var busStopId = ""
                var busStopNumber = ""

                try {
                    busStopNumber = busStopUrl.substring(26)
                    busStopId = busStopUrl.substring(60)
                } catch (e: StringIndexOutOfBoundsException) {}

                BusStops.getInstance(context)
                    .loadByIdOrNumber(
                        busStopId,
                        busStopNumber,
                        onError = { showError(R.string.stops_request_error) }
                    ) { busStop ->
                        if (busStop == null) {
                            showError(R.string.incorrect_bus_stop)
                            binding.zxingBarcodeScanner.resume()

                            return@loadByIdOrNumber
                        }
                        dismiss()
                        onSelected(busStop)
                    }
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
                        showError(R.string.camera_permission_description)
                    }
                })
                .check()
    }

    private fun showError(message: Int) {
        binding.errorText.setText(message)
        binding.errorText.visibility = android.view.View.VISIBLE
    }
}
