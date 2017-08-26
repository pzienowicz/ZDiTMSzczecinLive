package pl.pzienowicz.zditmszczecinlive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pl.pzienowicz.zditmszczecinlive.R;
import pl.pzienowicz.zditmszczecinlive.model.Info;

public class InfoListAdapter extends BaseAdapter {

    private Context context = null;
    private ArrayList<Info> records = null;

    public InfoListAdapter(Context context, ArrayList<Info> records) {
        this.context = context;
        this.records = records;
    }

    @Override
    public int getCount() {
        return records.size();
    }

    @Override
    public Object getItem(int position) {
        return records.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row;
        final Info info = records.get(position);

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.row_info, parent, false);
        } else {
            row = convertView;
        }

        TextView infoTextView = (TextView) row.findViewById(R.id.infoText);
        infoTextView.setText(info.getDescription());

        TextView fromDateTv = (TextView) row.findViewById(R.id.fromDate);
        fromDateTv.setText(context.getString(R.string.from_date, info.getFromDate()));

        TextView toDateTv = (TextView) row.findViewById(R.id.toDate);
        if(info.getTo() != null) {
            toDateTv.setVisibility(View.VISIBLE);
            toDateTv.setText(context.getString(R.string.to_date, info.getToDate()));
        }

        return row;
    }
}
