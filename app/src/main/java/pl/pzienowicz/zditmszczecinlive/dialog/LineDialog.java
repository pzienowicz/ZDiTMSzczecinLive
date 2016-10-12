package pl.pzienowicz.zditmszczecinlive.dialog;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pl.pzienowicz.zditmszczecinlive.Config;
import pl.pzienowicz.zditmszczecinlive.R;
import pl.pzienowicz.zditmszczecinlive.data.Lines;
import pl.pzienowicz.zditmszczecinlive.model.Line;

public class LineDialog extends Dialog {

    private Context context = null;
    private SharedPreferences sharedPreferences = null;
    private Map<Integer, Line> linesMap = new HashMap<>();
    private int currentLine = 0;

    public LineDialog(Context context) {
        super(context);
        this.context = context;

        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_line);

        TableLayout tramNormalLayout = (TableLayout) findViewById(R.id.tramNormalLayout);
        TableLayout busNormalLayout = (TableLayout) findViewById(R.id.busNormalLayout);
        TableLayout busExpressLayout = (TableLayout) findViewById(R.id.busExpressLayout);
        TableLayout busNightLayout = (TableLayout) findViewById(R.id.busNightLayout);

        TextView clearFilterText = (TextView) findViewById(R.id.clearFilterText);
        clearFilterText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFilter(0);
            }
        });

        currentLine = sharedPreferences.getInt(Config.PREFERENCE_SELECTED_LINE, 0);

        drawLinesTable(Lines.getTramNormal(), tramNormalLayout);
        drawLinesTable(Lines.getBusNormal(), busNormalLayout);
        drawLinesTable(Lines.getBusExpress(), busExpressLayout);
        drawLinesTable(Lines.getBusNight(), busNightLayout);
    }

    private void drawLinesTable(ArrayList<Line> lines, TableLayout layout)
    {
        int iterator = 0;

        int linesPerRow = Config.LINES_PER_ROW;
        if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            linesPerRow = Config.ZONES_PER_ROW_LANDSCAPE;
        }
        int rows = (lines.size() % linesPerRow == 0) ? lines.size() / linesPerRow : lines.size() / linesPerRow + 1;

        for (int i = 1; i <= rows; i++) {
            TableRow row = new TableRow(context);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            for (int j = 1; j <= linesPerRow; j++) {
                if(iterator >= lines.size()) {
                    continue;
                }

                Line line = lines.get(iterator);

                LinearLayout cellLayout = new LinearLayout(context);
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                params.setMargins(2, 2, 2, 3);
                cellLayout.setLayoutParams(params);
                cellLayout.setClickable(true);
                cellLayout.setFocusable(true);
                cellLayout.setId(lines.get(iterator).getId());

                if(currentLine == line.getId()) {
                    cellLayout.setBackgroundColor(context.getResources().getColor(R.color.yellow));
                } else {
                    cellLayout.setBackgroundResource(R.drawable.selector_line);
                }

                cellLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeFilter(view.getId());
                    }
                });

                TextView tv = new TextView(context);

                tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                tv.setGravity(Gravity.CENTER);
//                tv.setTextColor(getResources().getColor(R.color.white));
                tv.setText(lines.get(iterator).getName());

                cellLayout.addView(tv);
                row.addView(cellLayout);

                linesMap.put(line.getId(), line);

                iterator++;
            }

            layout.addView(row);
        }
    }

    private void changeFilter(int id)
    {
        sharedPreferences.edit().putInt(Config.PREFERENCE_SELECTED_LINE, id).apply();

        Intent intent = new Intent(Config.INTENT_LOAD_NEW_URL);
        intent.putExtra(Config.EXTRA_LINE_ID, id);
        context.sendBroadcast(intent);
        dismiss();
    }
}
