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
import com.lyrawallet.Crypto.Signatures;
import com.lyrawallet.Global;
import com.lyrawallet.MainActivity;
import com.lyrawallet.R;
import com.lyrawallet.Storage.KeyStorage;
import com.lyrawallet.Ui.Helpers;
import com.lyrawallet.Util.TextFilters;

public class FragmentRecoverAccount extends Fragment {
    public FragmentRecoverAccount() {
        // Required empty public constructor
    }

    public static FragmentRecoverAccount newInstance() {
        FragmentRecoverAccount fragment = new FragmentRecoverAccount();
        return fragment;
    }
    private void toDashboard() {
        getParentFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, Global.dashboard)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    private void toOpenWallet() {
        getParentFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, FragmentOpenWallet.newInstance())
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
        return inflater.inflate(R.layout.fragment_recover_account, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Button toDashboardButton = getActivity().findViewById(R.id.toDashboard);
        toDashboardButton.setVisibility(View.VISIBLE);
        Button toOpenWalletButton = getActivity().findViewById(R.id.toOpenWallet);
        toOpenWalletButton.setVisibility(View.INVISIBLE);
        Button toCloseWalletButton = getActivity().findViewById(R.id.toCloseWallet);
        toCloseWalletButton.setVisibility(View.INVISIBLE);

        EditText newWalletNameEditText = view.findViewById(R.id.recover_account_name);
        EditText passwordEditText = view.findViewById(R.id.recover_account_password);
        EditText privateKeyEditText = view.findViewById(R.id.recover_account_key);
        Button showPasswordButton = view.findViewById(R.id.recover_account_show_password);
        Button recoverAccountButton = view.findViewById(R.id.recover_account);

        Helpers.showKeyboard(view, newWalletNameEditText);
        newWalletNameEditText.setFilters(new InputFilter[]{TextFilters.getCharactersDigitsAndSpaceFilter()});

        recoverAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newWalletNameEditText.length() < Global.minCharAllowedOnWalletName) {
                    // Error ask for retry.
                    newWalletNameEditText.setError("Minimum " + Global.minCharAllowedOnWalletName + " characters.");
                } else if (KeyStorage.decrypt(Global.accountsContainer, passwordEditText.getText().toString()) == null) {
                    // Error ask for retry.
                    passwordEditText.setError("Incorrect wallet password.");
                } else if (KeyStorage.aliasExists(Global.walletName, newWalletNameEditText.getText().toString(), passwordEditText.getText().toString())) {
                    // Error ask for retry.
                    newWalletNameEditText.setError("This account already exists.");
                } else if (passwordEditText.getText().length() < Global.minCharAllowedOnPassword) {
                    // Error ask for retry.
                    passwordEditText.setError("Minimum " + Global.minCharAllowedOnPassword + " characters.");
                } else if (!Signatures.ValidatePrivateKey(privateKeyEditText.getText().toString())) {
                    // Error ask for retry.
                    privateKeyEditText.setError("Invalid private key.");
                } else {
                    if(!KeyStorage.aliasAdd(Global.walletName, newWalletNameEditText.getText().toString(), privateKeyEditText.getText().toString(), passwordEditText.getText().toString())) {
                        Snackbar.make(view, "An error occured when saving new account.", Snackbar.LENGTH_LONG)
                                .setAction("", null).show();
                    } else {
                        Accounts accounts = new Accounts((MainActivity) getActivity());
                        boolean success = accounts.loadAccountsFromDisk(Global.walletName, passwordEditText.getText().toString());
                        passwordEditText.setText("");
                        newWalletNameEditText.setText("");
                        if(!success) {
                            Snackbar.make(view, "An error occured when reloading wallet.", Snackbar.LENGTH_LONG)
                                    .setAction("", null).show();
                            toOpenWallet();
                        } else {
                            Helpers.closeKeyboard(view);
                            toDashboard();
                        }
                    }
                }
            }
        });
        showPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passwordEditText.getTransformationMethod().getClass().getSimpleName() .equals("PasswordTransformationMethod")) {
                    passwordEditText.setTransformationMethod(new SingleLineTransformationMethod());
                    showPasswordButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_visibility_on_password, 0, 0, 0);
                }
                else {
                    passwordEditText.setTransformationMethod(new PasswordTransformationMethod());
                    showPasswordButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_visibility_off_password, 0, 0, 0);
                }
            }
        });
    }
}