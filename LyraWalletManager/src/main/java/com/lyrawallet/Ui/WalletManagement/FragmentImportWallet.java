package com.lyrawallet.Ui.WalletManagement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.lyrawallet.Global;
import com.lyrawallet.R;
import com.lyrawallet.Storage.ImportWallet;
import com.lyrawallet.Ui.Helpers;

import java.io.File;

public class FragmentImportWallet extends Fragment {
    public FragmentImportWallet() {

    }

    public static FragmentImportWallet newInstance() {
        FragmentImportWallet fragment = new FragmentImportWallet();
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
        return inflater.inflate(R.layout.fragment_import_wallet, container, false);
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

        EditText walletNameEditText = view.findViewById(R.id.wallet_name);
        Button importWalletButton = view.findViewById(R.id.import_wallet);

        Helpers.showKeyboard(view, walletNameEditText);

        importWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                walletNameEditText.setError(null);
                File f = new File(Global.getWalletPath(walletNameEditText.getText().toString()));
                if(f.exists()) {
                    walletNameEditText.setError(getString(R.string.str_wallet_already_exists));
                    return;
                } else if (walletNameEditText.getText().length() < Global.minCharAllowedOnWalletName) {
                    walletNameEditText.setError(getString(R.string.str_minimum_characters_in_name_is) + ": " + Global.minCharAllowedOnWalletName);
                    return;
                }
                new ImportWallet(walletNameEditText.getText().toString());
            }
        });
    }
}
