package com.lyrawallet.Ui.FragmentStaking;

import static com.lyrawallet.Ui.UtilGetData.brokerAccountsToStakingAdapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.lyrawallet.Api.ApiRpc;
import com.lyrawallet.Api.ApiRpcActions.ApiRpcActionsBrokerAccounts;
import com.lyrawallet.Api.Network.NetworkRpc;
import com.lyrawallet.Global;
import com.lyrawallet.GlobalLyra;
import com.lyrawallet.MainActivity;
import com.lyrawallet.R;
import com.lyrawallet.Ui.FragmentManagerUser;
import com.lyrawallet.Ui.UiDialog;
import com.lyrawallet.Ui.UtilGetData;
import com.lyrawallet.Util.Concatenate;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class FragmentStaking extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    int historyLen = 0;
    int selectedIndex = -1;
    StakingGalleryAdapter adapter;
    private boolean refreshInProgress = false;
    Timer timer1;
    Timer timer2;

    public FragmentStaking() {
        // Required empty public constructor
    }

    public static class StakingEntry {
        String AccountName;
        String StakingAccountId;
        String ProfitingAccountId;
        long ExpiryDate;
        int Days;
        double Amount;

        public StakingEntry(String accountName, String stakingAccountId, String profitingAccountId,
                                   long expiryDate, int days, double amount) {
            this.AccountName = accountName;
            this.StakingAccountId = stakingAccountId;
            this.ProfitingAccountId = profitingAccountId;
            this.ExpiryDate = expiryDate;
            this.Days = days;
            this.Amount = amount;
        }
    }

    public static class ClickListener {
        public void click(int index) {

        }
    }


    public static FragmentStaking newInstance(String param1, String param2) {
        FragmentStaking fragment = new FragmentStaking();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    void restoreTimers(View view) {
        // Refresh recycler once every 5 seconds, first start after 700mS to avoid UI freezing..
        timer1 = new Timer();
        timer1.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    populateStaking(view);
                }
            }
        }, 500, 1000);
        timer2 = new Timer();
        timer2.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            NetworkRpc rpc = (NetworkRpc) new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL, Global.getWalletPassword())
                                    .execute("", "GetBrokerAccounts", Global.getSelectedAccountId());
                            rpc.setListener(new NetworkRpc.RpcTaskListener() {
                                @Override
                                public void onRpcTaskFinished(String[] output) {
                                    activity.runOnUiThread(new Runnable() {
                                        public void run() {
                                            ApiRpcActionsBrokerAccounts newBrokerAccounts = new ApiRpcActionsBrokerAccounts().fromJson(output[2]);
                                            ApiRpcActionsBrokerAccounts lastBrokerAccounts = Global.getBrokerAccounts(Concatenate.getHistoryFileName());
                                            Global.setBrokerAccounts(Concatenate.getHistoryFileName(), newBrokerAccounts);
                                            if(lastBrokerAccounts == null)
                                                return;
                                            boolean needRepopulating = false;
                                            if(lastBrokerAccounts.getStakingAccount().size() != newBrokerAccounts.getStakingAccount().size()) {
                                                needRepopulating = true;
                                            } else {
                                                for (int i = 0; i < lastBrokerAccounts.getStakingAccount().size(); i++) {
                                                    if(lastBrokerAccounts.getStakingAccount().get(i).getAmount() != newBrokerAccounts.getStakingAccount().get(i).getAmount())
                                                        needRepopulating = true;
                                                }
                                            }
                                            if(needRepopulating) {
                                                historyLen = 0;
                                                populateStaking(view);
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
        }, 500, 15 * 1000);
    }
    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(getActivity(), "onResumed called", Toast.LENGTH_LONG).show();
        restoreTimers(getView());
    }
    @Override
    public void onPause() {
        super.onPause();
        //Toast.makeText(getActivity(), "onPause called", Toast.LENGTH_LONG).show();
        timer1.cancel();
        timer2.cancel();
    }

    public void populateStaking(View view) {
        FragmentActivity activity = getActivity();
        if (activity == null || view == null) {
            refreshInProgress = false;
            return;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FragmentActivity activity = getActivity();
                if (activity == null) {
                    refreshInProgress = false;
                    return;
                }
                List<FragmentStaking.StakingEntry> entryList = brokerAccountsToStakingAdapter();
                if (entryList == null) {
                    refreshInProgress = false;
                    return;
                }
                ProgressBar progress = activity.findViewById(R.id.stakingAccountProgressBar);
                try {
                    if (entryList.size() == historyLen) {
                        refreshInProgress = false;
                        if (progress != null) {
                            progress.setVisibility(View.GONE);
                        }
                        return;
                    }
                    historyLen = entryList.size();
                } catch (NullPointerException ignored) { }
                RecyclerView account_history_recycler = activity.findViewById(R.id.stakingAccountsRecycler);
                FragmentStaking.ClickListener selectedListener = new FragmentStaking.ClickListener() {
                    @Override
                    public void click(int index) {
                        //Snackbar.make(view, String.format(Locale.US, "You clicked: %d", index), Snackbar.LENGTH_LONG)
                                //.setAction("", null).show();
                        adapter.setSelected(index);
                        selectedIndex = index;
                    }
                };
                FragmentStaking.ClickListener stakeMoreListener = new FragmentStaking.ClickListener() {
                    @Override
                    public void click(int index) {
                        //Snackbar.make(view, String.format(Locale.US, "You clicked Stake more on: %d", index), Snackbar.LENGTH_LONG)
                                //.setAction("", null).show();
                        FragmentActivity activity = getActivity();
                        final EditText passEditText = new EditText(activity);
                        // Put EditText in password mode
                        List<Pair<String, Double>> Balances = UtilGetData.getAvailableTokenList();;
                        double max = Balances.get(0).second;
                        if(max > 0) {
                            if (Balances.get(0).first.equals("LYR")) {
                                max -= GlobalLyra.LYRA_TX_FEE;
                            }
                        }
                        passEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        passEditText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        passEditText.setHint(String.format("%s: %s %s", getString(R.string.send_token_available), max, "LYR"));
                        AlertDialog dialog = new AlertDialog.Builder(activity)
                                .setTitle(R.string.Add_stake)
                                .setMessage(R.string.You_will_add_more_value_to_current_staking)
                                .setView(passEditText)
                                .setPositiveButton(R.string.Accept, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        activity.runOnUiThread(new Runnable() {
                                            public void run() {
                                                UiDialog.showDialogStatus(R.string.Adding_stake);
                                                NetworkRpc rpc = (NetworkRpc) new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL, Global.getWalletPassword())
                                                        .execute("", "AddStaking", Global.getSelectedAccountId(),
                                                                entryList.get(index).StakingAccountId,
                                                                passEditText.getText().toString());
                                                rpc.setListener(new NetworkRpc.RpcTaskListener() {
                                                    @Override
                                                    public void onRpcTaskFinished(String[] output) {
                                                        activity.runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                UiDialog.showDialogStatus(R.string.Stake_successfully_add);
                                                                restoreTimers(view);
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton(R.string.Cancel, null)
                                .create();
                        dialog.show();
                    }
                };
                FragmentStaking.ClickListener unstakeListener = new FragmentStaking.ClickListener() {
                    @Override
                    public void click(int index) {
                        //Snackbar.make(view, String.format(Locale.US, "You clicked Unstake on: %d", index), Snackbar.LENGTH_LONG)
                                //.setAction("", null).show();
                        FragmentActivity activity = getActivity();
                        AlertDialog dialog = new AlertDialog.Builder(activity)
                                .setTitle(R.string.Remove_stake)
                                .setMessage(R.string.Are_you_sure_you_want_to_remove_stake)
                                //.setView(passEditText)
                                .setPositiveButton(R.string.Accept, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        activity.runOnUiThread(new Runnable() {
                                            public void run() {
                                                UiDialog.showDialogStatus(R.string.Unstaking);
                                                NetworkRpc rpc = (NetworkRpc) new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL, Global.getWalletPassword())
                                                        .execute("", "UnStaking", Global.getSelectedAccountId(),
                                                                entryList.get(index).StakingAccountId
                                                        );
                                                rpc.setListener(new NetworkRpc.RpcTaskListener() {
                                                    @Override
                                                    public void onRpcTaskFinished(String[] output) {
                                                        activity.runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                UiDialog.showDialogStatus(R.string.Successfully_unstaked);
                                                                restoreTimers(view);
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton(R.string.Cancel, null)
                                .create();
                        dialog.show();
                    }
                };
                adapter = new StakingGalleryAdapter(
                        entryList, activity, selectedListener, stakeMoreListener, unstakeListener);
                if(selectedIndex != -1) {
                    adapter.setSelected(selectedIndex);
                }
                if(account_history_recycler != null) {
                    account_history_recycler.setAdapter(adapter);
                    account_history_recycler.setLayoutManager(
                            new LinearLayoutManager(activity));
                }
                refreshInProgress = false;
                if(progress != null) {
                    progress.setVisibility(View.GONE);
                }
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
        return inflater.inflate(R.layout.fragment_staking, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        CurvedBottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);

        TextView stakingAccountNameTextView = view.findViewById(R.id.stakingAccountNameTextView);
        stakingAccountNameTextView.setText(String.format("%s/%s", Global.getSelectedAccountName(), Global.getCurrentNetworkName()));

        ProgressBar progress = activity.findViewById(R.id.stakingAccountProgressBar);
        if (progress != null) {
            progress.setVisibility(View.VISIBLE);
        }
        ///LayoutInflater inflater = requireActivity().getLayoutInflater();

        View v = new View((MainActivity) getActivity());
        //new Accounts((MainActivity) getActivity()).promptForPassword(getContext(), v.getRootView());
        historyLen = 0;

        Button stakingAccountAddNewButton = (Button) view.findViewById(R.id.stakingAccountAddNewButton);
        stakingAccountAddNewButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new FragmentManagerUser().goToDialogCreateStaking();
            }
        });
    }
}
