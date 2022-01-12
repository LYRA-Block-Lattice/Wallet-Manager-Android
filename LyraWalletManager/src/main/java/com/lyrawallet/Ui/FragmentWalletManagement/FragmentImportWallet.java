package com.lyrawallet.Ui.FragmentWalletManagement;

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
import com.lyrawallet.Storage.StorageImportWallet;
import com.lyrawallet.Ui.FragmentAccount.FragmentAccount;
import com.lyrawallet.Ui.UiHelpers;

import java.io.File;

import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class FragmentImportWallet extends Fragment {
    public FragmentImportWallet() {

    }

    public static FragmentImportWallet newInstance() {
        FragmentImportWallet fragment = new FragmentImportWallet();
        return fragment;
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

    private void toAccount() {
        CurvedBottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
        getParentFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, FragmentAccount.newInstance("", ""))
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        Global.setVisiblePage(Global.visiblePage.ACCOUNT);
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
        CurvedBottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.GONE);

        EditText walletNameEditText = view.findViewById(R.id.wallet_name);
        Button importWalletButton = view.findViewById(R.id.import_wallet);

        UiHelpers.showKeyboard(view, walletNameEditText);

        Button importToWalletButton = (Button) view.findViewById(R.id.importToWalletButton);
        importToWalletButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(Global.getWalletName() == null) {
                    toOpenWallet();
                } else {
                    toAccount();
                }
            }
        });


        importWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                walletNameEditText.setError(null);
                File f = new File(Global.getWalletPath(walletNameEditText.getText().toString()));
                if(f.exists()) {
                    walletNameEditText.setError(getString(R.string.str_wallet_already_exists));
                    return;
                } else if (walletNameEditText.getText().length() < Global.getMinCharAllowedOnWalletName()) {
                    walletNameEditText.setError(getString(R.string.str_minimum_characters_in_name_is) + ": " + Global.getMinCharAllowedOnWalletName());
                    return;
                }
                new StorageImportWallet(walletNameEditText.getText().toString());
            }
        });
    }
}
