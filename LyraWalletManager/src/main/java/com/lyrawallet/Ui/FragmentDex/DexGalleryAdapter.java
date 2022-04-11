package com.lyrawallet.Ui.FragmentDex;

import static com.lyrawallet.GlobalLyra.TokenMainnetIconList;
import static com.lyrawallet.GlobalLyra.TokenTestnetIconList;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lyrawallet.Global;
import com.lyrawallet.GlobalLyra;
import com.lyrawallet.R;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DexGalleryAdapter extends RecyclerView.Adapter<DexViewHolder> {
    List<FragmentDex.DexEntry> list = Collections.emptyList();
    android.content.Context Context;
    FragmentDex.ClickListener SettingsListener;
    FragmentDex.ClickListener DexToSpotListener;
    FragmentDex.ClickListener SpotToDexListener;
    FragmentDex.ClickListener DepositListener;
    FragmentDex.ClickListener WithdrawListener;

    public DexGalleryAdapter(List<FragmentDex.DexEntry> list,
                             Context context,
                             FragmentDex.ClickListener settingsListener,
                             FragmentDex.ClickListener dexToSpotListener,
                             FragmentDex.ClickListener spotToDexListener,
                             FragmentDex.ClickListener depositListener,
                             FragmentDex.ClickListener withdrawListener)
    {
        this.SettingsListener = settingsListener;
        this.DexToSpotListener = dexToSpotListener;
        this.SpotToDexListener = spotToDexListener;
        this.DepositListener = depositListener;
        this.WithdrawListener = withdrawListener;
        this.list = list;
        this.Context = context;
    }

    public List<FragmentDex.DexEntry> getDataSet(){
        return list;
    }

    public void setDataSet(List<FragmentDex.DexEntry> newDataSet){
        this.list = newDataSet;
        notifyDataSetChanged();
    }

    public void clear(){
        int size = list.size();
        this.list.clear();
        notifyItemRangeRemoved(0, size);
    }


    @NonNull
    @Override
    public DexViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the layout
        View photoView = inflater.inflate(R.layout.entry_dex_balance, parent, false);
        return new DexViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull DexViewHolder viewHolder, int position) {
        final int index = viewHolder.getAdapterPosition();
        Pair<String, Integer>[] TokenIconList = TokenTestnetIconList;
        if(Global.getCurrentNetworkName().equals("MAINNET"))
            TokenIconList = TokenMainnetIconList;

        int icon = GlobalLyra.TickerIconList[1].second;
        String tokenName = TokenIconList[1].first;
        int tickerCnt = 0;
        for (Pair<String, Integer> k : GlobalLyra.TickerIconList) {
            if (k.first.equals(GlobalLyra.domainToSymbol(list.get(position).Ticker))) {
                icon = k.second;
                tokenName = TokenIconList[tickerCnt].first;
                break;
            }
            tickerCnt++;
        }
        viewHolder.TickerImage.setImageResource(icon);
        viewHolder.TickerText.setText(tokenName);
        viewHolder.SpotQtyText.setText(String.format(Locale.US, "%.8f", list.get(position).SpotQty));
        viewHolder.SpotTickerText.setText(list.get(position).Ticker.replace("$", ""));
        viewHolder.DexQtyText.setText(String.format(Locale.US, "%.8f", list.get(position).DexQty));
        viewHolder.DexTickerText.setText(list.get(position).Ticker.replace("$", ""));

        viewHolder.SettingsImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsListener.click(index);
            }
        });
        viewHolder.SpotToDexImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpotToDexListener.click(index);
            }
        });
        viewHolder.DexToSpotImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DexToSpotListener.click(index);
            }
        });
        viewHolder.DepositButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DepositListener.click(index);
            }
        });
        viewHolder.WithdrawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WithdrawListener.click(index);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(
            RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
