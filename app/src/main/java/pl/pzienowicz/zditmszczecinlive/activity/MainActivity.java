package pl.pzienowicz.zditmszczecinlive.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.onesignal.OneSignal;

import java.util.Timer;
import java.util.TimerTask;

import pl.pzienowicz.zditmszczecinlive.Config;
import pl.pzienowicz.zditmszczecinlive.Functions;
import pl.pzienowicz.zditmszczecinlive.R;
import pl.pzienowicz.zditmszczecinlive.dialog.LineDialog;
import pl.pzienowicz.zditmszczecinlive.dialog.SettingsDialog;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private WebView myWebView = null;
    private SharedPreferences sharedPreferences = null;
    private Context context = null;
    private BroadcastReceiver bcr = null;
    private LocationManager locationManager = null;
    private Location currentLocation = null;
    private Timer myTimer = null;
    private boolean zoomMap = false;
    private String currentUrl = Config.URL;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1443;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        OneSignal.startInit(this).init();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putInt(Config.PREFERENCE_SELECTED_LINE, 0).apply();

        final FloatingActionsMenu floatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);

        FloatingActionButton setFavouriteBtn = (FloatingActionButton) findViewById(R.id.set_favourite);
        setFavouriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences.edit().putString(Config.PREFERENCE_FAVOURITE_MAP, currentUrl).apply();
                Snackbar.make(findViewById(R.id.swiperefresh), R.string.set_favourite, Snackbar.LENGTH_LONG).show();
                floatingActionsMenu.collapse();
            }
        });

        FloatingActionButton actionButton = (FloatingActionButton) findViewById(R.id.show_lines);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LineDialog dialog = new LineDialog(context);
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                dialog.show();
                floatingActionsMenu.collapse();
            }
        });

        FloatingActionButton settingsButton = (FloatingActionButton) findViewById(R.id.settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsDialog dialog = new SettingsDialog(context);
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                dialog.show();
                floatingActionsMenu.collapse();
            }
        });

        myWebView = (WebView) findViewById(R.id.webView);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.INTENT_LOAD_NEW_URL);
        filter.addAction(Config.INTENT_REFRESH_SETTINGS);
        filter.addAction(Config.INTENT_NO_INTERNET_CONNETION);

        bcr = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case Config.INTENT_LOAD_NEW_URL:
                        int lineId = intent.getIntExtra(Config.EXTRA_LINE_ID, 0);
                        if (lineId == 0) {
                            currentUrl = Config.URL;
                        } else {
                            currentUrl = Config.LINE_URL + lineId;
                        }
                        myWebView.loadUrl(currentUrl);
                        break;
                    case Config.INTENT_REFRESH_SETTINGS:
                        refreshSettings();
                        break;
                    case Config.INTENT_NO_INTERNET_CONNETION:
                        showNoInternetSnackbar();
                        break;
                }
            }
        };
        registerReceiver(bcr, filter);
        refreshSettings();

        if (savedInstanceState != null) {
            myWebView.restoreState(savedInstanceState);
        } else {
            loadPage();
        }

        myTimer = new Timer();
        myTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(currentLocation != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String newUrl = currentUrl + "?lat=" + currentLocation.getLatitude() + "&lon=" + currentLocation.getLongitude();

                            if(zoomMap) {
                                newUrl += "&zoom=16";
                            }

                            myWebView.loadUrl(newUrl);
                        }
                    });
                }
            }
        }, 0, 30 * 1000);
    }

    private void refreshSettings() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (sharedPreferences.getBoolean(Config.PREFERENCE_USE_LOCATION, true)) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.this);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        } else {
            if(locationManager != null) {
                locationManager.removeUpdates(MainActivity.this);
                locationManager = null;
            }
            currentLocation = null;
        }

        zoomMap = sharedPreferences.getBoolean(Config.PREFERENCE_ZOOM_MAP, true);
    }

    @Override
    public void onDestroy() {
        if(myTimer != null) {
            myTimer.cancel();
        }
        unregisterReceiver(bcr);
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        myWebView.saveState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    finish();
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    private void loadPage() {
        if (Functions.isNetworkAvailable(this)) {
            String favouriteMap = sharedPreferences.getString(Config.PREFERENCE_FAVOURITE_MAP, Config.URL);

            myWebView.loadUrl(favouriteMap);
            showInitDialog();
        } else {
            showNoInternetSnackbar();
        }
    }

    private void showInitDialog() {
        if (!sharedPreferences.getBoolean(Config.PREFERENCE_SHOW_DIALOG, true)) {
            return;
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.dialog_title);
        alertDialogBuilder
                .setMessage(R.string.info)
                .setCancelable(false)
                .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.do_not_show_more, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sharedPreferences.edit().putBoolean(Config.PREFERENCE_SHOW_DIALOG, false).apply();
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        sharedPreferences.edit().putBoolean(Config.PREFERENCE_ZOOM_MAP, false).apply();
                        sharedPreferences.edit().putBoolean(Config.PREFERENCE_USE_LOCATION, false).apply();
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.this);
                } else {
                    sharedPreferences.edit().putBoolean(Config.PREFERENCE_ZOOM_MAP, false).apply();
                    sharedPreferences.edit().putBoolean(Config.PREFERENCE_USE_LOCATION, false).apply();
                }
                break;
            }
        }
    }


    private void showNoInternetSnackbar()
    {
        Snackbar
                .make(findViewById(R.id.swiperefresh), R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.refresh, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadPage();
                    }
                })
                .show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null) {
            currentLocation = location;
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
