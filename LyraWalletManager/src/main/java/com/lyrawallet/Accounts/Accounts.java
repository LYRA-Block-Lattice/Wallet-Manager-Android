package com.lyrawallet.Accounts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputLayout;
import com.lyrawallet.Actions.UserRpcActions;
import com.lyrawallet.Crypto.Signatures;
import com.lyrawallet.Global;
import com.lyrawallet.MainActivity;
import com.lyrawallet.R;
import com.lyrawallet.Storage.KeyStorage;
import com.lyrawallet.Ui.Helpers;

import java.util.ArrayList;
import java.util.List;

public class Accounts {
    public static String password = null;
    MainActivity mainActivity = null;
    public Accounts(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public boolean loadAccountsFromDisk(String walletName, String password) {
        // We get the account/pKys list and store them encrypted to make them globally available.
        List<Pair<String, String>> recoveredKeys = KeyStorage.containerRead(walletName, password);
        if(recoveredKeys == null) {
            return false;
        }
        Global.accountsContainer = KeyStorage.encrypt(recoveredKeys, password);
        //setSupportActionBar(binding.appBarMain.toolbar);
        // Load account names from encrypted internal container.
        boolean status = loadAccountsFromInternalContainer(password);
        if(!status) {
            return false;
        }
        // Check if any account is in the container.
        Spinner accountsSpinner = mainActivity.findViewById(R.id.accountSpinner);
        if(accountsSpinner.getCount() != 0) {
            Global.selectedAccountNr = 0;
            Global.selectedAccountName = accountsSpinner.getSelectedItem().toString();
            //new UserRpcActions().actionHistory();

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
                new UserRpcActions().actionHistory();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        return true;
    }

    public boolean loadAccountsFromInternalContainer(String password) {
        // Populate the spinner with the accounts name.
        ArrayList<String> accNameList = new ArrayList<>();
        Global.walletAccNameAndId = new ArrayList<Pair<String, String>>();
        List<Pair<String, String>> accKeyList = KeyStorage.decrypt(Global.accountsContainer, password);
        if(accKeyList != null) {
            for (Pair<String, String> acc: accKeyList) {
                accNameList.add(acc.first);
                Global.walletAccNameAndId.add(new Pair<String, String>(acc.first, Signatures.GetAccountIdFromPrivateKey(acc.second)));
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

    public static String getPrivateKey() {
        List<Pair<String, String>> acc = KeyStorage.decrypt(Global.accountsContainer, password);
        for (Pair<String, String> ac: acc) {
            if(ac.first.equals(Global.selectedAccountName)) {
                return ac.second;
            }
        }
        return null;
    }

    public static String getPrivateKey(String password) {
        List<Pair<String, String>> acc = KeyStorage.decrypt(Global.accountsContainer, password);
        for (Pair<String, String> ac: acc) {
            if(ac.first.equals(Global.selectedAccountName)) {
                return ac.second;
            }
        }
        return null;
    }

    public static String getAccount() {
        List<Pair<String, String>> acc = Global.walletAccNameAndId;
        if (acc == null) {
            return null;
        }
        for (Pair<String, String> ac: acc) {
            if(ac.first.equals(Global.selectedAccountName)) {
                return ac.second;
            }
        }
        return null;
    }

    public void promptForPassword(Context c, View view) {
        password = null;
        final EditText passEditText = new EditText(c);
        passEditText.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        passEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        Helpers.showKeyboard(view, passEditText);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle(R.string.str_dialog_title)
                .setMessage(R.string.str_dialog_message)
                .setView(passEditText)
                .setPositiveButton(R.string.str_dialog_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        password = String.valueOf(passEditText.getText());
                    }
                })
                .setNegativeButton(R.string.str_dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        password = "";
                    }
                })
                .create();
        dialog.show();
    }
}
