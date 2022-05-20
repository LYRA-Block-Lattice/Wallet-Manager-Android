package com.lyrawallet.Ui.FragmentPreferences;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.lyrawallet.Api.ApiRpc;
import com.lyrawallet.Global;
import com.lyrawallet.R;
import com.lyrawallet.Ui.FragmentManagerUser;

import java.util.Locale;

import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class FragmentPreferencesRoot extends PreferenceFragmentCompat {
    private void setAppLocale(String localeCode){
        Resources resources = getActivity().getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(new Locale(localeCode.toLowerCase()));
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        // App bar language is not recreated after language is changed, a manual change is needed.
    }
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_root, rootKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        CurvedBottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.GONE);

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
                    new FragmentManagerUser().goToMore();
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
        Preference pref_debug_enabled_switch_key = findPreference(getString(R.string.pref_debug_enabled_switch_key));
        if(pref_debug_enabled_switch_key != null) {
            pref_debug_enabled_switch_key.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object value) {
                    Global.setDebugEnabled(value.toString().equals("true"));
                    return true;
                }
            });
        }
        Preference pref_randomize_node_address_enabled_switch_key = findPreference(getString(R.string.pref_randomize_node_address_enabled_switch_key));
        if(pref_randomize_node_address_enabled_switch_key != null) {
            pref_randomize_node_address_enabled_switch_key.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object value) {
                    Global.setRandomizeIpEnabled(value.toString().equals("true"));
                    return true;
                }
            });
        }
        PackageInfo pInfo = null;
        try {
            pInfo = this.getActivity().getPackageManager().getPackageInfo(this.getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;
        Preference pref_version_key = findPreference(getString(R.string.pref_version_key));
        pref_version_key.setSummary(String.format(Locale.US, "%s%s", "LyraWalletManager V", version));

        View v = super.onCreateView(inflater, container, savedInstanceState);
        return v;//inflater.inflate(R.layout.fragment_settings, container, false);
    }
}
