package com.lyrawallet.Ui.Preferences;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.lyrawallet.R;
import com.lyrawallet.Ui.WalletManagement.NewAccount;

public class PreferencesRoot extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Button toDashboardButton = getActivity().findViewById(R.id.toDashboard);
        toDashboardButton.setVisibility(View.VISIBLE);
        Button toOpenWalletButton = getActivity().findViewById(R.id.toOpenWallet);
        toOpenWalletButton.setVisibility(View.INVISIBLE);
        Button toCloseWalletButton = getActivity().findViewById(R.id.toCloseWallet);
        toCloseWalletButton.setVisibility(View.INVISIBLE);

        setPreferencesFromResource(R.xml.preferences_root, rootKey);
        Preference myPref = (Preference) findPreference("key_upload_quality");
        myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                // open browser or intent here
                return true;
            }
        });
        myPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object value) {
                System.out.println("Selected: " + value);
                return true;
            }
        });
        Preference button = findPreference(getString(R.string.prefs_add_new_account));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
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
