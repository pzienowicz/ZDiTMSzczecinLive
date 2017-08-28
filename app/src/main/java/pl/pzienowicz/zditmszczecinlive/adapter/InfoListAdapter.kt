package pl.pzienowicz.zditmszczecinlive.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import java.util.ArrayList

import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.model.Info

class InfoListAdapter(context: Context, records: ArrayList<Info>) : BaseAdapter() {

    private val context: Context = context
    private val records: ArrayList<Info> = records

    override fun getCount(): Int {
        return records.size
    }

    override fun getItem(position: Int): Any {
        return records[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val row: View
        val info = records[position]

        if (convertView == null) {
            val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            row = inflater.inflate(R.layout.row_info, parent, false)
        } else {
            row = convertView
        }

        val infoTextView = row.findViewById(R.id.infoText) as TextView
        infoTextView.text = info.description

        val fromDateTv = row.findViewById(R.id.fromDate) as TextView
        fromDateTv.text = context!!.getString(R.string.from_date, info.fromDate)

        val toDateTv = row.findViewById(R.id.toDate) as TextView
        if (info.to != null) {
            toDateTv.visibility = View.VISIBLE
            toDateTv.text = context.getString(R.string.to_date, info.toDate)
        }

        return row
    }
}
