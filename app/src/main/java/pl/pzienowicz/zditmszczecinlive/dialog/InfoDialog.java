package pl.pzienowicz.zditmszczecinlive.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pl.pzienowicz.zditmszczecinlive.Config;
import pl.pzienowicz.zditmszczecinlive.Functions;
import pl.pzienowicz.zditmszczecinlive.R;
import pl.pzienowicz.zditmszczecinlive.adapter.InfoListAdapter;
import pl.pzienowicz.zditmszczecinlive.model.Info;
import pl.pzienowicz.zditmszczecinlive.model.Line;
import pl.pzienowicz.zditmszczecinlive.rest.ZDiTMService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InfoDialog extends Dialog {

    private FrameLayout progressBarHolder = null;
    private Context context = null;
    private InfoListAdapter adapter = null;
    private TextView noInfoTv = null;
    private ArrayList<Info> records = new ArrayList<>();

    public InfoDialog(@NonNull final Context context) {
        super(context);
        this.context = context;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_info);

        noInfoTv = (TextView) findViewById(R.id.noInfoTv);
        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
        adapter = new InfoListAdapter(context, records);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        if(!Functions.isNetworkAvailable(context)) {
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_LONG).show();

            Intent intent = new Intent(Config.INTENT_NO_INTERNET_CONNETION);
            context.sendBroadcast(intent);

            dismiss();
            return;
        }

        progressBarHolder.setVisibility(View.VISIBLE);

        ZDiTMService service = retrofit.create(ZDiTMService.class);
        Call<List<Info>> lines = service.listInfo();
        lines.enqueue(new Callback<List<Info>>() {
            @Override
            public void onResponse(Call<List<Info>> call, Response<List<Info>> response) {
                progressBarHolder.setVisibility(View.GONE);

                if(response.isSuccessful()) {
                    records.clear();
                    records.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    if(records.isEmpty()) {
                        noInfoTv.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(context, R.string.info_request_error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Info>> call, Throwable t) {
                progressBarHolder.setVisibility(View.GONE);
                t.printStackTrace();
                Toast.makeText(context, R.string.info_request_error, Toast.LENGTH_LONG).show();
            }
        });

        Button contactUsBtn = (Button) findViewById(R.id.contactUsBtn);
        contactUsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","zienowicz.pawel@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Reklama - Komunikacja Miejska Szczecin");
                context.startActivity(Intent.createChooser(emailIntent, "Wyślij email..."));
            }
        });
    }
}
