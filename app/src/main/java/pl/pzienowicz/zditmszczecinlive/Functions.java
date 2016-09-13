package pl.pzienowicz.zditmszczecinlive;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Functions {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

}
