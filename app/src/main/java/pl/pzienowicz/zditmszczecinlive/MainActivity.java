package pl.pzienowicz.zditmszczecinlive;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    WebView myWebView = null;
    SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        myWebView = (WebView) findViewById(R.id.webView);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
            }
        });

        loadPage();
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
            myWebView.loadUrl(Config.URL);
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
