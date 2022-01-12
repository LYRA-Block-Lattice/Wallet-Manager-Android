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
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.lyrawallet.Accounts.Accounts;
import com.lyrawallet.Crypto.CryptoSignatures;
import com.lyrawallet.Global;
import com.lyrawallet.MainActivity;
import com.lyrawallet.R;
import com.lyrawallet.Storage.StorageKeys;
import com.lyrawallet.Ui.FragmentAccount.FragmentAccount;
import com.lyrawallet.Ui.FragmentPreferences.FragmentPreferencesRoot;
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
    private void toAccount() {
        CurvedBottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.onMenuItemClick(2);
        getParentFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, FragmentAccount.newInstance("", ""))
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        Global.setVisiblePage(Global.visiblePage.ACCOUNT);
    }

    private void toOpenWallet() {
        getParentFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, FragmentOpenWallet.newInstance())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        Global.setVisiblePage(Global.visiblePage.OPEN_WALLET);
    }

    private void toRecoverAccount() {
        getParentFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, FragmentRecoverAccount.newInstance())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        Global.setVisiblePage(Global.visiblePage.RECOVER_ACCOUNT);
    }

    private void toSettings() {
        getParentFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, new FragmentPreferencesRoot())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        Global.setVisiblePage(Global.visiblePage.SETTINGS);
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

        Button newAccToWalletButton = (Button) view.findViewById(R.id.newAccToWalletButton);
        newAccToWalletButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Global.getWalletName() == null) {
                    toOpenWallet();
                } else {
                    toSettings();
                }
            }
        });

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
                            toOpenWallet();
                        } else {
                            UiHelpers.closeKeyboard(view);
                            toAccount();
                        }
                    }
                }
            }
        });
        recoverAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toRecoverAccount();
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
