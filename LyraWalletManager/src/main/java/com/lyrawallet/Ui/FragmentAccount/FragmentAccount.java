package com.lyrawallet.Ui.FragmentAccount;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
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

    Timer timer;

    List<AccountHistoryEntry> EntryList = new ArrayList<>();
    private boolean refreshInProgress = false;
    int scrolled = 0;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final String ARG_PARAM1 = "param1";
    private final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public static class AccountHistoryEntry {
        int TickerImage;
        String TickerName;
        double Quantity;
        double ValueUsdPerUnit;

        public AccountHistoryEntry(int tickerImage, String tickerName,
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

    public FragmentAccount() {
        // Required empty public constructor
    }

    public static FragmentAccount newInstance(String param1, String param2) {
        FragmentAccount fragment = new FragmentAccount();
        Bundle args = new Bundle();
        args.putString(fragment.ARG_PARAM1, param1);
        args.putString(fragment.ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void populateHistory(View view, @Nullable Bundle savedInstanceState) {
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
                List<AccountHistoryEntry> entryList = Global.getFragmentAccountHistory(ApiRpcActionsHistory.getHistoryFileName());//getData();
                if (entryList == null || EntryList == null) {
                    refreshInProgress = false;
                    return;
                }
                if(entryList == EntryList) {
                    refreshInProgress = false;
                    return;
                }
                EntryList = entryList;
                ClickListener listener = new ClickListener() {
                    @Override
                    public void click(int index) {
                        //Toast.makeText(v,"clicked item index is "+index,Toast.LENGTH_LONG).show();
                        Snackbar.make(view, "clicked item index is " + index, Snackbar.LENGTH_LONG)
                                .setAction("", null).show();
                    }
                };
                AccountHistoryGaleryAdapter adapter = new AccountHistoryGaleryAdapter(
                        entryList, activity, listener);
                RecyclerView account_history_recycler = activity.findViewById(R.id.account_history_recicler);
                if(account_history_recycler != null) {
                    account_history_recycler.setAdapter(adapter);
                    account_history_recycler.setLayoutManager(
                            new LinearLayoutManager(activity));
                }
                ProgressBar progress = activity.findViewById(R.id.fragment_account_progressBar);
                if(progress != null) {
                    progress.setVisibility(View.GONE);
                }
            }
        });
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
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        refreshInProgress = false;
        timer.cancel();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            CurvedBottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setVisibility(View.VISIBLE);
            ProgressBar progress = activity.findViewById(R.id.fragment_account_progressBar);
            progress.setVisibility(View.VISIBLE);
        }
        EntryList = new ArrayList<>();
        /*Snackbar.make(view, "account created", Snackbar.LENGTH_LONG)
                .setAction("", null).show();*/
        ApiRpcActionsHistory.load(ApiRpcActionsHistory.getHistoryFileName());
        refreshInProgress = false;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    if (!refreshInProgress) {
                        refreshInProgress = true;
                        populateHistory(view, savedInstanceState);
                    }
                }
            }
        }, 700, 5000);
        /*new Handler().postDelayed(new Runnable() {
            public void run() {
                ApiRpcActionsHistory.load(ApiRpcActionsHistory.getHistoryFileName());
            }
        }, 700);*/
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
