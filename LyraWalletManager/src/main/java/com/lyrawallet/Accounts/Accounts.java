package com.lyrawallet.Accounts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;

import com.lyrawallet.Crypto.CryptoSignatures;
import com.lyrawallet.Global;
import com.lyrawallet.MainActivity;
import com.lyrawallet.R;
import com.lyrawallet.Storage.StorageKeys;
import com.lyrawallet.Ui.UiHelpers;

import java.util.ArrayList;
import java.util.List;

public class Accounts {
    private static String Password = null;
    private MainActivity MainActivity = null;

    public Accounts(MainActivity mainActivity) {
        this.MainActivity = mainActivity;
    }

    public boolean loadAccountsFromDisk(String walletName, String password) {
        // We get the account/pKys list and store them encrypted to make them globally available.
        List<Pair<String, String>> recoveredKeys = StorageKeys.containerRead(walletName, password);
        if(recoveredKeys == null) {
            return false;
        }
        Global.setAccountsContainer(StorageKeys.encrypt(recoveredKeys, password));
        //setSupportActionBar(binding.appBarMain.toolbar);
        // Load account names from encrypted internal container.
        boolean status = loadAccountsFromInternalContainer(password);
        if(!status) {
            return false;
        }
        // Check if any account is in the container.
        if(Global.getWalletAccNameAndIdList().size() != 0) {
            Global.setSelectedAccountNr(0);
            Pair<String, String> acc = Global.getWalletAccNameAndIdList().get(0);
            Global.setSelectedAccountName(acc.first);
            //new UserRpcActions().actionHistory();

        } else {
            Global.setSelectedAccountNr(-1);
            Global.setSelectedAccountName("");
        }

        Global.setWalletName(walletName);
        //restoreAccountSelectSpinner(MainActivity);
        return true;
    }

    public boolean loadAccountsFromInternalContainer(String password) {
        // Populate the spinner with the accounts name.
        ArrayList<String> accNameList = new ArrayList<>();
        Global.setWalletAccNameAndIdList(new ArrayList<Pair<String, String>>());
        List<Pair<String, String>> accKeyList = StorageKeys.decrypt(Global.getAccountsContainer(), password);
        if(accKeyList != null) {
            for (Pair<String, String> acc: accKeyList) {
                accNameList.add(acc.first);
                Global.getWalletAccNameAndIdList().add(new Pair<String, String>(acc.first, CryptoSignatures.getAccountIdFromPrivateKey(acc.second)));
            }
            if(accNameList.size() != 0) {
                Global.setSelectedAccountName(accNameList.get(0));
            }
        }
        return accKeyList != null;
    }

    public static String getPrivateKey() {
        List<Pair<String, String>> acc = StorageKeys.decrypt(Global.getAccountsContainer(), Password);
        for (Pair<String, String> ac: acc) {
            if(ac.first.equals(Global.getSelectedAccountName())) {
                return ac.second;
            }
        }
        return null;
    }

    public static String getPrivateKey(String password) {
        List<Pair<String, String>> acc = StorageKeys.decrypt(Global.getAccountsContainer(), password);
        for (Pair<String, String> ac: acc) {
            if(ac.first.equals(Global.getSelectedAccountName())) {
                return ac.second;
            }
        }
        return null;
    }

    public static String getAccount() {
        List<Pair<String, String>> acc = Global.getWalletAccNameAndIdList();
        if (acc == null) {
            return null;
        }
        for (Pair<String, String> ac: acc) {
            if(ac.first.equals(Global.getSelectedAccountName())) {
                return ac.second;
            }
        }
        return null;
    }

    public void promptForPassword(Context c, View view) {
        Password = null;
        final EditText passEditText = new EditText(c);
        passEditText.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        passEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        UiHelpers.showKeyboard(view, passEditText);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle(R.string.Unlock_wallet)
                .setMessage(R.string.Enter_wallet_password)
                .setView(passEditText)
                .setPositiveButton(R.string.Accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Password = String.valueOf(passEditText.getText());
                    }
                })
                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Password = "";
                    }
                })
                .create();
        dialog.show();
    }
}
