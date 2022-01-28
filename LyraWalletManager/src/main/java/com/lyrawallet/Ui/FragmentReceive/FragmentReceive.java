package com.lyrawallet.Ui.FragmentReceive;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.WriterException;
import com.lyrawallet.Api.ApiRpc;
import com.lyrawallet.Global;
import com.lyrawallet.R;
import com.lyrawallet.Ui.FragmentManagerUser;
import com.lyrawallet.Ui.UiHelpers;

import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class FragmentReceive extends Fragment {
    private static String RequestQrStr = null;

    public static void setRequestQrStr(String req) {
        RequestQrStr = req;
    }

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
        //new ApiRpc().act(new ApiRpc.Action().actionReceive(Global.getSelectedAccountId()));
        new ApiRpc().act(new ApiRpc.Action().actionBalance("Receive", Global.getSelectedAccountId()));
        Activity activity = getActivity();
        if(activity == null)
            return;
        CurvedBottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.GONE);

        TextView addressTextView = getActivity().findViewById(R.id.receiveQrCodeCopyAddress);
        ImageView qrImageView = getActivity().findViewById(R.id.receiveQrCodeImageView);


        try {
            if(RequestQrStr == null)
                UiHelpers.textToQrEncode(Global.getSelectedAccountId(), qrImageView);
            else
                UiHelpers.textToQrEncode(RequestQrStr, qrImageView);
            addressTextView.setText(UiHelpers.getShortAccountId(Global.getSelectedAccountId(), 7));
        } catch (WriterException e) {
            e.printStackTrace();
        }

        addressTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                if(activity == null)
                    return;
                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(activity.getString(R.string.receive_scan_address_copied), Global.getSelectedAccountId());
                clipboard.setPrimaryClip(clip);

                Snackbar.make(activity.findViewById(R.id.nav_host_fragment_content_main), activity.getString(R.string.receive_scan_address_copied), Snackbar.LENGTH_SHORT)
                        .setAction("", null).show();            }
        });

        Button bookAccountButton = (Button) view.findViewById(R.id.receiveQrRequestButton);
        bookAccountButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*DialogFragment newFragment = new FragmentReceiveDialog();
                assert getFragmentManager() != null;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                newFragment.show(ft, "dialog");*/
                RequestQrStr = null;
                new FragmentManagerUser().goToDialogReceive();
            }
        });
    }
}
