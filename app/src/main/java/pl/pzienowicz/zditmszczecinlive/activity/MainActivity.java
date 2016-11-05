package pl.pzienowicz.zditmszczecinlive.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.onesignal.OneSignal;

import pl.pzienowicz.zditmszczecinlive.Config;
import pl.pzienowicz.zditmszczecinlive.Functions;
import pl.pzienowicz.zditmszczecinlive.R;
import pl.pzienowicz.zditmszczecinlive.dialog.LineDialog;

public class MainActivity extends AppCompatActivity {

    WebView myWebView = null;
    SharedPreferences sharedPreferences = null;
    Context context = null;
    BroadcastReceiver bcr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        OneSignal.startInit(this).init();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        final FloatingActionsMenu floatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);

        FloatingActionButton setFavouriteBtn = (FloatingActionButton) findViewById(R.id.set_favourite);
        setFavouriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences.edit().putString(Config.PREFERENCE_FAVOURITE_MAP, myWebView.getUrl()).apply();
                Snackbar.make(findViewById(R.id.swiperefresh), R.string.set_favourite, Snackbar.LENGTH_LONG).show();
                floatingActionsMenu.collapse();
            }
        });

        FloatingActionButton actionButton = (FloatingActionButton) findViewById(R.id.show_lines);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LineDialog dialog = new LineDialog(context);
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                dialog.show();
                floatingActionsMenu.collapse();
            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putInt(Config.PREFERENCE_SELECTED_LINE, 0).apply();

        myWebView = (WebView) findViewById(R.id.webView);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
            }
        });

        loadPage();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.INTENT_LOAD_NEW_URL);

        bcr = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch(intent.getAction()) {
                    case Config.INTENT_LOAD_NEW_URL:
                        int lineId = intent.getIntExtra(Config.EXTRA_LINE_ID, 0);
                        if(lineId == 0) {
                            myWebView.loadUrl(Config.URL);
                        } else {
                            myWebView.loadUrl(Config.LINE_URL + lineId);
                        }
                    break;
                }
            }
        };
        registerReceiver(bcr, filter);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(bcr);
        super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch(keyCode)
            {
                case KeyEvent.KEYCODE_BACK:
                    if(myWebView.canGoBack()){
                        myWebView.goBack();
                    }else{
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    private void loadPage() {
        if(Functions.isNetworkAvailable(this)) {
            String favouriteMap = sharedPreferences.getString(Config.PREFERENCE_FAVOURITE_MAP, Config.URL);

            myWebView.loadUrl(favouriteMap);
            showInitDialog();
        } else {
            showNoInternetSnackbar();
        }
    }

    private void showInitDialog()
    {
        if(!sharedPreferences.getBoolean(Config.PREFERENCE_SHOW_DIALOG, true)) {
            return;
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.dialog_title);
        alertDialogBuilder
                .setMessage(R.string.info)
                .setCancelable(false)
                .setPositiveButton(R.string.close,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.do_not_show_more,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        sharedPreferences.edit().putBoolean(Config.PREFERENCE_SHOW_DIALOG, false).apply();
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
}
