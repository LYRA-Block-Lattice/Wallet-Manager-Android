package com.lyrawallet.Ui.FragmentWalletManagement;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.lyrawallet.Accounts.Accounts;
import com.lyrawallet.Global;
import com.lyrawallet.MainActivity;
import com.lyrawallet.R;
import com.lyrawallet.Storage.StorageKeys;
import com.lyrawallet.Ui.FragmentAccount.FragmentAccount;
import com.lyrawallet.Ui.UiHelpers;
import com.lyrawallet.Util.UtilTextFilters;

import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class FragmentOpenWallet extends Fragment {
    public FragmentOpenWallet() {
        // Required empty public constructor
    }

    public static FragmentOpenWallet newInstance() {
        FragmentOpenWallet fragment = new FragmentOpenWallet();
        return fragment;
    }

    private void act(View view) {
        EditText walletNameEditText = getActivity().findViewById(R.id.wallet_name);
        EditText passwordEditText = getActivity().findViewById(R.id.password);
        // Check if minimum characters count in password is satisfied.
        if(walletNameEditText.length() < Global.getMinCharAllowedOnWalletName()) {
            // Error ask for retry.
            walletNameEditText.setError("Minimum " + Global.getMinCharAllowedOnWalletName() + " characters.");
        } else if(passwordEditText.getText().length() < Global.getMinCharAllowedOnPassword()) {
            // Error ask for retry.
            passwordEditText.setError("Minimum " + Global.getMinCharAllowedOnPassword() + " characters.");
        } else {
            // Success, hide keyboard and the key file.
            Accounts accounts = new Accounts((MainActivity) getActivity());
            boolean success = accounts.loadAccountsFromDisk(walletNameEditText.getText().toString(), passwordEditText.getText().toString());
            if(success) {
                // Load accounts success.
                if(Global.getSelectedAccountNr() == -1) {
                    passwordEditText.setText("");
                    walletNameEditText.setText("");
                    toNewAccount();
                } else {
                    passwordEditText.setText("");
                    walletNameEditText.setText("");
                    UiHelpers.closeKeyboard(view);
                    toAccount();
                }
            } else {
                if(StorageKeys.getStatus() == StorageKeys.status.OK) {
                    Global.setWalletName(walletNameEditText.getText().toString());
                    toNewAccount();
                } else {
                    // Error ask for retry.
                    walletNameEditText.setError("ERROR: Name and/or password incorrect.");
                    walletNameEditText.requestFocus();
                }
            }
        }
    }

    private void toAccount() {
        CurvedBottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
        getParentFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, FragmentAccount.newInstance("", ""))
                .addToBackStack(String.valueOf(Global.visiblePage.ACCOUNT.ordinal()))
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        Global.setVisiblePage(Global.visiblePage.ACCOUNT);
    }

    private void toNewAccount() {
        getParentFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, FragmentNewAccount.newInstance())
                .addToBackStack(String.valueOf(Global.visiblePage.NEW_ACCOUNT.ordinal()))
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        Global.setVisiblePage(Global.visiblePage.NEW_ACCOUNT);
    }

    private void toNewWallet() {
        getParentFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, FragmentNewWallet.newInstance())
                .addToBackStack(String.valueOf(Global.visiblePage.NEW_WALLET.ordinal()))
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        Global.setVisiblePage(Global.visiblePage.NEW_WALLET);
    }

    private void toImportWallet() {
        getParentFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, FragmentImportWallet.newInstance())
                .addToBackStack(String.valueOf(Global.visiblePage.IMPORT_WALLET.ordinal()))
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        Global.setVisiblePage(Global.visiblePage.IMPORT_WALLET);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_open_wallet, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        CurvedBottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.GONE);

        EditText walletNameEditText = view.findViewById(R.id.wallet_name);
        EditText passwordEditText = view.findViewById(R.id.password);
        Button openWalletButton = view.findViewById(R.id.open_wallet);
        Button newWalletButton = view.findViewById(R.id.new_wallet);
        Button recoverWalletButton = view.findViewById(R.id.recover_wallet);
        Button showPasswordButton = view.findViewById(R.id.show_password);

        Button openToWalletButton = (Button) view.findViewById(R.id.openToWalletButton);
        openToWalletButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                toAccount();
            }
        });

        if(Global.getWalletName() == null) {
            openToWalletButton.setVisibility(View.INVISIBLE);
        }

        UiHelpers.showKeyboard(view, walletNameEditText);
        walletNameEditText.setFilters(new InputFilter[]{UtilTextFilters.getCharactersDigitsAndSpaceFilter()});
        walletNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordEditText.setError(null);
                if(s.length() > 0 && s.charAt(s.length() - 1) == '\n') {
                    if(passwordEditText.getText().length() < Global.getMinCharAllowedOnPassword() + 1) {
                        passwordEditText.setError("Minimum " + Global.getMinCharAllowedOnPassword() + " characters.");
                    } else {
                        toAccount();
                        EditText password = getActivity().findViewById(R.id.password);
                        UiHelpers.closeKeyboard(view);
                        password.setError(null);
                    }
                } else if (s.length() < Global.getMinCharAllowedOnPassword()) {
                    openWalletButton.setEnabled(false);
                } else {
                    openWalletButton.setEnabled(true);
                }
            }
        });

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId & EditorInfo.IME_MASK_ACTION) != 0) {
                    act(v.getRootView());
                    return true;
                }
                else {
                    return false;
                }
            }
        });

        openWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                act(view);
            }
        });

        newWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toNewWallet();
            }
        });

        recoverWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toImportWallet();
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