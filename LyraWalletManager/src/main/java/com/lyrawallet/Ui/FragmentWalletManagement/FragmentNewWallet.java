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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.lyrawallet.Accounts.Accounts;
import com.lyrawallet.Global;
import com.lyrawallet.MainActivity;
import com.lyrawallet.R;
import com.lyrawallet.Storage.StorageKeys;
import com.lyrawallet.Ui.UiHelpers;
import com.lyrawallet.Util.UtilTextFilters;

import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class FragmentNewWallet extends Fragment {
    public FragmentNewWallet() {
        // Required empty public constructor
    }

    public static FragmentNewWallet newInstance() {
        FragmentNewWallet fragment = new FragmentNewWallet();
        return fragment;
    }

    private void toNewAccount() {
        getParentFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, FragmentNewAccount.newInstance())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        Global.setVisiblePage(Global.visiblePage.NEW_ACCOUNT);
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
        CurvedBottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.GONE);

        EditText newWalletNameEditText = view.findViewById(R.id.new_wallet_name);
        EditText password1EditText = view.findViewById(R.id.new_wallet_password1);
        EditText password2EditText = view.findViewById(R.id.new_wallet_password2);
        Button showPasswordButton = view.findViewById(R.id.new_wallet_show_password);
        Button createWalletButton = view.findViewById(R.id.create_wallet);

        UiHelpers.showKeyboard(view, newWalletNameEditText);
        newWalletNameEditText.setFilters(new InputFilter[]{UtilTextFilters.getCharactersDigitsAndSpaceFilter()});

        createWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(newWalletNameEditText.length() < Global.getMinCharAllowedOnWalletName()) {
                    // Error ask for retry.
                    newWalletNameEditText.setError("Minimum " + Global.getMinCharAllowedOnWalletName() + " characters.");
                } else if(password1EditText.getText().length() < Global.getMinCharAllowedOnPassword()) {
                    // Error ask for retry.
                    password1EditText.setError("Minimum " + Global.getMinCharAllowedOnPassword() + " characters.");
                } else if(!password1EditText.getText().toString().equals(password2EditText.getText().toString())) {
                    password1EditText.setError("Password not match.");
                } else {
                    if(StorageKeys.fileExists(newWalletNameEditText.getText().toString())) {
                        newWalletNameEditText.setError("A wallet with this name already exists.");
                    } else {
                        if(!StorageKeys.containerSave(newWalletNameEditText.getText().toString(), password1EditText.getText().toString(), null)) {
                            UiHelpers.closeKeyboard(view);
                            Snackbar.make(view, "Unable to save wallet.", Snackbar.LENGTH_LONG)
                                    .setAction("", null).show();
                        } else {
                            Global.setWalletName(newWalletNameEditText.getText().toString());
                            newWalletNameEditText.setText("");
                            password1EditText.setText("");
                            password2EditText.setText("");
                            Accounts accounts = new Accounts((MainActivity) getActivity());
                            if(accounts.loadAccountsFromDisk(Global.getWalletName(), password1EditText.getText().toString())) {
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
