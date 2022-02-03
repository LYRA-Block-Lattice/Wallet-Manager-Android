package com.lyrawallet.Ui.FragmentDex;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lyrawallet.Global;
import com.lyrawallet.MainActivity;
import com.lyrawallet.R;

import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class FragmentDex extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public FragmentDex() {
        // Required empty public constructor
    }

    public static FragmentDex newInstance(String param1, String param2) {
        FragmentDex fragment = new FragmentDex();
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
        return inflater.inflate(R.layout.fragment_dex, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        CurvedBottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);

        TextView dexAccountNameTextView = view.findViewById(R.id.dexAccountNameTextView);
        dexAccountNameTextView.setText(String.format("%s/%s", Global.getSelectedAccountName(), Global.getCurrentNetworkName()));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View v = new View((MainActivity) getActivity());
        //new Accounts((MainActivity) getActivity()).promptForPassword(getContext(), v.getRootView());
    }
}
