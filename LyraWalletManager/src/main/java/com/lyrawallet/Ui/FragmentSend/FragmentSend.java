package com.lyrawallet.Ui.FragmentSend;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.lyrawallet.Api.ApiRpc;
import com.lyrawallet.Crypto.CryptoSignatures;
import com.lyrawallet.Global;
import com.lyrawallet.GlobalLyra;
import com.lyrawallet.R;
import com.lyrawallet.Ui.TokensSpinnerAdapter;
import com.lyrawallet.Ui.UiHelpers;
import com.lyrawallet.Ui.UtilGetData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSend#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSend extends Fragment {
    private List<Pair<String, Double>> Balances = new ArrayList<>();

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

    private boolean checkSend() {
        Activity activity = getActivity();
        if(activity == null)
            return false;
        Button nextButton = (Button) activity.findViewById(R.id.send_token_next_button);
        if(nextButton == null)
            return false;
        nextButton.setEnabled(false);
        Spinner tokenSpinner = (Spinner) activity.findViewById(R.id.sendTokenSelectSpinner);
        EditText recipientAddressEditText = (EditText) activity.findViewById(R.id.send_token_recipient_address_value);
        if(!CryptoSignatures.validateAccountId(recipientAddressEditText.getText().toString()))
            return false;
        EditText tokenAmountEditText = (EditText) activity.findViewById(R.id.send_token_amount_value);
        if (tokenSpinner == null)
            return false;
        SpinnerAdapter adapter = tokenSpinner.getAdapter();
        if (adapter == null)
            return false;
        try {
            if (!UtilGetData.checkEnoughTokens(adapter.getItem(tokenSpinner.getSelectedItemPosition()).toString(),
                    Double.parseDouble(tokenAmountEditText.getText().toString()),
                    GlobalLyra.LYRA_TX_FEE)) {
                return false;
            }
        } catch (NumberFormatException ex) {
            return false;
        }

        nextButton.setEnabled(true);
        return true;
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
        Activity activity = getActivity();
        if(activity == null)
            return;
        CurvedBottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.GONE);
        //new ApiRpc().act(new ApiRpc.Action().actionPool("LYR", "tether/LTT"));
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
                    checkSend();
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
                    if(s.length() > 0) {
                        try {
                            Double.parseDouble(s.toString());
                        } catch (NumberFormatException e) {
                            tokenAmountEditText.setText(s.subSequence(0, before - 1));
                        }
                    }
                    checkSend();
                }
            });
        }
        Spinner tokenSpinner = (Spinner) view.findViewById(R.id.sendTokenSelectSpinner);
        List<String> tickerList = new ArrayList<>();
        Balances = UtilGetData.getAvailableTokenList();
        for (int i = 0; i < Balances.size(); i++) {
            tickerList.add(Balances.get(i).first);
        }
        TokensSpinnerAdapter adapter = new TokensSpinnerAdapter(this.getContext(), R.layout.send_token_select_spinner_entry,
                tickerList.toArray(new String[0]), UiHelpers.tickerToImage(tickerList).toArray(new Integer[0]));
        adapter.setDropDownViewResource(R.layout.send_token_select_spinner_entry_first);
        tokenSpinner.setAdapter(adapter);
        tokenSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(view != null) {
                    EditText tokenAmountEditText = (EditText) view.findViewById(R.id.send_token_amount_value);
                    if(tokenAmountEditText != null) {
                        double max = Balances.get(i).second;
                        if(max > 0) {
                            if (Balances.get(i).first.equals("LYR")) {
                                max -= GlobalLyra.LYRA_TX_FEE;
                            }
                        }
                        tokenAmountEditText.setHint(String.format("%s: %s", getString(R.string.send_token_available), max));
                        //tokenAmountEditText.setText("");
                        checkSend();
                    }
                }
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        ImageButton qrButton = (ImageButton) view.findViewById(R.id.send_token_select_spinner_entry_button_qr);
        qrButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Activity activity = getActivity();
                if(activity == null)
                    return;
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
                Spinner tokenSpinner = (Spinner) view.findViewById(R.id.sendTokenSelectSpinner);
                if (tokenSpinner != null) {
                    SpinnerAdapter adapter = tokenSpinner.getAdapter();
                    for (int i = 0; i < Balances.size(); i++) {
                        if (Balances.get(i).first.equals(adapter.getItem(tokenSpinner.getSelectedItemPosition()).toString())) {
                            double max = Balances.get(i).second;
                            if(max >= GlobalLyra.LYRA_TX_FEE) {
                                if (Balances.get(i).first.equals("LYR"))
                                    max -= GlobalLyra.LYRA_TX_FEE;
                                EditText tokenAmountEditText = (EditText) view.findViewById(R.id.send_token_amount_value);
                                tokenAmountEditText.setText(String.format(Locale.US, "%f", max));
                            }
                            return;
                        }
                    }
                }
            }
        });
        Button nextButton = (Button) view.findViewById(R.id.send_token_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (checkSend()) {
                    Activity activity = getActivity();
                    if (activity == null) {
                        Snackbar.make(view, "Activity = null.", Snackbar.LENGTH_LONG)
                                .setAction("", null).show();
                        return;
                    }
                    UiHelpers.closeKeyboard(view);
                    final AlertDialog.Builder alert = new AlertDialog.Builder(activity,R.style.GeneralDialogTheme);
                    View mView = getLayoutInflater().inflate(R.layout.dialog_send,null);
                    alert.setView(mView);
                    final AlertDialog alertDialog = alert.create();
                    alertDialog.setCanceledOnTouchOutside(false);

                    ImageButton closeButton = (ImageButton) mView.findViewById(R.id.dialog_send_close);
                    closeButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    EditText recipientAddressEditText = (EditText) activity.findViewById(R.id.send_token_recipient_address_value);
                    TextView sendValueTextView = (TextView) mView.findViewById(R.id.dialog_send_value);
                    TextView sendFromTextView = (TextView) mView.findViewById(R.id.dialog_send_from_value);
                    TextView sendToTextView = (TextView) mView.findViewById(R.id.dialog_send_to_value);
                    TextView networkFeeTextView = (TextView) mView.findViewById(R.id.dialog_send_network_fee_value);
                    TextView totalSpendTextView = (TextView) mView.findViewById(R.id.dialog_send_total_spend_value);

                    if(sendValueTextView == null || sendFromTextView == null || sendToTextView == null || networkFeeTextView == null || totalSpendTextView == null) {
                        Snackbar.make(view, "Dialog not inflated.", Snackbar.LENGTH_LONG)
                                .setAction("", null).show();
                        return;
                    }
                    Spinner tokenSpinner = (Spinner) activity.findViewById(R.id.sendTokenSelectSpinner);
                    SpinnerAdapter adapter = tokenSpinner.getAdapter();
                    EditText tokenAmountEditText = (EditText) view.findViewById(R.id.send_token_amount_value);
                    if (tokenAmountEditText == null) {
                        Snackbar.make(view, "Inalid token amount pointer.", Snackbar.LENGTH_LONG)
                                .setAction("", null).show();
                        return;
                    }
                    String tokenToSend = adapter.getItem(tokenSpinner.getSelectedItemPosition()).toString();
                    try {
                        sendValueTextView.setText(String.format(Locale.US, "%f %s",
                                Double.parseDouble(tokenAmountEditText.getText().toString()), tokenToSend));
                    } catch (NumberFormatException ex) {
                        Snackbar.make(view, "Invalid amount value.", Snackbar.LENGTH_LONG)
                                .setAction("", null).show();
                        return;
                    }
                    sendFromTextView.setText(String.format(Locale.US, "%s-%d (%s)", activity.getString(R.string.Wallet),
                            Global.getSelectedAccountNr() + 1, UiHelpers.getShortAccountId(Global.getSelectedAccountId(), 4)));
                    sendToTextView.setText(UiHelpers.getShortAccountId(recipientAddressEditText.getText().toString(), 7));
                    networkFeeTextView.setText(String.format(Locale.US, "%s LYR", GlobalLyra.LYRA_TX_FEE));
                    String s;
                    if(tokenToSend.equals("LYR"))
                        s = String.format(Locale.US, "%f", Double.parseDouble(tokenAmountEditText.getText().toString()) + GlobalLyra.LYRA_TX_FEE);
                    else
                        s = String.format(Locale.US, "%f", Double.parseDouble(tokenAmountEditText.getText().toString()));
                    totalSpendTextView.setText(String.format(Locale.US, "%s %s", s, tokenToSend));

                    Button sendButton = (Button) mView.findViewById(R.id.dialog_send_token_button);
                    sendButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Activity activity = getActivity();
                            if (activity == null)
                                return;
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    try {
                                        new ApiRpc().act(new ApiRpc.Action().actionSend(
                                                Global.getSelectedAccountId(),
                                                Double.parseDouble(tokenAmountEditText.getText().toString()),
                                                GlobalLyra.symbolToDomain(tokenToSend),
                                                recipientAddressEditText.getText().toString()));
                                        alertDialog.dismiss();
                                    } catch (NumberFormatException ex) {
                                        Snackbar.make(view, "An error occurred when trying to send.", Snackbar.LENGTH_LONG)
                                                .setAction("", null).show();
                                    }
                                }
                            });
                        }
                    });
                    alertDialog.show();
                }
            }
        });
    }
}
