package com.lyrawallet.Ui.FragmentStaking.ProfitingListInfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lyrawallet.Api.ApiRpcActions.ApiRpcActionsBrokerAccounts;
import com.lyrawallet.Api.Network.NetworkWebHttps;
import com.lyrawallet.ApiNode;
import com.lyrawallet.Global;
import com.lyrawallet.GlobalLyra;
import com.lyrawallet.R;
import com.lyrawallet.Ui.FragmentManagerUser;
import com.lyrawallet.Ui.UiHelpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class FragmentProfitingListingInfo extends Fragment {
    int historyLen = 0;
    ProfitingListInfoSpinnerAdapter adapter;
    private boolean refreshInProgress = false;
    Timer timer1;
    Timer timer2;
    ApiNode.AllProfitingAccounts brokerAccounts = null;

    public FragmentProfitingListingInfo() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_staking_add_account, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Activity activity = getActivity();
        if (activity == null)
            return;
        CurvedBottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.GONE);

        Spinner tokenSpinner = (Spinner) view.findViewById(R.id.stakingAddAccountSpinner);

        NetworkWebHttps webHttpsTask = new NetworkWebHttps();
        webHttpsTask.setListener(new NetworkWebHttps.WebHttpsTaskListener() {
            @Override
            public void onWebHttpsTaskFinished(NetworkWebHttps instance) {
                System.out.println("BROKER ACCOUNTS: " + instance.getContent());
                brokerAccounts = new ApiNode.AllProfitingAccounts().fromJson(instance.getContent());

                List<String> accountName = new ArrayList<>();
                List<String> accountType = new ArrayList<>();
                List<Double> shareRatio = new ArrayList<>();
                List<Integer> seats = new ArrayList<>();
                List<Long> timeStamp = new ArrayList<>();
                List<Double> totalProfit = new ArrayList<>();
                List<Double> totalStaked = new ArrayList<>();
                List<Double> yourShareWillBe = new ArrayList<>();

                List<ApiNode.AllProfitingAccounts.AllProfitingAccountsEntry> profitingAccountList = brokerAccounts.getAccountList();
                for (int i = 0; i < profitingAccountList.size(); i++) {
                    ApiNode.AllProfitingAccounts.AllProfitingAccountsEntry entry = profitingAccountList.get(i);
                    accountName.add(entry.getName());
                    accountType.add(entry.getPType());
                    shareRatio.add(entry.getShareRatio());
                    seats.add(entry.getSeats());
                    timeStamp.add(entry.getTimeStamp());
                    totalProfit.add(entry.getTotalProfit());
                    totalStaked.add(0d);
                    yourShareWillBe.add(0d);
                }

                ProfitingListInfoSpinnerAdapter adapter = new ProfitingListInfoSpinnerAdapter(view.getContext(), R.layout.profiting_account_info_entry,
                        accountName.toArray(new String[0]), accountType.toArray(new String[0]), shareRatio.toArray(new Double[0]), seats.toArray(new Integer[0]),
                        timeStamp.toArray(new Long[0]), totalProfit.toArray(new Double[0]), totalStaked.toArray(new Double[0]), yourShareWillBe.toArray(new Double[0])
                );
                adapter.setDropDownViewResource(R.layout.send_token_select_spinner_entry_first);
                tokenSpinner.setAdapter(adapter);
            }
        });
        webHttpsTask.execute("https://" + Global.getNodeAddress() + GlobalLyra.LYRA_NODE_API_URL + "/FindAllProfitingAccounts/?timeBeginTicks=0&timeEndTicks=3155378975999999999");

        tokenSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        EditText sendFromTextView = (EditText) view.findViewById(R.id.sendTokenAmountValue);
        double amount = Global.getAvailableToken("LYR");
        if (amount != 0f) {
            sendFromTextView.setHint(String.format(Locale.US, "%s %.6f LYR", getString(R.string.send_token_available), amount));
        } else
            sendFromTextView.setHint(getString(R.string.No_tokens_available));

        Button stakingAddAccountAddButton = (Button) view.findViewById(R.id.stakingAddAccountAddButton);
        stakingAddAccountAddButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        ImageButton stakingAddAccountAmountMaxButton = (ImageButton) view.findViewById(R.id.stakingAddAccountAmountMaxButton);
        stakingAddAccountAmountMaxButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendFromTextView.setText(String.valueOf(amount));
            }
        });

    }
}
