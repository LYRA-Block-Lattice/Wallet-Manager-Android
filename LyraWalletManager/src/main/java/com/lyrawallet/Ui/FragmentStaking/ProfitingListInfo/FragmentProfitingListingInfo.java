package com.lyrawallet.Ui.FragmentStaking.ProfitingListInfo;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lyrawallet.Api.Network.NetworkWebHttps;
import com.lyrawallet.Api.ApiWebActions.ApiNode;
import com.lyrawallet.Global;
import com.lyrawallet.GlobalLyra;
import com.lyrawallet.R;

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
        View v = view;
        CurvedBottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.GONE);

        Spinner tokenSpinner = (Spinner) view.findViewById(R.id.stakingAddAccountSpinner);

        NetworkWebHttps webHttpsTask = new NetworkWebHttps();
        webHttpsTask.setListener(new NetworkWebHttps.WebHttpsTaskListener() {
            @Override
            public void onWebHttpsTaskFinished(NetworkWebHttps instance) {
                System.out.println("BROKER ACCOUNTS: " + instance.getContent());
                ApiNode.AllProfitingAccounts brokerAccounts = new ApiNode.AllProfitingAccounts().fromJson(instance.getContent());

                List<String> accountName = new ArrayList<>();
                List<String> accountId = new ArrayList<>();
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
                    accountId.add(entry.getAccountId());
                    accountType.add(entry.getPType());
                    shareRatio.add(entry.getShareRatio());
                    seats.add(entry.getSeats());
                    timeStamp.add(entry.getTimeStamp());
                    totalProfit.add(entry.getTotalProfit());
                    totalStaked.add(0d);
                    yourShareWillBe.add(0d);
                }

                ProfitingListInfoSpinnerAdapter adapter = new ProfitingListInfoSpinnerAdapter(view.getContext(), R.layout.profiting_account_info_entry,
                        accountName.toArray(new String[0]), accountId.toArray(new String[0]), accountType.toArray(new String[0]), shareRatio.toArray(new Double[0]), seats.toArray(new Integer[0]),
                        timeStamp.toArray(new Long[0]), totalProfit.toArray(new Double[0]), totalStaked.toArray(new Double[0]), yourShareWillBe.toArray(new Double[0])
                );
                adapter.setDropDownViewResource(R.layout.send_token_select_spinner_entry_first);
                tokenSpinner.setAdapter(adapter);
            }
        });
        webHttpsTask.execute("https://" + Global.getNodeAddress() + GlobalLyra.LYRA_NODE_API_URL + "/FindAllProfitingAccounts/?timeBeginTicks=0&timeEndTicks=3155378975999999999");

        tokenSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ProfitingListInfoSpinnerAdapter adapter = (ProfitingListInfoSpinnerAdapter) adapterView.getAdapter();
                NetworkWebHttps webHttpsTask = new NetworkWebHttps();
                webHttpsTask.setListener(new NetworkWebHttps.WebHttpsTaskListener() {
                    @Override
                    public void onWebHttpsTaskFinished(NetworkWebHttps instance) {
                        System.out.println("BROKER ACCOUNTS: " + instance.getContent());
                        ApiNode.FindAllStakings brokerAccounts = new ApiNode.FindAllStakings().fromJson(instance.getContent());
                        double totalAmountStaked = 0f;
                        for (ApiNode.FindAllStakings.FindAllStakingsEntry entry : brokerAccounts.getAccountList()) {
                            totalAmountStaked += entry.getAmount();
                        }
                        EditText stakingTokenAmountValue = (EditText) v.findViewById(R.id.stakingTokenAmountValue);
                        double amount = 0f;
                        if(stakingTokenAmountValue != null) {
                            try {
                                amount = Double.parseDouble(stakingTokenAmountValue.getText().toString());
                            } catch (NumberFormatException ignored) { }
                        }
                        adapter.setTotalStaked(i, totalAmountStaked);
                        adapter.setYourShareWillBe( i, (amount / (totalAmountStaked + amount)) * adapter.ShareRatio[i]);
                        adapter.setFetch(i);
                        Button stakingAddAccountAmountMaxButton = (Button) v.findViewById(R.id.stakingAddAccountAddButton);
                        if(brokerAccounts.getAccountList().size() < adapter.Seats[i]) {
                            stakingAddAccountAmountMaxButton.setEnabled(true);
                            if (stakingTokenAmountValue != null) {
                                stakingTokenAmountValue.setEnabled(true);
                                stakingTokenAmountValue.setText("");
                            }
                        }
                        else {
                            stakingAddAccountAmountMaxButton.setEnabled(false);
                            if (stakingTokenAmountValue != null) {
                                stakingTokenAmountValue.setEnabled(false);
                                stakingTokenAmountValue.setText("");
                            }
                        }
                    }
                });
                webHttpsTask.execute("https://" + Global.getNodeAddress() + GlobalLyra.LYRA_NODE_API_URL + "/FindAllStakings/?pftid=" + adapter.AccountId[i] + "&timeBeforeTicks=637793186320590000");
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        EditText stakingTokenAmountValue = (EditText) view.findViewById(R.id.stakingTokenAmountValue);
        stakingTokenAmountValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0) {
                    try {
                        Double.parseDouble(s.toString());
                        ProfitingListInfoSpinnerAdapter adapter = (ProfitingListInfoSpinnerAdapter) tokenSpinner.getAdapter();
                        adapter.clearFetch();
                        EditText stakingTokenAmountValue = (EditText) v.findViewById(R.id.stakingTokenAmountValue);
                        double amount = 0f;
                        if(stakingTokenAmountValue != null) {
                            try {
                                amount = Double.parseDouble(stakingTokenAmountValue.getText().toString());
                            } catch (NumberFormatException ignored) { }
                        }
                        int selectedItem = tokenSpinner.getSelectedItemPosition();
                        adapter.setYourShareWillBe(selectedItem , (amount / (adapter.TotalStaked[selectedItem] + amount)) * adapter.ShareRatio[selectedItem]);
                    } catch (NumberFormatException e) {
                        stakingTokenAmountValue.setText(s.subSequence(0, before - 1));
                    }
                }
            }
        });

        double amount = Global.getAvailableToken("LYR");
        if (amount != 0f) {
            stakingTokenAmountValue.setHint(String.format(Locale.US, "%s %.6f LYR", getString(R.string.send_token_available), amount));
        } else
            stakingTokenAmountValue.setHint(getString(R.string.No_tokens_available));

        Button stakingAddAccountAddButton = (Button) view.findViewById(R.id.stakingAddAccountAddButton);
        stakingAddAccountAddButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        ImageButton stakingAddAccountAmountMaxButton = (ImageButton) view.findViewById(R.id.stakingAddAccountAmountMaxButton);
        stakingAddAccountAmountMaxButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText stakingTokenAmountValue = (EditText) view.findViewById(R.id.stakingTokenAmountValue);
                stakingTokenAmountValue.setText(String.valueOf(amount));
            }
        });

    }
}
