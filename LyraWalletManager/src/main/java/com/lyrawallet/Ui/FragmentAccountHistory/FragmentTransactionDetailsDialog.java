package com.lyrawallet.Ui.FragmentAccountHistory;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;
import com.lyrawallet.Api.ApiRpcActions.ApiRpcActionsHistory;
import com.lyrawallet.Global;
import com.lyrawallet.GlobalLyra;
import com.lyrawallet.R;
import com.lyrawallet.Ui.UiDialog;
import com.lyrawallet.Ui.UiHelpers;
import com.lyrawallet.Util.Concatenate;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class FragmentTransactionDetailsDialog extends DialogFragment {

    int height = 0;
    public FragmentTransactionDetailsDialog(String[] params) {
        this.height = Integer.parseInt(params[0]);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.GeneralDialogTheme);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_history_detail, container, false);

        Activity activity = getActivity();
        if (activity == null) {
            Snackbar.make(view, "Activity = null.", Snackbar.LENGTH_LONG)
                    .setAction("", null).show();
            return view;
        }

        CurvedBottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.GONE);

        Pair<Integer, List<ApiRpcActionsHistory.HistoryEntry>> history = Global.getWalletHistory(Concatenate.getHistoryFileName());
        if(history == null || history.second == null) {
            UiDialog.showDialogStatus(R.string.Invalid_request);
            return view;
        }
        ApiRpcActionsHistory.HistoryEntry transaction = null;
        try {
            transaction = history.second.get(this.height - 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            Snackbar.make(view, e.toString(), Snackbar.LENGTH_LONG)
                    .setAction("", null).show();
            return view;
        }
        ImageView dialog_history_ticker_imageView = (ImageView) view.findViewById(R.id.dialog_history_ticker_imageView);
        TextView historyDetailHeightTextView = (TextView) view.findViewById(R.id.historyDetailHeightTextView);
        TextView historyDetailDirectionTextView = (TextView) view.findViewById(R.id.historyDetailDirectionTextView);
        TextView historyDetailTimestampTextView = (TextView) view.findViewById(R.id.historyDetailTimestampTextView);
        TextView historyDetailSendIdTextView = (TextView) view.findViewById(R.id.historyDetailSendIdTextView);
        TextView historyDetailSendHashTextView = (TextView) view.findViewById(R.id.historyDetailSendHashTextView);
        TextView historyDetailReceiveIdTextView = (TextView) view.findViewById(R.id.historyDetailReceiveIdTextView);
        TextView historyDetailReceiveHashTextView = (TextView) view.findViewById(R.id.historyDetailReceiveHashTextView);
        TextView historyDetailAmountTransferredTextView = (TextView) view.findViewById(R.id.historyDetailAmountTransferredTextView);
        TextView historyDetailAmountInAccountTextView = (TextView) view.findViewById(R.id.historyDetailAmountInAccountTextView);


        historyDetailHeightTextView.setText(String.valueOf(transaction.getHeight()));
        if(transaction.getIsReceive()) {
            historyDetailDirectionTextView.setText(R.string.Receive);
            historyDetailDirectionTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_south_west_24, 0);
        } else {
            historyDetailDirectionTextView.setText(R.string.Send3);
            historyDetailDirectionTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_north_east_24, 0);
        }
        Date date = new Date(transaction.getTimeStamp());
        Format format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
        historyDetailTimestampTextView.setText(format.format(date));
        historyDetailSendIdTextView.setText(UiHelpers.getShortAccountId(transaction.getSendAccountId(), 8));
        historyDetailSendHashTextView.setText(UiHelpers.getShortAccountId(transaction.getSendHash(), 8));
        historyDetailReceiveIdTextView.setText(UiHelpers.getShortAccountId(transaction.getRecvAccountId(), 8));
        StringBuilder amountTransferred = new StringBuilder();
        for (int i = 0; i < transaction.getChanges().size(); i++) {
            amountTransferred.append(String.format(Locale.US, "%f %s",
                    transaction.getChanges().get(i).second < 0 ? 0 - transaction.getChanges().get(i).second : transaction.getChanges().get(i).second,
                    GlobalLyra.domainToSymbol(transaction.getChanges().get(i).first)));
            if(i < transaction.getChanges().size() - 1)
                amountTransferred.append("\n");
        }
        historyDetailAmountTransferredTextView.setText(amountTransferred.toString());
        StringBuilder amountInAccount = new StringBuilder();
        for (int i = 0; i < transaction.getBalances().size(); i++) {
            amountInAccount.append(String.format(Locale.US, "%f %s", transaction.getBalances().get(i).second,
                    GlobalLyra.domainToSymbol(transaction.getBalances().get(i).first)));
            if(i < transaction.getBalances().size() - 1)
                amountInAccount.append("\n");
        }
        historyDetailAmountInAccountTextView.setText(amountInAccount.toString());

        dialog_history_ticker_imageView.setImageResource(UiHelpers.tickerToImage(GlobalLyra.domainToSymbol(
                transaction.getChanges().size() == 1 ? transaction.getChanges().get(0).first : transaction.getChanges().get(1).first)));

        historyDetailTimestampTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(activity.getString(R.string.account_transaction_time_stamp_copied), historyDetailTimestampTextView.getText());
                clipboard.setPrimaryClip(clip);

                Snackbar.make(activity.findViewById(R.id.nav_host_fragment_content_main), activity.getString(R.string.account_transaction_time_stamp_copied), Snackbar.LENGTH_SHORT)
                        .setAction("", null).show();            }
        });
        ApiRpcActionsHistory.HistoryEntry finalTransaction = transaction;
        historyDetailSendIdTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url;
                switch (Global.getCurrentNetworkName()) {
                    case "MAINNET": url = "https://nebula.lyra.live/showblock/"; break;
                    case "DEVNET": url = ""; break;
                    default: url = "https://nebulatestnet.lyra.live/showblock/"; break;
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url + finalTransaction.getSendAccountId()));
                startActivity(browserIntent);
            }
        });
        historyDetailSendHashTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url;
                switch (Global.getCurrentNetworkName()) {
                    case "MAINNET": url = "https://nebula.lyra.live/showblock/"; break;
                    case "DEVNET": url = ""; break;
                    default: url = "https://nebulatestnet.lyra.live/showblock/"; break;
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url + finalTransaction.getSendHash()));
                startActivity(browserIntent);
            }
        });
        historyDetailReceiveIdTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url;
                switch (Global.getCurrentNetworkName()) {
                    case "MAINNET": url = "https://nebula.lyra.live/showblock/"; break;
                    case "DEVNET": url = ""; break;
                    default: url = "https://nebulatestnet.lyra.live/showblock/"; break;
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url + finalTransaction.getRecvAccountId()));
                startActivity(browserIntent);
            }
        });
        try {
            historyDetailReceiveHashTextView.setText(UiHelpers.getShortAccountId(transaction.getRecvHash(), 8));
            historyDetailReceiveHashTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url;
                    switch (Global.getCurrentNetworkName()) {
                        case "MAINNET": url = "https://nebula.lyra.live/showblock/"; break;
                        case "DEVNET": url = ""; break;
                        default: url = "https://nebulatestnet.lyra.live/showblock/"; break;
                    }
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url + finalTransaction.getRecvHash()));
                    startActivity(browserIntent);
                }
            });
        } catch (NullPointerException ignored) {
            historyDetailReceiveHashTextView.setText(getString(R.string.Not_received_yet));
        }
        historyDetailAmountTransferredTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(activity.getString(R.string.account_transaction_amount_transferred_copied), historyDetailAmountTransferredTextView.getText());
                clipboard.setPrimaryClip(clip);

                Snackbar.make(activity.findViewById(R.id.nav_host_fragment_content_main), activity.getString(R.string.account_transaction_amount_transferred_copied), Snackbar.LENGTH_SHORT)
                        .setAction("", null).show();            }
        });
        historyDetailAmountInAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(activity.getString(R.string.account_transaction_amounts_in_account_copied), historyDetailAmountInAccountTextView.getText());
                clipboard.setPrimaryClip(clip);

                Snackbar.make(activity.findViewById(R.id.nav_host_fragment_content_main), activity.getString(R.string.account_transaction_amounts_in_account_copied), Snackbar.LENGTH_SHORT)
                        .setAction("", null).show();            }
        });


        return view;
    }
}
