package com.lyrawallet.Ui.FragmentSend;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.lyrawallet.Api.ApiRpc;
import com.lyrawallet.Api.Network.NetworkRpc;
import com.lyrawallet.Crypto.CryptoBase58Encoding;
import com.lyrawallet.Crypto.CryptoSignatures;
import com.lyrawallet.Global;
import com.lyrawallet.GlobalLyra;
import com.lyrawallet.R;
import com.lyrawallet.Ui.FragmentManagerUser;
import com.lyrawallet.Ui.UiDialog;
import com.lyrawallet.Ui.UiHelpers;
import com.lyrawallet.Ui.UtilGetData;
import com.lyrawallet.Util.ExternTokenAddressValidator;

import org.json.JSONException;
import org.json.JSONObject;

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

    String Ticker = null;
    String Provider = null;
    String ContractAddress = null;
    double AmountToSend = 0f;
    double WithdrawFee = 0f;
    boolean sendToExternalAddress = false;

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

    public FragmentSend(String ticker, String provider, String contractAddress, double amountToSend, double withdrawFee) {
        Ticker = ticker;
        Provider = provider;
        ContractAddress = contractAddress;
        AmountToSend = amountToSend;
        WithdrawFee = withdrawFee;
        sendToExternalAddress = true;
    }

    public static FragmentSend newInstance(String param1, String param2) {
        FragmentSend fragment = new FragmentSend();
        return fragment;
    }

    private boolean checkSend() {
        Activity activity = getActivity();
        if (activity == null)
            return false;
        Button nextButton = (Button) activity.findViewById(R.id.sendTokenNextButton);
        if (nextButton == null)
            return false;
        nextButton.setEnabled(false);
        Spinner tokenSpinner = (Spinner) activity.findViewById(R.id.sendTokenSelectSpinner);
        EditText recipientAddressEditText = (EditText) activity.findViewById(R.id.sendTokenRecipientAddressValue);
        // Check destination account validity.
        if (!sendToExternalAddress) {
            if (!CryptoSignatures.validateAccountId(recipientAddressEditText.getText().toString()))
                return false;
        } else {
            if (Ticker.equals("TRX") || Provider.equals("TRC-20")) {
                if(!ExternTokenAddressValidator.tron(recipientAddressEditText.getText().toString()))
                    return false;
            } else if (Ticker.equals("ETH") || Provider.equals("ERC-20")) {
                if(!ExternTokenAddressValidator.ethereum(recipientAddressEditText.getText().toString()))
                    return false;
            }
        }
        EditText tokenAmountEditText = (EditText) activity.findViewById(R.id.sendTokenAmountValue);
        if (!sendToExternalAddress && tokenSpinner == null)
            return false;
        SpinnerAdapter adapter = tokenSpinner.getAdapter();
        if (!sendToExternalAddress && adapter == null)
            return false;
        // Check enough tokens.
        if(!sendToExternalAddress) {
            try {
                if (!UtilGetData.checkEnoughTokens(adapter.getItem(tokenSpinner.getSelectedItemPosition()).toString(),
                        Double.parseDouble(tokenAmountEditText.getText().toString()),
                        GlobalLyra.LYRA_TX_FEE)) {
                    return false;
                }
            } catch (NumberFormatException ignored) {
                return false;
            }
        } else {
            try {
                if (Double.parseDouble(tokenAmountEditText.getText().toString()) + WithdrawFee > AmountToSend)
                    return false;
            } catch (NumberFormatException ignored) {
                return false;
            }
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
        Spinner tokenSpinner = (Spinner) activity.findViewById(R.id.sendTokenSelectSpinner);
        if(sendToExternalAddress) {
            tokenSpinner.setVisibility(View.GONE);
            TextView dexSendTickerNameTextView = view.findViewById(R.id.dexSendTickerNameTextView);
            TextView dexSendTokenTextView = view.findViewById(R.id.dexSendTokenTextView);
            TextView dexSendTickerTextView = view.findViewById(R.id.dexSendTickerTextView);
            TextView dexSendProviderTextView = view.findViewById(R.id.dexSendProviderTextView);
            TextView dexSendContractTextView = view.findViewById(R.id.dexSendContractTextView);
            TextView dexSendWithdrawFeeTextView = view.findViewById(R.id.dexSendWithdrawFeeTextView);
            TextView dexSendContractAddressCopyTextView = view.findViewById(R.id.dexSendContractAddressCopyTextView);
            TextView dexSendContractAddressViewTextView = view.findViewById(R.id.dexSendContractAddressViewTextView);
            dexSendTokenTextView.setText(GlobalLyra.tickerToTokenName(Ticker));
            dexSendTickerNameTextView.setText(Ticker);
            dexSendTickerTextView.setText(Ticker);
            dexSendProviderTextView.setText(Provider);
            if(ContractAddress == null || ContractAddress.equals("null")) {
                dexSendContractAddressCopyTextView.setVisibility(View.GONE);
                dexSendContractAddressViewTextView.setVisibility(View.GONE);
                dexSendContractTextView.setText(" ");
            } else {
                dexSendContractTextView.setText(UiHelpers.getShortAccountId(ContractAddress, 12));
            }
            dexSendWithdrawFeeTextView.setText(String.format(Locale.US, "%f",  WithdrawFee));

            dexSendContractAddressCopyTextView.setOnClickListener(new View.OnClickListener() {
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
            dexSendContractAddressViewTextView.setOnClickListener(new View.OnClickListener() {
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
        }
        else {
            FrameLayout dexSendFrameLayout = view.findViewById(R.id.dexSendFrameLayout);
            dexSendFrameLayout.setVisibility(View.GONE);
        }
        EditText recipientAddressEditText = (EditText) view.findViewById(R.id.sendTokenRecipientAddressValue);
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

        EditText tokenAmountEditText = (EditText) view.findViewById(R.id.sendTokenAmountValue);
        tokenAmountEditText.setText(String.format(Locale.US, "%f", AmountToSend));
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
        List<String> tickerList = new ArrayList<>();
        Balances = UtilGetData.getAvailableTokenList();
        for (int i = 0; i < Balances.size(); i++) {
            tickerList.add(GlobalLyra.domainToSymbol(Balances.get(i).first));
        }
        SendTokensSpinnerAdapter adapter = new SendTokensSpinnerAdapter(this.getContext(), R.layout.entry_send_token_select_spinner,
                tickerList.toArray(new String[0]), UiHelpers.tickerToImage(tickerList).toArray(new Integer[0]));
        adapter.setDropDownViewResource(R.layout.entry_send_token_select_spinner_first);
        tokenSpinner.setAdapter(adapter);
        tokenSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(view != null) {
                    EditText tokenAmountEditText = (EditText) view.findViewById(R.id.sendTokenAmountValue);
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
        ImageButton qrButton = (ImageButton) view.findViewById(R.id.sendTokenSelectSpinnerEntryQrButton);
        qrButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Activity activity = getActivity();
                if(activity == null)
                    return;
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
        ImageButton bookAccountButton = (ImageButton) view.findViewById(R.id.sendTokenSelectSpinnerEntryBookButton);
        bookAccountButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
        ImageButton maxButton = (ImageButton) view.findViewById(R.id.sendTokenAmountMaxButton);
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
                                EditText tokenAmountEditText = (EditText) view.findViewById(R.id.sendTokenAmountValue);
                                tokenAmountEditText.setText(String.format(Locale.US, "%f", max));
                            }
                            return;
                        }
                    }
                }
            }
        });
        Button nextButton = (Button) view.findViewById(R.id.sendTokenNextButton);
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

                    ImageButton closeButton = (ImageButton) mView.findViewById(R.id.dialogSendClose);
                    closeButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    EditText recipientAddressEditText = (EditText) activity.findViewById(R.id.sendTokenRecipientAddressValue);
                    TextView sendValueTextView = (TextView) mView.findViewById(R.id.dialogSendValue);
                    TextView sendFromTextView = (TextView) mView.findViewById(R.id.dialogSendFromValue);
                    TextView sendToTextView = (TextView) mView.findViewById(R.id.dialogSendToValue);
                    TextView networkFeeTextView = (TextView) mView.findViewById(R.id.dialogSendNetworkFeeValue);
                    TextView totalSpendTextTextView = (TextView) mView.findViewById(R.id.dialogSendTotalSpendText);
                    TextView totalSpendTextView = (TextView) mView.findViewById(R.id.dialogSendTotalSpendValue);

                    if(sendValueTextView == null || sendFromTextView == null || sendToTextView == null || networkFeeTextView == null || totalSpendTextView == null) {
                        Snackbar.make(view, "Dialog not inflated.", Snackbar.LENGTH_LONG)
                                .setAction("", null).show();
                        return;
                    }
                    Spinner tokenSpinner = (Spinner) activity.findViewById(R.id.sendTokenSelectSpinner);
                    if(sendToExternalAddress)
                        tokenSpinner.setVisibility(View.GONE);
                    else {
                        FrameLayout dexSendFrameLayout = view.findViewById(R.id.dexSendFrameLayout);
                        dexSendFrameLayout.setVisibility(View.GONE);
                    }
                    SpinnerAdapter adapter = tokenSpinner.getAdapter();
                    EditText tokenAmountEditText = (EditText) view.findViewById(R.id.sendTokenAmountValue);
                    if (tokenAmountEditText == null) {
                        Snackbar.make(view, "Inalid token amount pointer.", Snackbar.LENGTH_LONG)
                                .setAction("", null).show();
                        return;
                    }
                    String tokenToSend = adapter.getItem(tokenSpinner.getSelectedItemPosition()).toString();
                    if(!sendToExternalAddress) {
                        try {
                            sendValueTextView.setText(String.format(Locale.US, "%f %s",
                                    Double.parseDouble(tokenAmountEditText.getText().toString()), tokenToSend));
                        } catch (NumberFormatException ex) {
                            Snackbar.make(view, "Invalid amount value.", Snackbar.LENGTH_LONG)
                                    .setAction("", null).show();
                            return;
                        }
                    } else {
                        String s = String.format(Locale.US, "%f", Double.parseDouble(tokenAmountEditText.getText().toString()));
                        sendValueTextView.setText(String.format(Locale.US, "%s %s", s, Ticker));
                    }
                    sendFromTextView.setText(String.format(Locale.US, "%s-%d (%s)", activity.getString(R.string.Wallet),
                            Global.getSelectedAccountNr() + 1, UiHelpers.getShortAccountId(Global.getSelectedAccountId(), 4)));
                    sendToTextView.setText(UiHelpers.getShortAccountId(recipientAddressEditText.getText().toString(), 7));
                    if(!sendToExternalAddress) {
                        networkFeeTextView.setText(String.format(Locale.US, "%s LYR", GlobalLyra.LYRA_TX_FEE));
                        String s;
                        if (tokenToSend.equals("LYR"))
                            s = String.format(Locale.US, "%f", Double.parseDouble(tokenAmountEditText.getText().toString()) + GlobalLyra.LYRA_TX_FEE);
                        else
                            s = String.format(Locale.US, "%f", Double.parseDouble(tokenAmountEditText.getText().toString()));
                        totalSpendTextView.setText(String.format(Locale.US, "%s %s", s, tokenToSend));
                    } else {
                        networkFeeTextView.setText(String.format(Locale.US, "%s %s", WithdrawFee, Ticker));
                        totalSpendTextTextView.setVisibility(View.GONE);
                        totalSpendTextView.setVisibility(View.GONE);
                    }

                    Button sendButton = (Button) mView.findViewById(R.id.dialog_send_token_button);
                    sendButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Activity activity = getActivity();
                            if (activity == null)
                                return;
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    try {
                                        //TODO DEX Send to external addresses
                                        if(!sendToExternalAddress) {
                                            UiDialog.showDialogStatus(R.string.send_sending);
                                            NetworkRpc rpc = (NetworkRpc) new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL, Global.getWalletPassword())
                                                    .execute("", "Send", Global.getSelectedAccountId(),
                                                            tokenAmountEditText.getText().toString(),
                                                            recipientAddressEditText.getText().toString(),
                                                            GlobalLyra.symbolToDomain(tokenToSend)
                                                    );
                                            rpc.setListener(new NetworkRpc.RpcTaskListener() {
                                                @Override
                                                public void onRpcTaskFinished(String[] output) {
                                                    activity.runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            System.out.println("RPC SEND: " + output[0] + output[1] + output[2]);
                                                            EditText recipientAddressEditText = (EditText) activity.findViewById(R.id.sendTokenRecipientAddressValue);
                                                            EditText tokenAmountEditText = (EditText) activity.findViewById(R.id.sendTokenAmountValue);
                                                            if (recipientAddressEditText != null && tokenAmountEditText != null) {
                                                                try {
                                                                    JSONObject objRsp = new JSONObject(output[2]);
                                                                    if (!objRsp.isNull("txHash")) { // If txHash is present, the transaction is successfully send.
                                                                        Spinner tokenSpinner = (Spinner) activity.findViewById(R.id.sendTokenSelectSpinner);
                                                                        SpinnerAdapter adapter = tokenSpinner.getAdapter();
                                                                        String tokenToSend = adapter.getItem(tokenSpinner.getSelectedItemPosition()).toString();
                                                                        ApiRpc.getBalanceAfterAction();
                                                                        try {
                                                                            UiDialog.showDialogStatus(R.string.send_successful, String.format(Locale.US, "%s: %f %s\n%s: %s-%d (%s)\n%s: %s",
                                                                                    activity.getString(R.string.Send1), Double.parseDouble(tokenAmountEditText.getText().toString()), tokenToSend,
                                                                                    activity.getString(R.string.From), activity.getString(R.string.Wallet), Global.getSelectedAccountNr() + 1, UiHelpers.getShortAccountId(Global.getSelectedAccountId(), 4),
                                                                                    activity.getString(R.string.To), UiHelpers.getShortAccountId(recipientAddressEditText.getText().toString(), 7)),
                                                                                    ApiRpc.class.getDeclaredMethod("runActionHistory")
                                                                            );
                                                                        } catch (NoSuchMethodException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                        recipientAddressEditText.setText("");
                                                                        tokenAmountEditText.setText("");
                                                                        new FragmentManagerUser().goToAccount();
                                                                    }
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                    Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        }
                                                    });
                                                }
                                            });

                                            alertDialog.dismiss();
                                        }
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
        checkSend();
    }
}
