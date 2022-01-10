package com.lyrawallet.Ui.FragmentReceive;

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

public class FragmentReceive extends Fragment {
    public FragmentReceive() {
        // Required empty public constructor
    }

    public static FragmentReceive newInstance(String param1, String param2) {
        FragmentReceive fragment = new FragmentReceive();
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
        return inflater.inflate(R.layout.fragment_receive, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        new ApiRpc().act(new ApiRpc.Action().actionReceive(Global.getSelectedAccountId()));
    }
}
