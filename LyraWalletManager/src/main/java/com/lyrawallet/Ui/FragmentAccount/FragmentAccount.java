package com.lyrawallet.Ui.FragmentAccount;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.lyrawallet.MainActivity;
import com.lyrawallet.R;
import com.lyrawallet.Ui.UiHelpers;

import java.util.ArrayList;
import java.util.List;

import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class FragmentAccount extends Fragment {
    RecyclerView account_history_recicler;
    AccountHistoryGaleryAdapter adapter;
    ClickListener listener;
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
    private List<AccountHistoryEntry> getData() {
        List<AccountHistoryEntry> list = new ArrayList<>();
        list.add(new AccountHistoryEntry(R.mipmap.ic_lyra_dashboard_icon,
                "LYR", 1000f, 0.00021f));
        list.add(new AccountHistoryEntry(R.mipmap.ic_lyra_dashboard_icon,
                "LYR", 1400f, 0.00021f));
        list.add(new AccountHistoryEntry(R.mipmap.ic_lyra_dashboard_icon,
                "LYR", 1800f, 0.00021f));
        list.add(new AccountHistoryEntry(R.mipmap.ic_lyra_dashboard_icon,
                "LYR", 2800f, 0.00021f));
        list.add(new AccountHistoryEntry(R.mipmap.ic_lyra_dashboard_icon,
                "LYR", 2400f, 0.00021f));
        list.add(new AccountHistoryEntry(R.mipmap.ic_lyra_dashboard_icon,
                "LYR", 2600f, 0.00021f));
        list.add(new AccountHistoryEntry(R.mipmap.ic_lyra_dashboard_icon,
                "LYR", 3800f, 0.00021f));
        list.add(new AccountHistoryEntry(R.mipmap.ic_lyra_dashboard_icon,
                "LYR", 3400f, 0.00021f));
        list.add(new AccountHistoryEntry(R.mipmap.ic_lyra_dashboard_icon,
                "LYR", 3600f, 0.00021f));
        return list;
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
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        CurvedBottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);

        UiHelpers.closeKeyboard(view);

        Spinner accountsSpinner = getActivity().findViewById(R.id.accountSpinner);
        if(accountsSpinner != null) {
            accountsSpinner.setVisibility(View.VISIBLE);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View v = new View((MainActivity) getActivity());
        //new Accounts((MainActivity) getActivity()).promptForPassword(getContext(), v.getRootView());
        account_history_recicler = getActivity().findViewById(R.id.account_history_recicler);
        listener = new ClickListener() {
            @Override
            public void click(int index){
                //Toast.makeText(v,"clicked item index is "+index,Toast.LENGTH_LONG).show();
                Snackbar.make(view, "clicked item index is "+index, Snackbar.LENGTH_LONG)
                        .setAction("", null).show();
            }
        };
        List<AccountHistoryEntry> list = new ArrayList<>();
        list = getData();
        adapter
                = new AccountHistoryGaleryAdapter(
                list, getActivity(), listener);
        account_history_recicler.setAdapter(adapter);
        account_history_recicler.setLayoutManager(
                new LinearLayoutManager(getActivity()));
    }
}
