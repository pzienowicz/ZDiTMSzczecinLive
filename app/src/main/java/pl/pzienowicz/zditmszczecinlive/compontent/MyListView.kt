package pl.pzienowicz.zditmszczecinlive.compontent

import android.content.Context
import android.util.AttributeSet
import android.widget.ListView

class MyListView(context: Context, attrs: AttributeSet) : ListView(context, attrs) {

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val expandSpec = MeasureSpec.makeMeasureSpec(
            Integer.MAX_VALUE shr 2,
                MeasureSpec.AT_MOST
        )
        super.onMeasure(widthMeasureSpec, expandSpec)
    }
}