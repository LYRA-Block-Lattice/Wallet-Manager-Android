package com.lyrawallet.Ui.FragmentStaking.StakingProfitingListInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.lyrawallet.Api.Network.NetworkRpc;
import com.lyrawallet.Api.Network.NetworkWebHttps;
import com.lyrawallet.Api.ApiWebActions.ApiNode;
import com.lyrawallet.Global;
import com.lyrawallet.GlobalLyra;
import com.lyrawallet.R;
import com.lyrawallet.Ui.FragmentManagerUser;
import com.lyrawallet.Ui.UiDialog;
import com.lyrawallet.Ui.UiHelpers;
import com.lyrawallet.Util.UtilTextFilters;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class FragmentProfitingListingInfo extends Fragment {
    int historyLen = 0;
    StakingProfitingListInfoSpinnerAdapter adapter;
    private boolean refreshInProgress = false;
    Timer timer1;
    Timer timer2;
    int occupiedSeatsForSelectedAccount = 0;

    public FragmentProfitingListingInfo() {
        // Required empty public constructor
    }
    public String getTime(long time){
        Date endDate = new Date(time);
        Format format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
        return format.format(endDate);
    }

    void check(View v) {
        EditText stakingTokenAmountValue = (EditText) v.findViewById(R.id.dialogStakingTokenAmountValue);
        EditText stakingAddAccountDaysValue = (EditText) v.findViewById(R.id.stakingAddAccountDaysValue);
        Button stakingAddAccountPreviewButton = (Button) v.findViewById(R.id.stakingAddAccountPreviewButton);
        Spinner tokenSpinner = (Spinner) v.findViewById(R.id.dialogStakingAddAccountSpinner);
        StakingProfitingListInfoSpinnerAdapter adapter = (StakingProfitingListInfoSpinnerAdapter) tokenSpinner.getAdapter();
        int selectedItem = tokenSpinner.getSelectedItemPosition();
        int stakingDays = 0;
        if(stakingAddAccountDaysValue.getText().toString().length() > 0) {
            stakingDays = Integer.parseInt(stakingAddAccountDaysValue.getText().toString());
        }
        double amount = Global.getAvailableToken("LYR");
        double amountInput = 0f;
        if(stakingTokenAmountValue.getText().toString().length() > 0) {
            amountInput = Double.parseDouble(stakingTokenAmountValue.getText().toString());
        }
        if(occupiedSeatsForSelectedAccount < adapter.Seats[selectedItem] &&
                stakingDays >= GlobalLyra.LYRA_STAKE_MIN_DAYS &&
                stakingDays <= GlobalLyra.LYRA_STAKE_MAX_DAYS &&
                amount - GlobalLyra.LYRA_TX_FEE >= amountInput &&
                amountInput > 0
        ) {
            stakingAddAccountPreviewButton.setEnabled(true);
            //stakingTokenAmountValue.setEnabled(true);
        } else {
            stakingAddAccountPreviewButton.setEnabled(false);
            //stakingTokenAmountValue.setEnabled(false);
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
        return inflater.inflate(R.layout.fragment_staking_add_account, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Activity activity = getActivity();
        if (activity == null)
            return;
        View v = view;
        CurvedBottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.GONE);

        Spinner tokenSpinner = (Spinner) view.findViewById(R.id.dialogStakingAddAccountSpinner);

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

                StakingProfitingListInfoSpinnerAdapter adapter = new StakingProfitingListInfoSpinnerAdapter(view.getContext(), R.layout.profiting_account_info_entry,
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
                StakingProfitingListInfoSpinnerAdapter adapter = (StakingProfitingListInfoSpinnerAdapter) adapterView.getAdapter();
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
                        EditText stakingTokenAmountValue = (EditText) v.findViewById(R.id.dialogStakingTokenAmountValue);
                        double amount = 0f;
                        if(stakingTokenAmountValue != null) {
                            try {
                                amount = Double.parseDouble(stakingTokenAmountValue.getText().toString());
                            } catch (NumberFormatException ignored) { }
                            stakingTokenAmountValue.setText("");
                        }
                        adapter.setTotalStaked(i, totalAmountStaked);
                        adapter.setYourShareWillBe( i, (amount / (totalAmountStaked + amount)) * adapter.ShareRatio[i]);
                        adapter.setFetch(i);
                        occupiedSeatsForSelectedAccount = brokerAccounts.getAccountList().size();
                        check(v);
                    }
                });
                webHttpsTask.execute("https://" + Global.getNodeAddress() + GlobalLyra.LYRA_NODE_API_URL + "/FindAllStakings/?pftid=" + adapter.AccountId[i] + "&timeBeforeTicks=637793186320590000");
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        EditText stakingTokenAmountValue = (EditText) view.findViewById(R.id.dialogStakingTokenAmountValue);
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
                        StakingProfitingListInfoSpinnerAdapter adapter = (StakingProfitingListInfoSpinnerAdapter) tokenSpinner.getAdapter();
                        adapter.clearFetch();
                        EditText stakingTokenAmountValue = (EditText) v.findViewById(R.id.dialogStakingTokenAmountValue);
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
                check(v);
            }
        });

        EditText stakingAddAccountNameValue = (EditText) view.findViewById(R.id.stakingAddAccountNameValue);
        stakingAddAccountNameValue.setFilters(new InputFilter[]{UtilTextFilters.getCharactersDigitsAndSpaceFilter()});

        EditText stakingAddAccountDaysValue = (EditText) view.findViewById(R.id.stakingAddAccountDaysValue);
        stakingAddAccountDaysValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    try {
                        int days = Integer.parseInt(s.toString());
                        if(days > GlobalLyra.LYRA_STAKE_MAX_DAYS)
                            stakingAddAccountDaysValue.setText(s.subSequence(0, before - 1));
                    } catch (NumberFormatException ignore) {
                        stakingAddAccountDaysValue.setText(s.subSequence(0, before - 1));
                    }
                }
                check(v);
            }
        });

//dialogStakingTokenAmountValue    dialogStakingAddAccountDaysValue

        double amount = Global.getAvailableToken("LYR");
        if (amount != 0f) {
            stakingTokenAmountValue.setHint(String.format(Locale.US, "%s %.6f LYR", getString(R.string.send_token_available), amount));
        } else
            stakingTokenAmountValue.setHint(getString(R.string.No_tokens_available));

        Button stakingAddAccountPreviewButton = (Button) view.findViewById(R.id.stakingAddAccountPreviewButton);
        stakingAddAccountPreviewButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String StakingTokenAmountValue = stakingTokenAmountValue.getText().toString();
                String StakingAddAccountNameValue = stakingAddAccountNameValue.getText().toString();
                String StakingAddAccountDaysValue = stakingAddAccountDaysValue.getText().toString();
                Spinner tokenSpinner = (Spinner) view.findViewById(R.id.dialogStakingAddAccountSpinner);
                StakingProfitingListInfoSpinnerAdapter adapter = (StakingProfitingListInfoSpinnerAdapter) tokenSpinner.getAdapter();
                int selectedItem = tokenSpinner.getSelectedItemPosition();
                //adapter.AccountId[tokenSpinner.getSelectedItemPosition()];

                UiHelpers.closeKeyboard(view);
                final AlertDialog.Builder alert = new AlertDialog.Builder(activity, R.style.GeneralDialogTheme);
                View mView = getLayoutInflater().inflate(R.layout.dialog_staking_add_account, null);
                alert.setView(mView);
                final AlertDialog alertDialog = alert.create();
                alertDialog.setCanceledOnTouchOutside(false);

                /*ImageButton closeButton = (ImageButton) mView.findViewById(R.id.dialogSendClose);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });*/
                TextView dialogStakingTokenAmountValue = (TextView) mView.findViewById(R.id.dialogStakingTokenAmountValue);
                TextView dialogStakingAccountInfoAccountName = (TextView) mView.findViewById(R.id.dialogStakingAccountInfoAccountName);
                TextView dialogStakingAccountInfoAccountType = (TextView) mView.findViewById(R.id.dialogStakingAccountInfoAccountType);
                TextView dialogStakingAccountInfoShareRatio = (TextView) mView.findViewById(R.id.dialogStakingAccountInfoShareRatio);
                TextView dialogStakingAccountInfoSeats = (TextView) mView.findViewById(R.id.dialogStakingAccountInfoSeats);
                TextView dialogStakingAccountInfoTimeStamp = (TextView) mView.findViewById(R.id.dialogStakingAccountInfoTimeStamp);
                TextView dialogStakingAccountInfoTotalProfit = (TextView) mView.findViewById(R.id.dialogStakingAccountInfoTotalProfit);
                TextView dialogStakingAccountInfoTotalStaked = (TextView) mView.findViewById(R.id.dialogStakingAccountInfoTotalStaked);
                TextView dialogStakingAccountInfoYourShare = (TextView) mView.findViewById(R.id.dialogStakingAccountInfoYourShare);
                TextView dialogStakingAddAccountDaysValue = (TextView) mView.findViewById(R.id.dialogStakingAddAccountDaysValue);
                TextView dialogStakingAddAccountNameValue = (TextView) mView.findViewById(R.id.dialogStakingAddAccountNameValue);

                dialogStakingTokenAmountValue.setText(String.format("%s LYR", StakingTokenAmountValue));
                dialogStakingAccountInfoAccountName.setText(adapter.AccountName[selectedItem]);
                dialogStakingAccountInfoAccountType.setText(adapter.AccountType[selectedItem]);
                //.setText(adapter.AccountId[selectedItem]);
                dialogStakingAccountInfoShareRatio.setText(String.format(Locale.US, "%.3f%%", adapter.ShareRatio[selectedItem] * 100));
                dialogStakingAccountInfoSeats.setText(String.format(Locale.US, "%d", adapter.Seats[selectedItem]));
                dialogStakingAccountInfoTimeStamp.setText(getTime(adapter.TimeStamp[selectedItem]));
                dialogStakingAccountInfoTotalProfit.setText(String.format(Locale.US, "%.8f", adapter.TotalProfit[selectedItem]));
                dialogStakingAccountInfoTotalStaked.setText(String.format(Locale.US, "%.8f", adapter.TotalStaked[selectedItem]));
                dialogStakingAccountInfoYourShare.setText(String.format(Locale.US, "%.3f%%", adapter.YourShareWillBe[selectedItem] * 100));
                dialogStakingAddAccountNameValue.setText(StakingAddAccountNameValue);
                dialogStakingAddAccountDaysValue.setText(String.format("%s %s", StakingAddAccountDaysValue, getString(R.string.Days)));
                alertDialog.show();

                Button dialogStakingAddAccountStakeButton = (Button) mView.findViewById(R.id.dialogStakingAddAccountStakeButton);
                dialogStakingAddAccountStakeButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Activity activity = getActivity();
                        if (activity == null)
                            return;
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    int selectedItem = tokenSpinner.getSelectedItemPosition();
                                    dialogStakingAccountInfoAccountName.setText(adapter.AccountName[selectedItem]);
                                    UiDialog.showDialogStatus(R.string.Creating_staking_account);
                                    NetworkRpc rpc = (NetworkRpc) new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL, Global.getWalletPassword())
                                            .execute("", "CreateStakingAccount", Global.getSelectedAccountId(),
                                                    dialogStakingAddAccountNameValue.getText().toString(), adapter.AccountId[selectedItem],
                                                    StakingAddAccountDaysValue, "true");
                                    rpc.setListener(new NetworkRpc.RpcTaskListener() {
                                        @Override
                                        public void onRpcTaskFinished(String[] output) {
                                            System.out.println("RPC CREATE STAKING ACCOUNT: " + output[0] + output[1] + output[2]);
                                            Activity activity = getActivity();
                                            if (activity == null)
                                                return;
                                            activity.runOnUiThread(new Runnable() {
                                                public void run() {
                                                    try {
                                                        JSONObject objRsp = new JSONObject(output[2]);
                                                        if (objRsp.isNull("stkid")) {
                                                            UiDialog.showDialogStatus(R.string.Error_adding_staking);
                                                            return;
                                                        }
                                                        UiDialog.showDialogStatus(R.string.Adding_stake);
                                                        NetworkRpc rpc = (NetworkRpc) new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL, Global.getWalletPassword())
                                                                .execute("", "AddStaking", Global.getSelectedAccountId(),
                                                                        objRsp.getString("stkid"),
                                                                        StakingTokenAmountValue);
                                                        rpc.setListener(new NetworkRpc.RpcTaskListener() {
                                                            @Override
                                                            public void onRpcTaskFinished(String[] output) {
                                                                System.out.println("RPC ADD STAKING: " + output[0] + output[1] + output[2]);
                                                                activity.runOnUiThread(new Runnable() {
                                                                    public void run() {
                                                                        UiDialog.showDialogStatus(R.string.Finished_to_add_stake);
                                                                        alertDialog.dismiss();
                                                                        new FragmentManagerUser().goToStaking();
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    } catch (NumberFormatException | JSONException ex) {
                                                        Snackbar.make(view, "An error occurred when trying to add staking.", Snackbar.LENGTH_LONG)
                                                                .setAction("", null).show();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                } catch (NumberFormatException ex) {
                                    Snackbar.make(view, "An error occurred when trying to create staking account.", Snackbar.LENGTH_LONG)
                                            .setAction("", null).show();
                                }
                            }
                        });
                    }
                });
            }
        });

        ImageButton stakingAddAccountAmountMaxButton = (ImageButton) view.findViewById(R.id.stakingAddAccountAmountMaxButton);
        stakingAddAccountAmountMaxButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText stakingTokenAmountValue = (EditText) view.findViewById(R.id.dialogStakingTokenAmountValue);
                stakingTokenAmountValue.setText(String.valueOf(amount));
            }
        });

    }
}
