package pl.pzienowicz.zditmszczecinlive.adapter

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat

import de.codecrafters.tableview.TableDataAdapter
import pl.pzienowicz.zditmszczecinlive.Config
import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.data.Widget

class WidgetTableDataAdapter(context: Context, data: List<Widget>)
    : TableDataAdapter<Widget>(context, data) {

    override fun getCellView(rowIndex: Int, columnIndex: Int, parentView: ViewGroup): View {
        val widget = getRowData(rowIndex)
        val textView = TextView(context)
        textView.setPadding(20, 20, 20, 20)
        textView.setTextColor(ContextCompat.getColor(context, R.color.black))

        when (columnIndex) {
            0 -> textView.text = (rowIndex+1).toString()
            1 -> if(widget.busStop != null) {
                textView.text = widget.busStop.number
            }
            2 -> if(widget.busStop != null) {
                textView.text = widget.busStop.name
            }
            3 -> {
                val imageView = ImageView(context)
                imageView.setImageResource(R.drawable.ic_edit_black_24dp)
                imageView.setOnClickListener {
                    val intent = Intent(Config.INTENT_OPEN_BUS_STOP_EDIT)
                    intent.putExtra("widgetId", widget.widgetId)
                    context.sendBroadcast(intent)
                }
                return imageView
            }
        }

        return textView
    }
}
