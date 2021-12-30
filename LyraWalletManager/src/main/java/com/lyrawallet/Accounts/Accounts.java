package com.lyrawallet.Accounts;

import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.lyrawallet.Global;
import com.lyrawallet.MainActivity;
import com.lyrawallet.R;
import com.lyrawallet.Storage.KeyStorage;

import java.util.ArrayList;
import java.util.List;

public class Accounts {
    MainActivity mainActivity = null;
    public Accounts(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public boolean loadAccountsFromDisk(String walletName) {
        // We get the account/pKys list and store them encrypted to make them globally available.
        List<Pair<String, String>> recoveredKeys = KeyStorage.containerRead(walletName, Global.walletPassword);
        if(recoveredKeys == null) {
            return false;
        }
        Global.accountsContainer = KeyStorage.encrypt(recoveredKeys, Global.walletPassword);
        //setSupportActionBar(binding.appBarMain.toolbar);
        // Load account names from encrypted internal container.
        boolean status = loadAccountsFromInternalContainer();
        if(!status) {
            return false;
        }
        // Check if any account is in the container.
        Spinner accountsSpinner = mainActivity.findViewById(R.id.accountSpinner);
        if(accountsSpinner.getCount() != 0) {
            Global.selectedAccountNr = 0;
            Global.selectedAccountName = accountsSpinner.getSelectedItem().toString();
        } else {
            Global.selectedAccountNr = -1;
            Global.selectedAccountName = "";
        }

        Global.walletName = walletName;
        accountsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Global.selectedAccountName = adapterView.getSelectedItem().toString();// list.get(i).first;
                Global.selectedAccountNr = i;
                Global.walletName = walletName;
                System.out.println(Global.selectedAccountName);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        return true;
    }

    public boolean loadAccountsFromInternalContainer() {
        // Populate the spinner with the accounts name.
        ArrayList<String> accNameList = new ArrayList<>();
        List<Pair<String, String>> accKeyList = KeyStorage.decrypt(Global.accountsContainer, Global.walletPassword);
        if(accKeyList != null) {
            for (Pair<String, String> acc: accKeyList) {
                accNameList.add(acc.first);
            }
            if(accNameList.size() != 0) {
                Global.selectedAccountName = accNameList.get(0);
            }
        }
        Spinner accountsSpinner = mainActivity.findViewById(R.id.accountSpinner);
        ArrayAdapter<String> accountListArrayAdapter = new ArrayAdapter<>(mainActivity, android.R.layout.simple_spinner_item, accNameList);
        accountListArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountsSpinner.setAdapter(accountListArrayAdapter);
        if(Global.selectedAccountNr != 0 && Global.selectedAccountNr >= accNameList.size()) {
            Global.selectedAccountNr = accNameList.size() - 1;
            accountsSpinner.setSelection(Global.selectedAccountNr);
        }
        return accKeyList != null;
    }
}
