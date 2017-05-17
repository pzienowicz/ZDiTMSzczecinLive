package pl.pzienowicz.zditmszczecinlive.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import pl.pzienowicz.zditmszczecinlive.Config;
import pl.pzienowicz.zditmszczecinlive.R;

public class SettingsDialog extends Dialog {

    public SettingsDialog(@NonNull final Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_settings);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        CheckBox locationCheckbox = (CheckBox) findViewById(R.id.locationCheckbox);
        locationCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.edit().putBoolean(Config.PREFERENCE_USE_LOCATION, isChecked).apply();
                Intent intent = new Intent(Config.INTENT_REFRESH_SETTINGS);
                context.sendBroadcast(intent);
            }
        });

        locationCheckbox.setChecked(preferences.getBoolean(Config.PREFERENCE_USE_LOCATION, true));

        CheckBox zoomMapCheckbox = (CheckBox) findViewById(R.id.zoomMapCheckbox);
        zoomMapCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.edit().putBoolean(Config.PREFERENCE_ZOOM_MAP, isChecked).apply();
                Intent intent = new Intent(Config.INTENT_REFRESH_SETTINGS);
                context.sendBroadcast(intent);
            }
        });

        zoomMapCheckbox.setChecked(preferences.getBoolean(Config.PREFERENCE_ZOOM_MAP, true));
    }
}
