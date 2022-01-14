package com.lyrawallet.Ui.FragmentWalletManagement;

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

import com.google.android.material.snackbar.Snackbar;
import com.lyrawallet.Accounts.Accounts;
import com.lyrawallet.Crypto.CryptoSignatures;
import com.lyrawallet.Global;
import com.lyrawallet.MainActivity;
import com.lyrawallet.R;
import com.lyrawallet.Storage.StorageKeys;
import com.lyrawallet.Ui.FragmentManager;
import com.lyrawallet.Ui.UiHelpers;
import com.lyrawallet.Util.UtilTextFilters;

import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class FragmentNewAccount extends Fragment {
    public FragmentNewAccount() {
        // Required empty public constructor
    }

    public static FragmentNewAccount newInstance() {
        FragmentNewAccount fragment = new FragmentNewAccount();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_account, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        CurvedBottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.GONE);

        EditText newWalletNameEditText = view.findViewById(R.id.new_account_name);
        EditText passwordEditText = view.findViewById(R.id.new_account_password);
        Button showPasswordButton = view.findViewById(R.id.new_account_show_password);
        Button createAccountButton = view.findViewById(R.id.create_account);
        Button recoverAccountButton = view.findViewById(R.id.recover_account);

        UiHelpers.showKeyboard(view, newWalletNameEditText);
        newWalletNameEditText.setFilters(new InputFilter[]{UtilTextFilters.getCharactersDigitsAndSpaceFilter()});

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newWalletNameEditText.length() < Global.getMinCharAllowedOnWalletName()) {
                    // Error ask for retry.
                    newWalletNameEditText.setError("Minimum " + Global.getMinCharAllowedOnWalletName() + " characters.");
                } else if (StorageKeys.decrypt(Global.getAccountsContainer(), passwordEditText.getText().toString()) == null) {
                    // Error ask for retry.
                    passwordEditText.setError("Incorrect wallet password.");
                } else if (StorageKeys.aliasExists(Global.getWalletName(), newWalletNameEditText.getText().toString(), passwordEditText.getText().toString())) {
                    // Error ask for retry.
                    newWalletNameEditText.setError("This account already exists.");
                } else if (passwordEditText.getText().length() < Global.getMinCharAllowedOnPassword()) {
                    // Error ask for retry.
                    passwordEditText.setError("Minimum " + Global.getMinCharAllowedOnPassword() + " characters.");
                } else {
                    if(!StorageKeys.aliasAdd(Global.getWalletName(), newWalletNameEditText.getText().toString(), CryptoSignatures.generateWallet().first, passwordEditText.getText().toString())) {
                        UiHelpers.closeKeyboard(view);
                        Snackbar.make(view, "An error occured when saving new account.", Snackbar.LENGTH_LONG)
                                .setAction("", null).show();
                    } else {
                        Accounts accounts = new Accounts((MainActivity) getActivity());
                        boolean success = accounts.loadAccountsFromDisk(Global.getWalletName(), passwordEditText.getText().toString());
                        passwordEditText.setText("");
                        newWalletNameEditText.setText("");
                        if(!success) {
                            Snackbar.make(view, "An error occured when reloading wallet.", Snackbar.LENGTH_LONG)
                                    .setAction("", null).show();
                            new FragmentManager().goToOpenWallet();
                        } else {
                            UiHelpers.closeKeyboard(view);
                            new FragmentManager().goToAccount();
                        }
                    }
                }
            }
        });
        recoverAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FragmentManager().goToRecoverAccount();
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
