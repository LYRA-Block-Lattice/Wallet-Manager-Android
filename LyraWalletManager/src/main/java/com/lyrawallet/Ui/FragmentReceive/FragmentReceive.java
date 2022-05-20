package com.lyrawallet.Ui.FragmentReceive;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.WriterException;
import com.lyrawallet.Api.ApiRpc;
import com.lyrawallet.Global;
import com.lyrawallet.GlobalLyra;
import com.lyrawallet.R;
import com.lyrawallet.Ui.FragmentManagerUser;
import com.lyrawallet.Ui.UiHelpers;

import java.util.Locale;

import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class FragmentReceive extends Fragment {
    private static String RequestQrStr = null;
    String Ticker = null;
    String Provider = null;
    String ContractAddress = null;
    String DepositAddress = null;
    double MinDeposit = 0f;
    double DepositionFee = 0f;
    int Confirmations = 0;

    public static void setRequestQrStr(String req) {
        RequestQrStr = req;
    }

    public FragmentReceive() {
        // Required empty public constructor
    }

    public FragmentReceive(String ticker, String provider, String contractAddress, String depositAddress, double minDeposit, double depositionFee, int confirmations) {
        Ticker = ticker;
        Provider = provider;
        ContractAddress = contractAddress;
        DepositAddress = depositAddress;
        MinDeposit = minDeposit;
        DepositionFee = depositionFee;
        Confirmations = confirmations;
    }

    public static FragmentReceive newInstance(String param1, String param2) {
        return new FragmentReceive();
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


        Button requestButton = (Button) view.findViewById(R.id.receiveQrRequestButton);
        requestButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*DialogFragment newFragment = new FragmentReceiveDialog();
                assert getFragmentManager() != null;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                newFragment.show(ft, "dialog");*/
                RequestQrStr = null;
                new FragmentManagerUser().goToDialogReceive();
            }
        });
        try {
            if(DepositAddress != null)
                UiHelpers.textToQrEncode(DepositAddress, qrImageView);
            else if(RequestQrStr == null)
                UiHelpers.textToQrEncode(Global.getSelectedAccountId(), qrImageView);
            else
                UiHelpers.textToQrEncode(RequestQrStr, qrImageView);
            if(DepositAddress != null) {
                addressTextView.setText(UiHelpers.getShortAccountId(DepositAddress, 7));
                requestButton.setVisibility(View.GONE);
                FrameLayout dexReceiveFragmentLayout = view.findViewById(R.id.dexReceiveFrameLayout);
                dexReceiveFragmentLayout.setVisibility(View.VISIBLE);

                TextView dexReceiveTokenNameTextView = view.findViewById(R.id.dexReceiveTokenNameTextView);
                TextView dexReceiveTickerTextView = view.findViewById(R.id.dexReceiveTickerTextView);
                TextView dexReceiveProviderTextView = view.findViewById(R.id.dexReceiveProviderTextView);
                TextView dexReceiveContractTextView = view.findViewById(R.id.dexReceiveContractTextView);
                TextView dexReceiveContractAddressCopyTextView = view.findViewById(R.id.dexReceiveContractAddressCopyTextView);
                TextView dexReceiveContractAddressViewTextView = view.findViewById(R.id.dexReceiveContractAddressViewTextView);
                TextView dexReceiveMinimalDepositTextView = view.findViewById(R.id.dexReceiveMinimalDepositTextView);
                TextView dexReceiveDepositionFeeTextView = view.findViewById(R.id.dexReceiveDepositionFeeTextView);
                TextView dexReceiveConfirmationsTextView = view.findViewById(R.id.dexReceiveConfirmationsTextView);
                dexReceiveTokenNameTextView.setText(GlobalLyra.tickerToTokenName(Ticker));
                dexReceiveTickerTextView.setText(Ticker);
                dexReceiveProviderTextView.setText(Provider);
                if(ContractAddress == null || ContractAddress.equals("null")) {
                    dexReceiveContractAddressCopyTextView.setVisibility(View.GONE);
                    dexReceiveContractAddressViewTextView.setVisibility(View.GONE);
                    dexReceiveContractTextView.setText(" ");
                } else {
                    dexReceiveContractTextView.setText(UiHelpers.getShortAccountId(ContractAddress, 12));
                }
                dexReceiveMinimalDepositTextView.setText(String.format(Locale.US, "%f %s", MinDeposit, Ticker));
                dexReceiveDepositionFeeTextView.setText(String.format(Locale.US, "%f %s", DepositionFee, Ticker));
                dexReceiveConfirmationsTextView.setText(String.format(Locale.US, "%d %s", Confirmations, Confirmations > 1 ? activity.getString(R.string.Confirmations) : activity.getString(R.string.Confirmation)));

                dexReceiveContractAddressCopyTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Activity activity = getActivity();
                        if(activity == null)
                            return;
                        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText(activity.getString(R.string.receive_scan_address_copied), ContractAddress);
                        clipboard.setPrimaryClip(clip);

                        Snackbar.make(activity.findViewById(R.id.nav_host_fragment_content_main), activity.getString(R.string.receive_scan_address_copied), Snackbar.LENGTH_SHORT)
                                .setAction("", null).show();
                    }
                });
                dexReceiveContractAddressViewTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Activity activity = getActivity();
                        if(activity == null)
                            return;
                        String url;
                        if(Provider.equals("TRC-20")) {
                            switch (Global.getCurrentNetworkName()) {
                                case "MAINNET":
                                    url = "https://tronscan.org/#/address/";
                                    break;
                                case "DEVNET":
                                    url = "";
                                    break;
                                default:
                                    url = "https://shasta.tronscan.org/#/address/";
                                    break;
                            }
                        } else {
                            switch (Global.getCurrentNetworkName()) {
                                case "MAINNET":
                                    url = "https://etherscan.io/address/";
                                    break;
                                case "DEVNET":
                                    url = "";
                                    break;
                                default:
                                    url = "https://rinkeby.etherscan.io/address/";
                                    break;
                            }
                        }
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url + ContractAddress));
                        startActivity(browserIntent);

                        Snackbar.make(activity.findViewById(R.id.nav_host_fragment_content_main), activity.getString(R.string.receive_scan_address_copied), Snackbar.LENGTH_SHORT)
                                .setAction("", null).show();
                    }
                });

            } else {
                addressTextView.setText(UiHelpers.getShortAccountId(Global.getSelectedAccountId(), 7));
                FrameLayout dexReceiveFragmentLayout = getActivity().findViewById(R.id.dexReceiveFrameLayout);
                dexReceiveFragmentLayout.setVisibility(View.GONE);
            }

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
    }
}
