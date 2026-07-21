package pl.pzienowicz.zditmszczecinlive.dialog

import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.R as MaterialR
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import pl.pzienowicz.zditmszczecinlive.R

open class AdaptiveSheetDialog(context: Context) : BottomSheetDialog(context) {

    private var onShowListener: DialogInterface.OnShowListener? = null
    private var sheetWidth: Int? = null
    private var sheetHeight: Int? = null
    private var bottomSheetCallback: BottomSheetBehavior.BottomSheetCallback? = null

    init {
        super.setOnShowListener {
            onShowListener?.onShow(this)
            configureBottomSheet()
        }
    }

    override fun show() {
        super.show()
        applySheetLayout()
    }

    override fun setOnShowListener(listener: DialogInterface.OnShowListener?) {
        onShowListener = listener
    }

    fun setSheetLayout(width: Int, height: Int) {
        sheetWidth = width
        sheetHeight = height
        applySheetLayout()
    }

    private fun applySheetLayout() {
        val width = sheetWidth ?: return
        val height = sheetHeight ?: return

        findViewById<FrameLayout>(MaterialR.id.design_bottom_sheet)?.let { bottomSheet ->
            bottomSheet.layoutParams = bottomSheet.layoutParams.apply {
                this.width = width
                this.height = height
            }
        }
    }

    private fun setBottomSheetBackground(sheet: View) {
        sheet.post { sheet.setBackgroundResource(R.drawable.bg_bottom_sheet) }
    }

    private fun configureBottomSheet() {
        findViewById<FrameLayout>(MaterialR.id.design_bottom_sheet)?.let { bottomSheet ->
            BottomSheetBehavior.from(bottomSheet).apply {
                bottomSheetCallback?.let { removeBottomSheetCallback(it) }
                skipCollapsed = true
                state = BottomSheetBehavior.STATE_EXPANDED
                setBottomSheetBackground(bottomSheet)
                bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(sheet: View, newState: Int) {
                        setBottomSheetBackground(sheet)
                    }

                    override fun onSlide(sheet: View, slideOffset: Float) = Unit
                }.also { addBottomSheetCallback(it) }
            }
        }
    }
}
