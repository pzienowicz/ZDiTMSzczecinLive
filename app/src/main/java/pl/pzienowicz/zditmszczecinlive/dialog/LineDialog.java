package pl.pzienowicz.zditmszczecinlive.dialog;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.pzienowicz.zditmszczecinlive.Config;
import pl.pzienowicz.zditmszczecinlive.Functions;
import pl.pzienowicz.zditmszczecinlive.R;
import pl.pzienowicz.zditmszczecinlive.model.Line;
import pl.pzienowicz.zditmszczecinlive.rest.RetrofitClient;
import pl.pzienowicz.zditmszczecinlive.rest.ZDiTMService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LineDialog extends Dialog {

    private Context context = null;
    private SharedPreferences sharedPreferences = null;
    private Map<Integer, Line> linesMap = new HashMap<>();
    private int currentLine = 0;
    private FrameLayout progressBarHolder = null;

    public LineDialog(final Context context) {
        super(context);
        this.context = context;

        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_line);

        final TableLayout tramNormalTable = findViewById(R.id.tramNormalTable);
        final TableLayout busNormalTable = findViewById(R.id.busNormalTable);
        final TableLayout busExpressTable = findViewById(R.id.busExpressTable);
        final TableLayout busNightTable = findViewById(R.id.busNightTable);
        final TableLayout busSubstituteTable = findViewById(R.id.busSubstituteTable);
        final TableLayout tramSubstituteTable = findViewById(R.id.tramSubstituteTable);
        final TableLayout tramTouristicTable = findViewById(R.id.tramTouristicTable);
        final TableLayout busTouristicTable = findViewById(R.id.busTouristicTable);
        final TableLayout busNormalOnDemandTable = findViewById(R.id.busNormalOnDemandTable);
        final TableLayout tramExtraTable = findViewById(R.id.tramExtraTable);
        final TableLayout busExtraTable = findViewById(R.id.busExtraTable);

        final LinearLayout tramNormalLabel = findViewById(R.id.tramNormalLabel);
        final LinearLayout busNormalLabel = findViewById(R.id.busNormalLabel);
        final LinearLayout busExpressLabel = findViewById(R.id.busExpressLabel);
        final LinearLayout busNightLabel = findViewById(R.id.busNightLabel);
        final LinearLayout busSubstituteLabel = findViewById(R.id.busSubstituteLabel);
        final LinearLayout tramSubstituteLabel = findViewById(R.id.tramSubstituteLabel);
        final LinearLayout tramTouristicLabel = findViewById(R.id.tramTouristicLabel);
        final LinearLayout busTouristicLabel = findViewById(R.id.busTouristicLabel);
        final LinearLayout busNormalOnDemandLabel = findViewById(R.id.busNormalOnDemandLabel);
        final LinearLayout tramExtraLabel = findViewById(R.id.tramExtraLabel);
        final LinearLayout busExtraLabel = findViewById(R.id.busExtraLabel);
        
        progressBarHolder = findViewById(R.id.progressBarHolder);

        TextView clearFilterText = findViewById(R.id.clearFilterText);
        clearFilterText.setOnClickListener(view -> changeFilter(0));

        currentLine = sharedPreferences.getInt(Config.PREFERENCE_SELECTED_LINE, 0);

        if(!Functions.INSTANCE.isNetworkAvailable(context)) {
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_LONG).show();

            Intent intent = new Intent(Config.INTENT_NO_INTERNET_CONNETION);
            context.sendBroadcast(intent);

            dismiss();
            return;
        }

        progressBarHolder.setVisibility(View.VISIBLE);

        ZDiTMService service = RetrofitClient.INSTANCE.getRetrofit().create(ZDiTMService.class);
        Call<List<Line>> lines = service.listLines();
        lines.enqueue(new Callback<List<Line>>() {
            @Override
            public void onResponse(Call<List<Line>> call, Response<List<Line>> response) {
                progressBarHolder.setVisibility(View.GONE);

                if(response.isSuccessful() && response.body() != null) {
                    drawLinesTable(filterLines(response.body(), "tdz"), tramNormalTable, tramNormalLabel);
                    drawLinesTable(filterLines(response.body(), "adz"), busNormalTable, busNormalLabel);
                    drawLinesTable(filterLines(response.body(), "adp"), busExpressTable, busExpressLabel);
                    drawLinesTable(filterLines(response.body(), "anz"), busNightTable, busNightLabel);
                    drawLinesTable(filterLines(response.body(), "ada"), busSubstituteTable, busSubstituteLabel);
                    drawLinesTable(filterLines(response.body(), "tda"), tramSubstituteTable, tramSubstituteLabel);
                    drawLinesTable(filterLines(response.body(), "tdt"), tramTouristicTable, tramTouristicLabel);
                    drawLinesTable(filterLines(response.body(), "adt"), busTouristicTable, busTouristicLabel);
                    drawLinesTable(filterLines(response.body(), "adz1"), busNormalOnDemandTable, busNormalOnDemandLabel);
                    drawLinesTable(filterLines(response.body(), "tdd"), tramExtraTable, tramExtraLabel);
                    drawLinesTable(filterLines(response.body(), "add"), busExtraTable, busExtraLabel);
                } else {
                    Toast.makeText(context, R.string.lines_request_error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Line>> call, Throwable t) {
                progressBarHolder.setVisibility(View.GONE);
                Toast.makeText(context, R.string.lines_request_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private List<Line> filterLines(List<Line> lines, String type) {
        List<Line> temp = new ArrayList<>();
        for(Line line : lines) {
            if(line.getType().equals(type)) {
                temp.add(line);
            }
        }

        return temp;
    }

    private void drawLinesTable(List<Line> lines, TableLayout layout, LinearLayout label)
    {
        if(lines.isEmpty()) {
            layout.setVisibility(View.GONE);
            label.setVisibility(View.GONE);
            return;
        }

        int iterator = 0;

        int linesPerRow = Config.LINES_PER_ROW;
        if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            linesPerRow = Config.LINES_PER_ROW_LANDSCAPE;
        }
        int rows = (lines.size() % linesPerRow == 0) ? lines.size() / linesPerRow : lines.size() / linesPerRow + 1;

        for (int i = 1; i <= rows; i++) {
            TableRow row = new TableRow(context);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            Line line = null;
            for (int j = 1; j <= linesPerRow; j++) {
                LinearLayout cellLayout = new LinearLayout(context);
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                params.setMargins(2, 2, 2, 3);
                cellLayout.setLayoutParams(params);

                TextView tv = new TextView(context);

                tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                tv.setGravity(Gravity.CENTER);

                if(iterator < lines.size()) {
                    line = lines.get(iterator);

                    cellLayout.setClickable(true);
                    cellLayout.setFocusable(true);
                    cellLayout.setId(lines.get(iterator).getId());

                    if(line.isChanged()) {
                        cellLayout.setBackgroundColor(context.getResources().getColor(R.color.yellow));
                    } else {
                        cellLayout.setBackgroundResource(R.drawable.selector_line);
                    }

                    if(currentLine == line.getId()) {
                        tv.setTextColor(context.getResources().getColor(R.color.red));
                    }

                    cellLayout.setOnClickListener(view -> changeFilter(view.getId()));

                    tv.setText(lines.get(iterator).getName());

                    linesMap.put(line.getId(), line);
                } else {
                    tv.setText(line.getName());
                    tv.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                }

                cellLayout.addView(tv);
                row.addView(cellLayout);
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
