package com.lyrawallet.Ui.FragmentDex;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lyrawallet.R;

public class DexViewHolder  extends RecyclerView.ViewHolder {
    ImageView TickerImage;
    TextView TickerText;
    TextView SpotQtyText;
    TextView SpotTickerText;
    TextView DexQtyText;
    TextView DexTickerText;

    ImageButton SettingsImageButton;
    ImageButton SpotToDexImageButton;
    ImageButton DexToSpotImageButton;
    Button DepositButton;
    Button WithdrawButton;


    View view;

    DexViewHolder(View itemView) {
        super(itemView);
        TickerImage
                = (ImageView) itemView
                .findViewById(R.id.entryDex_Ticker_ImageView);
        TickerText
                = (TextView) itemView
                .findViewById(R.id.entryDex_Ticker_TextView);
        SpotQtyText
                = (TextView) itemView
                .findViewById(R.id.entryDex_SpotQty_TextView);
        SpotTickerText
                = (TextView) itemView
                .findViewById(R.id.entryDex_SpotTicker_TextView);
        DexQtyText
                = (TextView) itemView
                .findViewById(R.id.entryDex_DexQty_TextView);
        DexTickerText
                = (TextView) itemView
                .findViewById(R.id.entryDex_DexTicker_TextView);

        SettingsImageButton
                = (ImageButton) itemView
                .findViewById(R.id.entryDexSettingsImageButton);
        SpotToDexImageButton
                = (ImageButton) itemView
                .findViewById(R.id.entryDex_SpotToDex_ImageButton);
        DexToSpotImageButton
                = (ImageButton) itemView
                .findViewById(R.id.entryDex_DexToSpot_ImageButton);
        DepositButton
                = (Button) itemView
                .findViewById(R.id.entryDex_Deposit_Button);
        WithdrawButton
                = (Button) itemView
                .findViewById(R.id.entryDex_Withdraw_Button);
        view = itemView;
    }
}
