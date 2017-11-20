package pl.pzienowicz.zditmszczecinlive.data;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;

import pl.pzienowicz.zditmszczecinlive.R;
import pl.pzienowicz.zditmszczecinlive.model.BusStop;

public class BusStops {

    private static BusStops mInstance = null;
    private static HashMap<String, BusStop> stops;

    public BusStops(Context context) {
        stops = new HashMap<>();
        ArrayList<BusStop> list = new ArrayList<>();

        try {
            InputStream in = context.getResources().openRawResource(R.raw.slupki);
            Reader reader = new InputStreamReader(in, "UTF-8");
            list = new Gson().fromJson(reader, new TypeToken<ArrayList<BusStop>>() {}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(BusStop busStop : list) {
            stops.put(busStop.getNumer(), busStop);
        }
    }

    public static BusStops getInstance(Context context){
        if(mInstance == null)
        {
            mInstance = new BusStops(context);
        }
        return mInstance;
    }

    public BusStop getByNumber(String number) {
        return stops.get(number);
    }

}
