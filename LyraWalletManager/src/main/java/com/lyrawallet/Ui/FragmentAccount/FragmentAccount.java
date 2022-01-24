package com.lyrawallet.Ui.FragmentAccount;

import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.lyrawallet.Api.ApiRpcActions.ApiRpcActionsHistory;
import com.lyrawallet.Global;
import com.lyrawallet.R;
import com.lyrawallet.Ui.UtilGetData;
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
    List<AccountHistoryEntry> EntryList = new ArrayList<>();
    AccountHistoryGalleryAdapter adapter;
    private NestedScrollView nestedSV;
    private boolean refreshInProgress = false;
    int addCnt = 0;
    static private Pair<Integer, List<ApiRpcActionsHistory.HistoryEntry>> history;

    public static class AccountHistoryEntry {
        int Height;
        int TickerImage;
        String TickerName;
        double Quantity;

        public AccountHistoryEntry(int height, int tickerImage, String tickerName,
                                   double quantity) {
            this.Height = height;
            this.TickerImage = tickerImage;
            this.TickerName = tickerName;
            this.Quantity = quantity;
        }
    }

    public static class ClickListener{
        public void click(int index) {

        }
    }

    public FragmentAccount() {
        // Required empty public constructor
        fInstance = this;
    }

    public static FragmentAccount newInstance() {
        return new FragmentAccount();
    }

    private boolean addMoreEntrys() {
        return addMoreEntrys(50);
    }
    private boolean addMoreEntrys(int entryNr) {
        if (addCnt < EntryList.size()) {
            if(addCnt <  EntryList.size() - 50) {
                adapter.addDataSet(EntryList.subList(addCnt, addCnt + 50));
                addCnt += 50;
            } else {
                adapter.addDataSet(EntryList.subList(addCnt, EntryList.size()));
                addCnt = EntryList.size();
            }
            return true;
        }
        return false;
    }

    public void populateHistory(View view) {
        //View v = new View((MainActivity) getActivity());
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
                Pair<Integer, List<ApiRpcActionsHistory.HistoryEntry>> intHistoryList = Global.getWalletHistory(Concatenate.getHistoryFileName());
                if (intHistoryList == null) {
                    refreshInProgress = false;
                    return;
                }
                List<AccountHistoryEntry> entryList = UtilGetData.historyToAccountAdapter(intHistoryList.second);
                if (entryList == null || EntryList == null) {
                    refreshInProgress = false;
                    return;
                }
                ProgressBar progress = activity.findViewById(R.id.fragment_account_progressBar);
                if(entryList == EntryList) {
                    refreshInProgress = false;
                    if(progress != null) {
                        progress.setVisibility(View.GONE);
                    }
                    return;
                }
                EntryList = entryList;
                List<AccountHistoryEntry> entryWrList = new ArrayList<>();
                ClickListener listener = new ClickListener() {
                    @Override
                    public void click(int index) {
                        Snackbar.make(view, "clicked item index is " + index, Snackbar.LENGTH_LONG)
                                .setAction("", null).show();
                    }
                };
                adapter = new AccountHistoryGalleryAdapter(
                        entryWrList, activity, listener);
                RecyclerView account_history_recycler = activity.findViewById(R.id.account_history_recicler);
                if(account_history_recycler != null) {
                    account_history_recycler.setAdapter(adapter);
                    account_history_recycler.setLayoutManager(
                            new LinearLayoutManager(activity));
                }
                addCnt = 0;
                addMoreEntrys();
                refreshInProgress = false;
                if(progress != null) {
                    progress.setVisibility(View.GONE);
                }
            }
        });
    }

    public static void setAccountValue(View view) {
        TextView accountValueLyr = (TextView) view.findViewById(R.id.accountValueLyr_textView);
        TextView accountValueUsd = (TextView) view.findViewById(R.id.accountValueUsd_textView);
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
        refreshInProgress = false;
        // Refresh recycler once every 5 seconds, first start after 700mS to avoid UI freezing..
        timer1 = new Timer();
        timer1.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    if (!refreshInProgress) {
                        refreshInProgress = true;
                        populateHistory(view);
                        setAccountValue(view);
                    }
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
        refreshInProgress = false;
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            CurvedBottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setVisibility(View.VISIBLE);
            ProgressBar progress = activity.findViewById(R.id.fragment_account_progressBar);
            if(progress != null) {
                progress.setVisibility(View.VISIBLE);
            }

            nestedSV = view.findViewById(R.id.nestedScrollViewAccount);
            nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    // on scroll change we are checking when users scroll as bottom.
                    if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                        ProgressBar progress = activity.findViewById(R.id.fragment_account_progressBar);
                        if(progress != null) {
                            progress.setVisibility(View.VISIBLE);
                        }
                        if(!addMoreEntrys()) {
                            if(progress != null) {
                                progress.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            });
        }
        EntryList = new ArrayList<>();
        /*Snackbar.make(view, "account created", Snackbar.LENGTH_LONG)
                .setAction("", null).show();*/
        ApiRpcActionsHistory.load(Concatenate.getHistoryFileName());
        setAccountValue(view);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                ApiRpcActionsHistory.load(Concatenate.getHistoryFileName());
            }
        }, 700);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //Parcelable listState = account_history_recycler.getLayoutManager().onSaveInstanceState();
        // putting recyclerview position
        //savedInstanceState.putParcelable("RECYCLER_STATE", listState);
        // putting recyclerview items
        //savedInstanceState.putParcelableArrayList("RECYCLER", adapter.getDataSet().toArray());
        //your code

    }
}
