package com.lyrawallet.Ui.WalletManagement;

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
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.lyrawallet.Accounts.Accounts;
import com.lyrawallet.Global;
import com.lyrawallet.MainActivity;
import com.lyrawallet.R;
import com.lyrawallet.Storage.KeyStorage;
import com.lyrawallet.Ui.Helpers;
import com.lyrawallet.Util.TextFilters;

public class OpenWallet extends Fragment {
    public OpenWallet() {
        // Required empty public constructor
    }

    public static OpenWallet newInstance() {
        OpenWallet fragment = new OpenWallet();
        return fragment;
    }

    private void act(View view) {
        EditText walletNameEditText = getActivity().findViewById(R.id.username);
        EditText passwordEditText = getActivity().findViewById(R.id.password);
        // Check if minimum characters count in password is satisfied.
        if(walletNameEditText.length() < Global.minCharAllowedOnWalletName) {
            // Error ask for retry.
            walletNameEditText.setError("Minimum " + Global.minCharAllowedOnWalletName + " characters.");
        } else if(passwordEditText.getText().length() < Global.minCharAllowedOnPassword) {
            // Error ask for retry.
            passwordEditText.setError("Minimum " + Global.minCharAllowedOnPassword + " characters.");
        } else {
            // Success, hide keyboard and the key file.
            Global.walletPassword = passwordEditText.getText().toString();
            Accounts accounts = new Accounts((MainActivity) getActivity());
            boolean success = accounts.loadAccountsFromDisk(walletNameEditText.getText().toString());
            if(success) {
                // Load accounts success.
                if(Global.selectedAccountNr == -1) {
                    passwordEditText.setText("");
                    walletNameEditText.setText("");
                    toCreateAccount();
                } else {
                    passwordEditText.setText("");
                    walletNameEditText.setText("");
                    Helpers.closeKeyboard(view);
                    toDashboard();
                }
            } else {
                if(KeyStorage.Status == KeyStorage.status.OK) {
                    Global.walletName = walletNameEditText.getText().toString();
                    toCreateAccount();
                } else {
                    // Error ask for retry.
                    walletNameEditText.setError("ERROR: Name and/or password incorrect.");
                    walletNameEditText.requestFocus();
                }
            }
        }
    }

    private void toDashboard() {
        getParentFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, Global.dashboard)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    private void toCreateAccount() {
        getParentFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, NewAccount.newInstance())
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
        return inflater.inflate(R.layout.fragment_open_wallet, container, false);
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
        toOpenWalletButton.setVisibility(View.INVISIBLE);
        Button toCloseWalletButton = getActivity().findViewById(R.id.toCloseWallet);
        toCloseWalletButton.setVisibility(View.INVISIBLE);

        EditText walletNameEditText = view.findViewById(R.id.username);
        EditText passwordEditText = view.findViewById(R.id.password);
        Button openWalletButton = view.findViewById(R.id.open_wallet);
        Button newWalletButton = view.findViewById(R.id.new_wallet);
        Button showPasswordButton = view.findViewById(R.id.show_password);

        Helpers.showKeyboard(view, walletNameEditText);
        walletNameEditText.setFilters(new InputFilter[]{TextFilters.getCharactersDigitsAndSpaceFilter()});
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
                    if(passwordEditText.getText().length() < Global.minCharAllowedOnPassword + 1) {
                        passwordEditText.setError("Minimum " + Global.minCharAllowedOnPassword + " characters.");
                    } else {
                        toDashboard();
                        EditText password = getActivity().findViewById(R.id.password);
                        Helpers.closeKeyboard(view);
                        password.setError(null);
                    }
                } else if (s.length() < Global.minCharAllowedOnPassword) {
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
                getParentFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.nav_host_fragment_content_main, NewWallet.newInstance())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
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