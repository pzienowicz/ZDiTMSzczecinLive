package pl.pzienowicz.zditmszczecinlive.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.pzienowicz.zditmszczecinlive.Config
import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.data.Widget
import pl.pzienowicz.zditmszczecinlive.sendLocalBroadcast

class WidgetTableDataAdapter(
    private val context: Context,
    private val data: List<Widget>
) : RecyclerView.Adapter<WidgetTableDataAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIndex: TextView = view.findViewById(R.id.tvIndex)
        val tvNumber: TextView = view.findViewById(R.id.tvNumber)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val ivEdit: ImageView = view.findViewById(R.id.ivEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_widget_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val widget = data[position]
        holder.tvIndex.text = (position + 1).toString()
        holder.tvNumber.text = widget.busStop?.number ?: ""
        holder.tvName.text = widget.busStop?.name ?: ""
        holder.ivEdit.setOnClickListener {
            val intent = Intent(Config.INTENT_OPEN_BUS_STOP_EDIT)
            intent.putExtra("widgetId", widget.widgetId)
            context.sendLocalBroadcast(intent)
        }
    }

    override fun getItemCount(): Int = data.size
}
