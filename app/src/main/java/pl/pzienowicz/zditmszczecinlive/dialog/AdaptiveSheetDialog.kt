package pl.pzienowicz.zditmszczecinlive.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.google.android.material.R as MaterialR
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.sidesheet.SideSheetDialog
import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.isLandscape

open class AdaptiveSheetDialog(context: Context) : Dialog(context) {

    private val useSideSheet = SIDE_SHEET_ENABLED && context.isLandscape
    private val delegate: Dialog = if (useSideSheet) {
        SideSheetDialog(context)
    } else {
        BottomSheetDialog(context)
    }
    private var onShowListener: DialogInterface.OnShowListener? = null
    private var sheetWidth: Int? = null
    private var sheetHeight: Int? = null

    val isSideSheet = useSideSheet

    init {
        delegate.setOnShowListener {
            setSheetBackground()
            onShowListener?.onShow(this)
        }
    }

    override fun show() {
        delegate.show()
        applySheetLayout()
    }

    override fun dismiss() {
        delegate.dismiss()
    }

    override fun cancel() {
        delegate.cancel()
    }

    override fun isShowing(): Boolean = delegate.isShowing

    override fun setContentView(layoutResID: Int) {
        delegate.setContentView(layoutResID)
        configureSideSheetEdge()
    }

    override fun setContentView(view: View) {
        delegate.setContentView(view)
        configureSideSheetEdge()
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams?) {
        delegate.setContentView(view, params)
        configureSideSheetEdge()
    }

    override fun setOnShowListener(listener: DialogInterface.OnShowListener?) {
        onShowListener = listener
    }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        delegate.setOnDismissListener(listener)
    }

    override fun setOnCancelListener(listener: DialogInterface.OnCancelListener?) {
        delegate.setOnCancelListener(listener)
    }

    override fun setCancelable(flag: Boolean) {
        delegate.setCancelable(flag)
    }

    override fun setCanceledOnTouchOutside(cancel: Boolean) {
        delegate.setCanceledOnTouchOutside(cancel)
    }

    override fun <T : View?> findViewById(id: Int): T? = delegate.findViewById(id)

    override fun getWindow(): Window? = delegate.window

    fun setSheetLayout(width: Int, height: Int) {
        sheetWidth = width
        sheetHeight = height
        applySheetLayout()
    }

    private fun applySheetLayout() {
        val width = sheetWidth ?: return
        val height = sheetHeight ?: return
        delegate.window?.setLayout(width, height)
    }

    private fun setSheetBackground() {
        findViewById<View>(MaterialR.id.design_bottom_sheet)
            ?.setBackgroundResource(R.drawable.bg_bottom_sheet)
        findViewById<View>(MaterialR.id.m3_side_sheet)
            ?.setBackgroundResource(R.drawable.bg_side_sheet)
    }

    private fun configureSideSheetEdge() {
        (delegate as? SideSheetDialog)?.setSheetEdge(SHEET_EDGE_LEFT)
    }

    private companion object {
        private const val SIDE_SHEET_ENABLED = false
        private const val SHEET_EDGE_LEFT = Gravity.LEFT
    }
}
