package com.lyrawallet.Ui.FragmentMyAccountSend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lyrawallet.Api.ApiRpc;
import com.lyrawallet.Global;
import com.lyrawallet.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentMyAccountSend#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMyAccountSend extends Fragment {
    public FragmentMyAccountSend() {
        // Required empty public constructor
    }

    public static FragmentMyAccountSend newInstance(String param1, String param2) {
        FragmentMyAccountSend fragment = new FragmentMyAccountSend();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_account_send, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Button toDashboardButton = getActivity().findViewById(R.id.toDashboard);
        toDashboardButton.setVisibility(View.VISIBLE);
        Button toOpenWalletButton = getActivity().findViewById(R.id.toOpenWallet);
        toOpenWalletButton.setVisibility(View.INVISIBLE);
        Button toCloseWalletButton = getActivity().findViewById(R.id.toCloseWallet);
        toCloseWalletButton.setVisibility(View.INVISIBLE);

        new ApiRpc().act(new ApiRpc.Action().actionPool("LYR", "tether/LTT"));
    }
}
