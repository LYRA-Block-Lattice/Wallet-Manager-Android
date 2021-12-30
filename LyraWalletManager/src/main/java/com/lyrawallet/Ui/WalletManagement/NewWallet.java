package com.lyrawallet.Ui.WalletManagement;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.lyrawallet.Accounts.Accounts;
import com.lyrawallet.Global;
import com.lyrawallet.MainActivity;
import com.lyrawallet.R;
import com.lyrawallet.Storage.KeyStorage;
import com.lyrawallet.Ui.Helpers;
import com.lyrawallet.Util.TextFilters;

public class NewWallet extends Fragment {
    public NewWallet() {
        // Required empty public constructor
    }

    public static NewWallet newInstance() {
        NewWallet fragment = new NewWallet();
        return fragment;
    }

    private void toNewAccount() {
        getParentFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, NewAccount.newInstance())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    private void toOpenWallet() {
        getParentFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, OpenWallet.newInstance())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_wallet, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Button toDashboardButton = getActivity().findViewById(R.id.toDashboard);
        toDashboardButton.setVisibility(View.INVISIBLE);
        Button toOpenWalletButton = getActivity().findViewById(R.id.toOpenWallet);
        toOpenWalletButton.setVisibility(View.VISIBLE);
        Button toCloseWalletButton = getActivity().findViewById(R.id.toCloseWallet);
        toCloseWalletButton.setVisibility(View.INVISIBLE);

        EditText newWalletNameEditText = view.findViewById(R.id.new_wallet_name);
        EditText password1EditText = view.findViewById(R.id.new_wallet_password1);
        EditText password2EditText = view.findViewById(R.id.new_wallet_password2);
        Button showPasswordButton = view.findViewById(R.id.new_wallet_show_password);
        Button createWalletButton = view.findViewById(R.id.create_wallet);

        Helpers.showKeyboard(view, newWalletNameEditText);
        newWalletNameEditText.setFilters(new InputFilter[]{TextFilters.getCharactersDigitsAndSpaceFilter()});

        createWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(newWalletNameEditText.length() < Global.minCharAllowedOnWalletName) {
                    // Error ask for retry.
                    newWalletNameEditText.setError("Minimum " + Global.minCharAllowedOnWalletName + " characters.");
                } else if(password1EditText.getText().length() < Global.minCharAllowedOnPassword) {
                    // Error ask for retry.
                    password1EditText.setError("Minimum " + Global.minCharAllowedOnPassword + " characters.");
                } else if(!password1EditText.getText().toString().equals(password2EditText.getText().toString())) {
                    password1EditText.setError("Password not match.");
                } else {
                    if(KeyStorage.fileExists(newWalletNameEditText.getText().toString())) {
                        newWalletNameEditText.setError("A wallet with this name already exists.");
                    } else {
                        if(!KeyStorage.containerSave(newWalletNameEditText.getText().toString(), password1EditText.getText().toString(), null)) {
                            Snackbar.make(view, "Unable to save wallet.", Snackbar.LENGTH_LONG)
                                    .setAction("", null).show();
                        } else {
                            Global.walletPassword = password1EditText.getText().toString();
                            Global.walletName = newWalletNameEditText.getText().toString();
                            newWalletNameEditText.setText("");
                            password1EditText.setText("");
                            password2EditText.setText("");
                            Accounts accounts = new Accounts((MainActivity) getActivity());
                            if(accounts.loadAccountsFromDisk(Global.walletName)) {
                                Snackbar.make(view, "Wallet successfully created.", Snackbar.LENGTH_LONG)
                                        .setAction("", null).show();
                                toNewAccount();
                            } else {
                                Snackbar.make(view, "That is not a wallet file.", Snackbar.LENGTH_LONG)
                                        .setAction("", null).show();
                                toOpenWallet();
                            }
                        }
                    }
                }
            }
        });

        showPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password1EditText.getTransformationMethod().getClass().getSimpleName() .equals("PasswordTransformationMethod")) {
                    password1EditText.setTransformationMethod(new SingleLineTransformationMethod());
                    password2EditText.setTransformationMethod(new SingleLineTransformationMethod());
                    showPasswordButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_visibility_on_password, 0, 0, 0);
                } else {
                    password1EditText.setTransformationMethod(new PasswordTransformationMethod());
                    password2EditText.setTransformationMethod(new PasswordTransformationMethod());
                    showPasswordButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_visibility_off_password, 0, 0, 0);
                }
            }
        });
    }
}
