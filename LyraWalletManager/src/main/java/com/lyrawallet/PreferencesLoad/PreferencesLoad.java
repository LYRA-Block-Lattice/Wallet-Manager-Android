package com.lyrawallet.PreferencesLoad;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import androidx.preference.PreferenceManager;

import com.lyrawallet.BuildConfig;
import com.lyrawallet.Global;
import com.lyrawallet.MainActivity;
import com.lyrawallet.R;

import java.util.Locale;

public class PreferencesLoad extends MainActivity {
    public PreferencesLoad() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getInstance());
        if(prefs.contains("wallet_version")) {
            // In case if preferences configuration need to be updated to a never version
            new PreferencesUpdate();
        } else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("wallet_version", BuildConfig.VERSION_NAME);
            editor.apply();
        }
        setAppLocale(getInstance(), prefs.getString(getInstance().getString(R.string.pref_language_selection_key), "0"));
        setNetwork(prefs.getString(getInstance().getString(R.string.pref_network_selection_key),"0"));
        String inactivityTime = prefs.getString(getInstance().getString(R.string.pref_auto_close_selection_key),"N");
        if (inactivityTime.equals("N")) {
            Global.setInactivityTimeForClose(-1);
        } else {
            Global.setInactivityTimeForClose(Integer.parseInt(inactivityTime));
        }
    }
    public PreferencesLoad(MainActivity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        setAppLocale(activity, prefs.getString(getInstance().getString(R.string.pref_language_selection_key), "0"));
        setNetwork(prefs.getString(getInstance().getString(R.string.pref_network_selection_key),"0"));
    }
    public void setAppLocale(MainActivity activity, String localeCode){
        String loc = "en";
        switch(localeCode) {
            case "1":
                loc = "ro";
                break;
            default:
                loc = "en";
                break;
        }
        Resources resources = activity.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.setLocale(new Locale(loc.toLowerCase()));
        resources.updateConfiguration(config, dm);
    }

    public void setNetwork(String network) {
        int net = Integer.parseInt(network);
        Global.setCurrentNetwork(Global.networkName[net]);
    }
}
