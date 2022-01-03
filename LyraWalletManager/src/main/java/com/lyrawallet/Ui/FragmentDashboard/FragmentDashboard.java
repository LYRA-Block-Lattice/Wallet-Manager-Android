package com.lyrawallet.Ui.FragmentDashboard;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lyrawallet.MainActivity;
import com.lyrawallet.R;

public class FragmentDashboard extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public FragmentDashboard() {
        // Required empty public constructor
    }

    public static FragmentDashboard newInstance(String param1, String param2) {
        FragmentDashboard fragment = new FragmentDashboard();
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
        return inflater.inflate(R.layout.fragment_dash, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Button toDashboardButton = getActivity().findViewById(R.id.toDashboard);
        if(toDashboardButton != null) {
            toDashboardButton.setVisibility(View.INVISIBLE);
        }
        Button toOpenWalletButton = getActivity().findViewById(R.id.toOpenWallet);
        if(toOpenWalletButton != null) {
            toOpenWalletButton.setVisibility(View.INVISIBLE);
        }
        Button toCloseWalletButton = getActivity().findViewById(R.id.toCloseWallet);
        if(toCloseWalletButton != null) {
            toCloseWalletButton.setVisibility(View.VISIBLE);
        }
        Spinner accountsSpinner = getActivity().findViewById(R.id.accountSpinner);
        if(accountsSpinner != null) {
            accountsSpinner.setVisibility(View.VISIBLE);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View v = new View((MainActivity) getActivity());
        //new Accounts((MainActivity) getActivity()).promptForPassword(getContext(), v.getRootView());
    }
}
