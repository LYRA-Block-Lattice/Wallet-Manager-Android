package com.lyrawallet.Ui.FragmentStaking;

import static com.lyrawallet.Ui.UtilGetData.brokerAccountsToStakingAdapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lyrawallet.Api.ApiRpc;
import com.lyrawallet.Global;
import com.lyrawallet.MainActivity;
import com.lyrawallet.R;
import com.lyrawallet.Ui.FragmentManagerUser;

import java.util.List;
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
        }, 100, 1000);
        timer2 = new Timer();
        timer2.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            new ApiRpc().act(new ApiRpc.Action().actionGetBrokerAccounts(Global.getSelectedAccountId()));
                        }
                    });
                }
            }
        }, 100, 60 * 1000);
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
                FragmentStaking.ClickListener listener = new FragmentStaking.ClickListener() {
                    @Override
                    public void click(int index) {
                        //new FragmentManagerUser().goToDialogStakingDetail(new String[]{entryList.get(index).AccountName});
                    }
                };
                adapter = new StakingGalleryAdapter(
                        entryList, activity, listener);
                RecyclerView account_history_recycler = activity.findViewById(R.id.stakingAccountsRecycler);
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
        if (activity != null) {
            CurvedBottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setVisibility(View.VISIBLE);
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
}
