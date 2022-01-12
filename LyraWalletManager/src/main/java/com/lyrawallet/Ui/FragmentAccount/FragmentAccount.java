package com.lyrawallet.Ui.FragmentAccount;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.lyrawallet.Api.ApiRpcActions.ApiRpcActionsHistory;
import com.lyrawallet.Global;
import com.lyrawallet.MainActivity;
import com.lyrawallet.R;
import com.lyrawallet.Ui.UiHelpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class FragmentAccount extends Fragment {
    RecyclerView account_history_recicler;
    AccountHistoryGaleryAdapter adapter;
    ClickListener listener;
    List<ApiRpcActionsHistory.HistoryEntry> HistoryList = new ArrayList<>();
    List<AccountHistoryEntry> List = new ArrayList<>();
    static Pair<Integer, String> HistoryAndCnt = new Pair<>(-1, "");
    private boolean initialized = false;
    private boolean refreshInProgress = false;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public class AccountHistoryEntry {
        int TickerImage;
        String TickerName;
        double Quantity;
        double ValueUsdPerUnit;

        AccountHistoryEntry(int tickerImage, String tickerName,
                            double quantity, double valueUsdPerUnit) {
            this.TickerImage = tickerImage;
            this.TickerName = tickerName;
            this.Quantity = quantity;
            this.ValueUsdPerUnit = valueUsdPerUnit;
        }
    }

    public static class ClickListener{
        public void click(int index) {

        }
    }

    // Sample data for RecyclerView
    private List<AccountHistoryEntry> getData(View view) {
        //List<ApiRpcActionsHistory.HistoryEntry> historyList = ApiRpcActionsHistory.loadHistory(ApiRpcActionsHistory.load(ApiRpcActionsHistory.getHistoryFileName()));
        Pair<Integer, String> historyAndCnt = Global.getWalletHistory(ApiRpcActionsHistory.getHistoryFileName());
        if(historyAndCnt == null || historyAndCnt.second == null || HistoryAndCnt == null || HistoryAndCnt.second == null) {
            String a1 = "null ";
            String a2 = "null ";
            String a3 = "null ";
            String a4 = "null ";
            if(historyAndCnt != null) {
                a1 = "non_null ";
                if(historyAndCnt.second != null) {
                    a2 = "non_null ";
                }
            }
            if(HistoryAndCnt != null) {
                a3 = "non_null ";
                if(HistoryAndCnt.second != null) {
                    a4 = "non_null ";
                }
            }
            Snackbar.make(view, a1 +a2 +a3 +a4, Snackbar.LENGTH_LONG)
                    .setAction("", null).show();
            ApiRpcActionsHistory.load(ApiRpcActionsHistory.getHistoryFileName());
            initialized = false;
            return null;
        }
        if(HistoryAndCnt.second.length() != historyAndCnt.second.length() || !initialized) {
            initialized = true;
            HistoryAndCnt = historyAndCnt;
        } else {
            return null;
        }
        List<ApiRpcActionsHistory.HistoryEntry> historyList = ApiRpcActionsHistory.loadHistory(HistoryAndCnt.second);
        if(historyList == null) {
            return null;
        }
        if(historyList.size() == HistoryList.size()) {
            return List;
        }
        List.clear();
        for (int i = 0; i < historyList.size(); i++) {
            int size = historyList.get(i).getChanges().size();
            Pair<String, Double> tokenAmount;
            if(size > 1) {
                tokenAmount = historyList.get(i).getChanges().get(1);
            } else  {
                tokenAmount = historyList.get(i).getChanges().get(0);
            }
            int icon = R.mipmap.ic_unknown_foreground;
            switch (tokenAmount.first) {
                case "LYR":
                    icon = R.mipmap.ic_lyra_foreground;
                    break;
                case "tether/USDC":
                    icon = R.mipmap.ic_usdc_foreground;
                    break;
                case "tether/USDT":
                    icon = R.mipmap.ic_usdt_foreground;
                    break;
                case "tether/ETH":
                    icon = R.mipmap.ic_eth_foreground;
                    break;
                default:
                    icon = R.mipmap.ic_unknown_foreground;
            }
            List.add(0, new AccountHistoryEntry(icon,
                    tokenAmount.first, tokenAmount.second, 0.00021f));
        }
        return List;
    }

    public FragmentAccount() {
        // Required empty public constructor
    }

    public static FragmentAccount newInstance(String param1, String param2) {
        FragmentAccount fragment = new FragmentAccount();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void populateHistory(View view) {
        //View v = new View((MainActivity) getActivity());
        listener = new ClickListener() {
            @Override
            public void click(int index){
                //Toast.makeText(v,"clicked item index is "+index,Toast.LENGTH_LONG).show();
                Snackbar.make(view, "clicked item index is "+index, Snackbar.LENGTH_LONG)
                        .setAction("", null).show();
            }
        };
        List<AccountHistoryEntry> list = getData(view);
        if(list == null) {
            refreshInProgress = false;
            return;
        }
        FragmentActivity activity = getActivity();
        if(activity == null) {
            refreshInProgress = false;
            return;
        }
        adapter = new AccountHistoryGaleryAdapter(
                list, activity, listener);
        account_history_recicler = activity.findViewById(R.id.account_history_recicler);
        account_history_recicler.setAdapter(adapter);
        account_history_recicler.setLayoutManager(
                new LinearLayoutManager(activity));
        ProgressBar progress = activity.findViewById(R.id.fragment_account_progressBar);
        progress.setVisibility(View.GONE);
        refreshInProgress = false;
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
        /*Snackbar.make(getActivity().findViewById(R.id.nav_host_fragment_content_main), "account created", Snackbar.LENGTH_LONG)
                .setAction("", null).show();*/
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        if(activity != null) {
            CurvedBottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setVisibility(View.VISIBLE);
            ProgressBar progress = activity.findViewById(R.id.fragment_account_progressBar);
            progress.setVisibility(View.VISIBLE);
        }
        ApiRpcActionsHistory.load(ApiRpcActionsHistory.getHistoryFileName());
        HistoryAndCnt = new Pair<>(-1, "");
        //populateHistory(view);
        //populateHistory(view);
        /*Snackbar.make(view, "account created", Snackbar.LENGTH_LONG)
                .setAction("", null).show();*/

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run () {
                FragmentActivity activity = getActivity();
                if(activity == null) {
                    return;
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(refreshInProgress == false) {
                            refreshInProgress = true;
                            populateHistory(view);
                        }
                    }
                });
            }
        },1000,1000);

    }
}
