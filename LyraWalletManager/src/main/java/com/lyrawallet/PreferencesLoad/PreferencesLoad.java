package com.lyrawallet.PreferencesLoad;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.lyrawallet.Global;
import com.lyrawallet.MainActivity;

import java.util.Locale;

public class PreferencesLoad extends MainActivity{
    public PreferencesLoad() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getInstance());
        setAppLocale(getInstance(), prefs.getString("pref_language_selection_key", "0"));
        setNetwork(prefs.getString("pref_network_selection_key","0"));
    }
    public PreferencesLoad(MainActivity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        setAppLocale(activity, prefs.getString("pref_language_selection_key", "0"));
        setNetwork(prefs.getString("pref_network_selection_key","0"));
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
        switch(network) {
            case "1":
                Global.setCurrentNetwork(Global.network.MAINNET);
                break;
            case "2":
                Global.setCurrentNetwork(Global.network.DEVNET);
                break;
            default:
                Global.setCurrentNetwork(Global.network.TESTNET);
                break;
        }
    }
}
