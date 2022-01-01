package com.lyrawallet.PreferencesLoad;

import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.lyrawallet.BuildConfig;
import com.lyrawallet.MainActivity;

public class PreferencesUpdate extends MainActivity {
    public PreferencesUpdate() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getInstance());
        if(!prefs.getString("wallet_version", "0.0.0").equals(BuildConfig.VERSION_NAME)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("wallet_version", BuildConfig.VERSION_NAME);
            editor.apply();
        }
    }
}
