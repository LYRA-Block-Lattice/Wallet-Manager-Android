package com.lyrawallet.Ui.FragmentAccount;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lyrawallet.R;

public class AccountHistoryViewHolder extends RecyclerView.ViewHolder {
    ImageView TickerImage;
    TextView TickerName;
    TextView Quantity;
    TextView QuantityUsd;
    TextView ValueUsdPerUnit;
    View view;

    AccountHistoryViewHolder(View itemView) {
        super(itemView);
        TickerImage
                = (ImageView) itemView
                .findViewById(R.id.tickerImageView);
        TickerName
                = (TextView) itemView
                .findViewById(R.id.wallet_tr_entry_card_ticker);
        Quantity
                = (TextView) itemView
                .findViewById(R.id.wallet_tr_entry_card_value_usd);
        QuantityUsd
                = (TextView) itemView
                .findViewById(R.id.wallet_tr_entry_card_total_value);
        ValueUsdPerUnit
                = (TextView) itemView
                .findViewById(R.id.wallet_tr_entry_card_total_value_usd);
        view = itemView;
    }
}
