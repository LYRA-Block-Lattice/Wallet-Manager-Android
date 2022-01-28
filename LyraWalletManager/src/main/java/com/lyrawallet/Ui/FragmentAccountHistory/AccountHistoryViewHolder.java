package com.lyrawallet.Ui.FragmentAccountHistory;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lyrawallet.R;

public class AccountHistoryViewHolder extends RecyclerView.ViewHolder {
    TextView Height;
    ImageView TickerImage;
    TextView TickerName;
    TextView Quantity;
    TextView QuantityUsd;
    TextView ValueUsdPerUnit;
    View view;

    AccountHistoryViewHolder(View itemView) {
        super(itemView);
        Height
                = (TextView) itemView
                .findViewById(R.id.walletTrEntryCardHeight);
        TickerImage
                = (ImageView) itemView
                .findViewById(R.id.tickerImageView);
        TickerName
                = (TextView) itemView
                .findViewById(R.id.walletTrEntryCardTicker);
        Quantity
                = (TextView) itemView
                .findViewById(R.id.walletTrEntryCardValueUsd);
        QuantityUsd
                = (TextView) itemView
                .findViewById(R.id.walletTrEntryCardTotalValue);
        ValueUsdPerUnit
                = (TextView) itemView
                .findViewById(R.id.walletTrEntryCardTotalValueUsd);
        view = itemView;
    }
}
