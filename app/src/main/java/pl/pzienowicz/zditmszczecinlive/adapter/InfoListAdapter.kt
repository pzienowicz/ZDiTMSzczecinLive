package pl.pzienowicz.zditmszczecinlive.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import java.util.ArrayList

import pl.pzienowicz.zditmszczecinlive.R
import pl.pzienowicz.zditmszczecinlive.inflater
import pl.pzienowicz.zditmszczecinlive.model.Info

class InfoListAdapter(
    private val context: Context,
    private val records: ArrayList<Info>
    ) : BaseAdapter() {

    override fun getCount(): Int = records.size

    override fun getItem(position: Int): Any = records[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val info = records[position]
        val row: View = convertView
            ?: context.inflater.inflate(R.layout.row_info, parent, false)

        val infoTextView = row.findViewById<TextView>(R.id.infoText)
        infoTextView.text = info.description.pl

        val fromDateTv = row.findViewById<TextView>(R.id.fromDate)
        fromDateTv.text = context.getString(R.string.from_date, info.fromDate)

        val toDateTv = row.findViewById<TextView>(R.id.toDate)
        if (info.valid_to != null) {
            toDateTv.visibility = View.VISIBLE
            toDateTv.text = context.getString(R.string.to_date, info.toDate)
        } else {
            toDateTv.visibility = View.GONE
        }

        return row
    }
}
