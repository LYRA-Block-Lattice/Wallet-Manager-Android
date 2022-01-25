package com.lyrawallet.Ui.FragmentMore;

import android.app.Activity;
import android.content.Intent;
import android.hardware.biometrics.BiometricManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lyrawallet.Api.ApiRpc;
import com.lyrawallet.Api.ApiRpcActions.ApiRpcActionsHistory;
import com.lyrawallet.Global;
import com.lyrawallet.R;
import com.lyrawallet.Storage.StorageBackUpWallet;
import com.lyrawallet.Ui.FragmentManagerUser;
import com.lyrawallet.Util.Concatenate;

import org.bouncycastle.util.StringList;

import java.util.ArrayList;
import java.util.List;

import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class FragmentMore extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public FragmentMore() {
        // Required empty public constructor
    }

    public static FragmentMore newInstance(String param1, String param2) {
        FragmentMore fragment = new FragmentMore();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private void populateAccountSpinner(Activity activity) {
        List<Pair<String, String>> nameAndIdList = Global.getWalletAccNameAndIdList();
        ArrayList<String> accNameList = new ArrayList<>();
        if(nameAndIdList != null) {
            for (Pair<String, String> acc: nameAndIdList) {
                accNameList.add(acc.first);
            }
            if(accNameList.size() != 0) {
                Global.setSelectedAccountName(accNameList.get(Global.getSelectedAccountNr()));
            }
        }
        Spinner accountsSpinner = activity.findViewById(R.id.accountSpinner);
        ArrayAdapter<String> accountListArrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, accNameList);
        accountListArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountsSpinner.setAdapter(accountListArrayAdapter);
        if(Global.getSelectedAccountNr( )!= 0 && Global.getSelectedAccountNr() >= accNameList.size()) {
            Global.setSelectedAccountNr(accNameList.size() - 1);
            accountsSpinner.setSelection(Global.getSelectedAccountNr());
        }

        if(Global.getSelectedAccountNr() >= 0) {
            accountsSpinner.setSelection(Global.getSelectedAccountNr());
        }
        accountsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(view != null) {
                    if(Global.getSelectedAccountNr() != i) {
                        Global.setSelectedAccountName(adapterView.getSelectedItem().toString());
                        Global.setSelectedAccountNr(i);
                        Global.setWalletName(Global.getWalletName());
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                new ApiRpc().act(new ApiRpc.Action().actionBalance(Global.getSelectedAccountId()));
                                if(Global.getWalletHistory(Concatenate.getHistoryFileName()) == null) {
                                    new ApiRpc().act(new ApiRpc.Action().actionHistory(Global.str_api_rpc_purpose_history_disk_storage,
                                            Global.getCurrentNetworkName(), Global.getSelectedAccountName(), Global.getSelectedAccountId()));
                                }
                            }
                        });
                    }
                    System.out.println(Global.getSelectedAccountName());
                }
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*View view = getView();
        if(view != null) {
            Spinner accountsSpinner = view.findViewById(R.id.accountSpinner);
            SpinnerAdapter accountListArrayAdapter = accountsSpinner.getAdapter();
            String[] arr = new String[accountListArrayAdapter.getCount()];
            for (int i = 0; i < accountListArrayAdapter.getCount(); i++) {
                arr[i] =accountListArrayAdapter.getItem(i).toString();
            }
            outState.putStringArray("spinner_items", arr);
        }*/
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        CurvedBottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);

        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        Button explorerButton = (Button) activity.findViewById(R.id.explorerButton);
        if (explorerButton != null) {
            explorerButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String url;
                    switch (Global.getCurrentNetworkName()) {
                        case "MAINNET":
                            url = "https://nebula.lyra.live/";
                            break;
                        case "DEVNET":
                            url = "";
                            break;
                        default:
                            url = "https://nebulatestnet.lyra.live/";
                            break;
                    }

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
            });
        }
        Button newAccountButton = (Button) activity.findViewById(R.id.newAccountButton);
        newAccountButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new FragmentManagerUser().goToNewAccount();
            }
        });
        Button backupWalletButton = (Button) activity.findViewById(R.id.backupWalletButton);
        backupWalletButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new StorageBackUpWallet();
            }
        });
        Button settingsButton = (Button) activity.findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new FragmentManagerUser().goToPreferences();
            }
        });
        Button aboutButton = (Button) activity.findViewById(R.id.aboutButton);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
        //if (savedInstanceState == null) {
            populateAccountSpinner(activity);
        /*} else {
            Spinner accountsSpinner = activity.findViewById(R.id.accountSpinner);
            ArrayAdapter<String> accountListArrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, savedInstanceState.getStringArray("spinner_items"));
            accountListArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            accountsSpinner.setAdapter(accountListArrayAdapter);
        }*/
    }
}
