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
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.lyrawallet.Accounts.Accounts;
import com.lyrawallet.Api.ApiRpc;
import com.lyrawallet.Global;
import com.lyrawallet.MainActivity;
import com.lyrawallet.R;
import com.lyrawallet.Storage.StorageKeys;
import com.lyrawallet.Ui.FragmentManagerUser;
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
                    new FragmentManagerUser().goToNewAccount();
                } else {
                    if(Global.getPasswordSaveAllowed()) {
                        Global.setWalletPassword(passwordEditText.getText().toString());
                    }
                    passwordEditText.setText("");
                    walletNameEditText.setText("");
                    UiHelpers.closeKeyboard(view);
                    FragmentActivity activity = getActivity();
                    if (activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                new ApiRpc().act(new ApiRpc.Action().actionHistory(Global.str_api_rpc_purpose_history_disk_storage, Global.getCurrentNetworkName(), Global.getSelectedAccountName(), Global.getSelectedAccountId()));
                            }
                        });
                    }
                    new FragmentManagerUser().goToAccount();
                }
            } else {
                if(StorageKeys.getStatus() == StorageKeys.status.OK) {
                    Global.setWalletName(walletNameEditText.getText().toString());
                    new FragmentManagerUser().goToNewAccount();
                } else {
                    // Error ask for retry.
                    walletNameEditText.setError("ERROR: Name and/or password incorrect.");
                    walletNameEditText.requestFocus();
                }
            }
        }
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
                        new FragmentManagerUser().goToAccount();
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
                new FragmentManagerUser().goToNewWallet();
            }
        });

        recoverWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FragmentManagerUser().goToImportWallet();
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