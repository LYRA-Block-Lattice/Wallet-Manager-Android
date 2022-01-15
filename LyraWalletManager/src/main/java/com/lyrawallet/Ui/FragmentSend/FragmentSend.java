package com.lyrawallet.Ui.FragmentSend;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.lyrawallet.Api.ApiRpc;
import com.lyrawallet.MainActivity;
import com.lyrawallet.R;

import java.util.ArrayList;
import java.util.List;

import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSend#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSend extends Fragment {
    public static class SendTokensEntry {
        int TickerImage;
        String TickerName;

        public SendTokensEntry(int tickerImage, String tickerName) {
            this.TickerImage = tickerImage;
            this.TickerName = tickerName;
        }
    }


    public FragmentSend() {
        // Required empty public constructor
    }

    public static FragmentSend newInstance(String param1, String param2) {
        FragmentSend fragment = new FragmentSend();
        return fragment;
    }

    public static List<Integer> getData(List<String> tokenNames) {
        List<Integer > List = new ArrayList<>();
        if(tokenNames == null) {
            /*List.add(0, new FragmentSend.SendTokensEntry(R.mipmap.ic_unknown_foreground,
                    "Empty list"));*/
            return null;
        }
        for (int i = 0; i < tokenNames.size(); i++) {
            int icon = R.mipmap.ic_unknown_foreground;
            switch (tokenNames.get(i)) {
                case "LYR":
                    icon = R.mipmap.ic_lyra_foreground;
                    break;
                case "$USDC":
                    icon = R.mipmap.ic_usdc_foreground;
                    break;
                case "$USDT":
                    icon = R.mipmap.ic_usdt_foreground;
                    break;
                case "$ETH":
                    icon = R.mipmap.ic_eth_foreground;
                    break;
            }
            List.add(icon);
        }
        return List;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        CurvedBottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.GONE);

        new ApiRpc().act(new ApiRpc.Action().actionPool("LYR", "tether/LTT"));

        Spinner tokenSpinner = (Spinner) view.findViewById(R.id.sendTokenSelectSpinner);
        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), R.layout.send_token_select_spinner_entry_first,
                R.id.send_token_select_spinner_entry_text, getActivity().fileList());*/
        List<String> tickerList = new ArrayList<>();
        tickerList.add("LYR");
        tickerList.add("$ETH");
        tickerList.add("$USDC");
        tickerList.add("$USDT");
        FragmentActivity activity = getActivity();
        /*SendTokensGalleryAdapter adapter = new SendTokensGalleryAdapter(view.getContext(), R.layout.send_token_select_spinner_entry,
                getData(tickerList));*/
        SendTokensSpinnerAdapter adapter = new SendTokensSpinnerAdapter(this.getContext(), R.layout.send_token_select_spinner_entry, tickerList.toArray(new String[0]), getData(tickerList).toArray(new Integer[0]));
        adapter.setDropDownViewResource(R.layout.send_token_select_spinner_entry_first);
        tokenSpinner.setAdapter(adapter);
        tokenSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(view != null) {

                }
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        EditText recipientAddressEditText = (EditText) view.findViewById(R.id.send_token_recipient_address_value);
        if(recipientAddressEditText != null) {
            recipientAddressEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                }
                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                }
            });
        }

        EditText tokenAmountEditText = (EditText) view.findViewById(R.id.send_token_amount_value);
        if(tokenAmountEditText != null) {
            tokenAmountEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                }
                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                }
            });
        }
        ImageButton qrButton = (ImageButton) view.findViewById(R.id.send_token_select_spinner_entry_button_qr);
        qrButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Activity activity = getActivity();
                if(activity == null) {
                    return;
                }
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
        ImageButton bookAccountButton = (ImageButton) view.findViewById(R.id.send_token_select_spinner_entry_button_book);
        bookAccountButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
        ImageButton maxButton = (ImageButton) view.findViewById(R.id.send_token_amount_max_button);
        maxButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
        Button nextButton = (Button) view.findViewById(R.id.send_token_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        SpinnerAdapter ad = tokenSpinner.getAdapter();
        Object item = ad.getItem(0);
        tokenSpinner.setSelection(2);
        System.out.println(ad);


    }
}
