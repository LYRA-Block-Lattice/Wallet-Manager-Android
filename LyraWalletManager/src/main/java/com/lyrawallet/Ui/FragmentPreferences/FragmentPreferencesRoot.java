package com.lyrawallet.Ui.FragmentPreferences;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import androidx.fragment.app.FragmentTransaction;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.lyrawallet.Accounts.Accounts;
import com.lyrawallet.Api.ApiRpc;
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
        // App bar language is not recreated after language is changed, a manual change is needed.
        Spinner accountsSpinner = getActivity().findViewById(R.id.accountSpinner);
        if(accountsSpinner != null) {
            accountsSpinner.setPromptId(R.string.str_spinner_account_select_hint);
        }

    }
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Spinner accountsSpinner = getActivity().findViewById(R.id.accountSpinner);
        if(accountsSpinner != null) {
            accountsSpinner.setVisibility(View.INVISIBLE);
        }
        setPreferencesFromResource(R.xml.preferences_root, rootKey);

        Preference networkSelectorPref = findPreference(getString(R.string.pref_network_selection_key));
        if (networkSelectorPref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) networkSelectorPref;
            networkSelectorPref.setSummary(getString(R.string.pref_network_selection_current_selection) + ": " + listPref.getEntry());
        }
        if(networkSelectorPref != null) {
            networkSelectorPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    return true;
                }
            });
            networkSelectorPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object value) {
                    int net = Integer.parseInt(value.toString());
                    Global.setCurrentNetwork(Global.networkName[net]);
                    networkSelectorPref.setSummary(getString(R.string.pref_network_selection_current_selection) + ": " + Global.networkName[net]);
                    new ApiRpc().act(new ApiRpc.Action().actionHistory(Global.str_api_rpc_purpose_history_disk_storage, Global.getCurrentNetworkName(), Global.getSelectedAccountName(), Global.getSelectedAccountId()));
                    return true;
                }
            });
        }
        Preference languageSelectorPref = findPreference(getString(R.string.pref_language_selection_key));
        if (languageSelectorPref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) languageSelectorPref;
            languageSelectorPref.setSummary(getString(R.string.pref_language_selection_current_selection) + ": " + listPref.getEntry());
        }
        if(languageSelectorPref != null) {
            languageSelectorPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object value) {
                    int lang = Integer.parseInt(value.toString());
                    Global.setCurrentLanguage(Global.languageName[lang]);
                    setAppLocale(Global.languageName[lang]);
                    languageSelectorPref.setSummary(getString(R.string.pref_language_selection_current_selection) + ": " + Global.languageName[lang]);
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
        Preference autoCloseSelectorPref = findPreference(getString(R.string.pref_auto_close_selection_key));
        if(autoCloseSelectorPref != null) {
            autoCloseSelectorPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object value) {
                    if (value.equals("N")) {
                        Global.setInactivityTimeForClose(-1);
                    } else {
                        Global.setInactivityTimeForClose(Integer.parseInt(value.toString()));
                    }
                    return true;
                }
            });
        }
        Preference newAccountButton = findPreference(getString(R.string.prefs_add_new_account_key));
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
