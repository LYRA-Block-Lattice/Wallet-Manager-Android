package com.lyrawallet.Ui.Preferences;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.FragmentTransaction;
import androidx.preference.DropDownPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.lyrawallet.Global;
import com.lyrawallet.R;
import com.lyrawallet.Ui.WalletManagement.NewAccount;

import java.util.Locale;

public class PreferencesRoot extends PreferenceFragmentCompat {
    private void setAppLocale(String localeCode){
        Resources resources = getActivity().getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(new Locale(localeCode.toLowerCase()));
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        //getActivity().setContentView(R.layout.content_main);

    }
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Button toDashboardButton = getActivity().findViewById(R.id.toDashboard);
        toDashboardButton.setVisibility(View.VISIBLE);
        Button toOpenWalletButton = getActivity().findViewById(R.id.toOpenWallet);
        toOpenWalletButton.setVisibility(View.INVISIBLE);
        Button toCloseWalletButton = getActivity().findViewById(R.id.toCloseWallet);
        toCloseWalletButton.setVisibility(View.INVISIBLE);

        setPreferencesFromResource(R.xml.preferences_root, rootKey);

        Preference networkSelectorPref = findPreference("pref_network_selection_key");
        if (networkSelectorPref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) networkSelectorPref;
            networkSelectorPref.setSummary("Current selected network is: " + listPref.getEntry());
        }
        if(networkSelectorPref != null) {
            networkSelectorPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    return true;
                }
            });
            networkSelectorPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object value) {
                    if (value.equals("0")) {
                        Global.currentNetwork = Global.network.TESTNET;
                        networkSelectorPref.setSummary("Current selected network is: TESTNET");
                    } else if (value.equals("1")) {
                        Global.currentNetwork = Global.network.MAINNET;
                        networkSelectorPref.setSummary("Current selected network is: MAINNET");
                    } else if (value.equals("2")) {
                        Global.currentNetwork = Global.network.DEVNET;
                        networkSelectorPref.setSummary("Current selected network is: DEVNET");
                    }
                    return true;
                }
            });
        }
        Preference languageSelectorPref = findPreference("pref_language_selection_key");
        if (languageSelectorPref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) languageSelectorPref;
            languageSelectorPref.setSummary("Current selected language is: " + listPref.getEntry());
        }
        if(languageSelectorPref != null) {
            languageSelectorPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object value) {
                    if (value.equals("0")) {
                        Global.currentLanguage = Global.language.ENG;
                        languageSelectorPref.setSummary("Current selected language is: ENG");
                        setAppLocale("en");
                    } else if (value.equals("1")) {
                        Global.currentLanguage = Global.language.ROM;
                        languageSelectorPref.setSummary("Current selected language is: ROM");
                        Locale locale = new Locale("RO");
                        setAppLocale("ro");
                    }
                    getParentFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.nav_host_fragment_content_main, Global.dashboard)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();
                    return true;
                }
            });
        }
        Preference newAccountButton = findPreference(getString(R.string.prefs_add_new_account));
        if(newAccountButton != null) {
            newAccountButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    getParentFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.nav_host_fragment_content_main, NewAccount.newInstance())
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();
                    return true;
                }
            });
        }
    }
}
