package com.lyrawallet.Ui.FragmentPreferences;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.FragmentTransaction;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.lyrawallet.UserActions.UserActionsRpcNode;
import com.lyrawallet.Global;
import com.lyrawallet.R;
import com.lyrawallet.Storage.StorageBackUpWallet;
import com.lyrawallet.Ui.FragmentWalletManagement.FragmentNewAccount;

import java.util.Locale;

public class FragmentPreferencesRoot extends PreferenceFragmentCompat {
    private void setAppLocale(String localeCode){
        Resources resources = getActivity().getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(new Locale(localeCode.toLowerCase()));
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        Button toDashboardButton = getActivity().findViewById(R.id.toDashboard);
        toDashboardButton.setText(R.string.str_launcher_dashboard_short_button);
        Button toOpenWalletButton = getActivity().findViewById(R.id.toOpenWallet);
        toOpenWalletButton.setText(R.string.str_launcher_open_wallet_short_button);
        Button toCloseWalletButton = getActivity().findViewById(R.id.toCloseWallet);
        toCloseWalletButton.setText(R.string.str_launcher_close_wallet_short_button);
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
                        Global.setCurrentNetwork(Global.network.TESTNET);
                        networkSelectorPref.setSummary("Current selected network is: TESTNET");
                    } else if (value.equals("1")) {
                        Global.setCurrentNetwork(Global.network.MAINNET);
                        networkSelectorPref.setSummary("Current selected network is: MAINNET");
                    } else if (value.equals("2")) {
                        Global.setCurrentNetwork(Global.network.DEVNET);
                        networkSelectorPref.setSummary("Current selected network is: DEVNET");
                    }
                    new UserActionsRpcNode().actionHistory();
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
                        Global.setCurrentLanguage(Global.language.ENG);
                        languageSelectorPref.setSummary("Current selected language is: EN");
                        setAppLocale("en");
                    } else if (value.equals("1")) {
                        Global.setCurrentLanguage(Global.language.ROM);
                        languageSelectorPref.setSummary("Current selected language is: RO");
                        setAppLocale("ro");
                    }
                    getParentFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.nav_host_fragment_content_main, Global.getDashboard())
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
                            .replace(R.id.nav_host_fragment_content_main, FragmentNewAccount.newInstance())
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();
                    return true;
                }
            });
        }
        Preference newBackupWalletButton = findPreference(getString(R.string.prefs_backup_wallet_key));
        if(newBackupWalletButton != null) {
            newBackupWalletButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new StorageBackUpWallet();
                    return true;
                }
            });
        }
    }
}
