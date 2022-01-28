package com.lyrawallet.Ui.FragmentAccount;

import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lyrawallet.Api.ApiRpcActions.ApiRpcActionsHistory;
import com.lyrawallet.Global;
import com.lyrawallet.GlobalLyra;
import com.lyrawallet.R;
import com.lyrawallet.Ui.FragmentAccountHistory.AccountHistoryGalleryAdapter;
import com.lyrawallet.Ui.FragmentAccountHistory.FragmentAccountHistory;
import com.lyrawallet.Ui.FragmentManagerUser;
import com.lyrawallet.Ui.UiHelpers;
import com.lyrawallet.Util.Concatenate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class FragmentAccount extends Fragment {
    static FragmentAccount fInstance = null;
    Timer timer1;
    AccountHistoryGalleryAdapter adapter;
    private NestedScrollView nestedSV;
    int historyLen = 0;
    static private Pair<Integer, List<ApiRpcActionsHistory.HistoryEntry>> history;

    public FragmentAccount() {
        fInstance = this;
    }

    public void populateHistory(View view) {
        //View v = new View((MainActivity) getActivity());
        FragmentActivity activity = getActivity();
        if (activity == null || view == null) {
            return;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FragmentActivity activity = getActivity();
                if (activity == null) {
                    return;
                }
                Pair<Integer, List<ApiRpcActionsHistory.HistoryEntry>> intHistoryList = Global.getWalletHistory(Concatenate.getHistoryFileName());
                if (intHistoryList == null) {
                     return;
                }
                List<FragmentAccountHistory.AccountHistoryEntry> entryList = new ArrayList<>();
                double valueInUsd = 0f;
                try {
                    int blockCount = intHistoryList.second.size();
                    if (blockCount > 0) {
                        blockCount -= 1;
                        for (int i = 0; i < intHistoryList.second.get(blockCount).getBalances().size(); i++) {
                            List<Pair<String, Double>> balances = intHistoryList.second.get(intHistoryList.second.size() - 1).getBalances();
                            entryList.add(new FragmentAccountHistory.AccountHistoryEntry(-1, UiHelpers.tickerToImage(balances.get(i).first),
                                    GlobalLyra.domainToSymbol(balances.get(i).first), balances.get(i).second));
                            double unitValuePerUsd = Global.getTokenPrice(new Pair<>(balances.get(i).first, "tether/USDT"));
                            if(unitValuePerUsd == 0) {
                                unitValuePerUsd = Global.getTokenPrice(new Pair<>(balances.get(i).first, "USDC"));
                            }
                            if(unitValuePerUsd == 0) {
                                unitValuePerUsd = Global.getTokenPrice(new Pair<>(balances.get(i).first, "USD"));
                            }
                            valueInUsd += unitValuePerUsd * balances.get(i).second;
                        }
                    }
                    if(intHistoryList.second.size() == historyLen) {
                        return;
                    }
                    historyLen = intHistoryList.second.size();
                } catch (NullPointerException ignored) { }
                TextView accountValueLyr = (TextView) view.findViewById(R.id.totalBalanceUsdTextView);
                accountValueLyr.setText(String.format(Locale.US, "%s $ %f", getString(R.string.Total_balance_usd), valueInUsd));
                setAccountValue(view);
                FragmentAccountHistory.ClickListener listener = new FragmentAccountHistory.ClickListener() {
                    @Override
                    public void click(int index) {
                        //new FragmentManagerUser().goToDialogTransactionDetail(new String[]{String.valueOf(EntryList.get(index).getHeight())});
                    }
                };
                adapter = new AccountHistoryGalleryAdapter(
                        entryList, activity, listener);
                RecyclerView account_history_recycler = activity.findViewById(R.id.accountTokensRecycler);
                if(account_history_recycler != null) {
                    account_history_recycler.setAdapter(adapter);
                    account_history_recycler.setLayoutManager(
                            new LinearLayoutManager(activity));
                }
            }
        });
    }

    public static void setAccountValue(View view) {
        TextView accountValueLyr = (TextView) view.findViewById(R.id.accountValueLyrTextView);
        TextView accountValueUsd = (TextView) view.findViewById(R.id.accountValueUsdTextView);
        history = Global.getWalletHistory(Concatenate.getHistoryFileName());
        if(history == null || fInstance == null)
            return;
        if(accountValueLyr == null || accountValueUsd == null || history.second == null)
            return;
        if(fInstance.getActivity() == null)
            return;
        fInstance.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                try {
                    if (history.second.size() == 0) {
                        accountValueLyr.setText(String.format(Locale.US, "%d %s", 0, "LYR"));
                        accountValueUsd.setText(String.format(Locale.US, "%d %s", 0, "USD"));
                    } else {
                        List<Pair<String, Double>> tokenList = history.second.get(history.second.size() - 1).getBalances();
                        for (int i = 0; i < tokenList.size(); i++) {
                            if( tokenList.get(i).first.equals("LYR")) {
                                accountValueLyr.setText(String.format(Locale.US, "%.8f %s", tokenList.get(i).second, "LYR"));
                                accountValueUsd.setText(String.format(Locale.US, "%.2f %s", tokenList.get(i).second * Global.getTokenPrice(new Pair<>("LYR", "USD")), "USD"));
                                break;
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void restoreTimers(View view) {
        // Refresh recycler once every 5 seconds, first start after 700mS to avoid UI freezing..
        timer1 = new Timer();
        timer1.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    populateHistory(view);
                }
            }
        }, 700, 5000);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            CurvedBottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
        historyLen = 0;
        ApiRpcActionsHistory.load(Concatenate.getHistoryFileName());
        setAccountValue(view);
        populateHistory(view);

        Button accountSendButton = (Button) view.findViewById(R.id.accountSendButton);
        accountSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new FragmentManagerUser().goToSend();
            }
        });
        Button accountReceiveButton = (Button) view.findViewById(R.id.accountReceiveButton);
        accountReceiveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new FragmentManagerUser().goToReceive();
            }
        });
        Button openAccountHistoryButton = (Button) view.findViewById(R.id.openAccountHistoryButton);
        openAccountHistoryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new FragmentManagerUser().goToAccountHistory();
            }
        });
        new Handler().postDelayed(new Runnable() {
            public void run() {
                ApiRpcActionsHistory.load(Concatenate.getHistoryFileName());
                setAccountValue(view);
            }
        }, 700);
    }
}
