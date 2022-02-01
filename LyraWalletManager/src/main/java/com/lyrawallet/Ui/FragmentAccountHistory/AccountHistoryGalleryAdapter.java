package com.lyrawallet.Ui.FragmentAccountHistory;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.lyrawallet.Global;
import com.lyrawallet.R;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class AccountHistoryGalleryAdapter extends RecyclerView.Adapter<AccountHistoryViewHolder> {
    List<FragmentAccountHistory.AccountHistoryEntry> list = Collections.emptyList();
    Context context;
    FragmentAccountHistory.ClickListener listener;

    public AccountHistoryGalleryAdapter(List<FragmentAccountHistory.AccountHistoryEntry> list,
                                        Context context, FragmentAccountHistory.ClickListener listener)
    {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    public List<FragmentAccountHistory.AccountHistoryEntry> getDataSet(){
        return list;
    }

    public void setDataSet(List<FragmentAccountHistory.AccountHistoryEntry> newDataSet){
        this.list = newDataSet;
        notifyDataSetChanged();
    }

    public void addDataSet(List<FragmentAccountHistory.AccountHistoryEntry> newDataSet){
        this.list.addAll(newDataSet);
        notifyItemRangeInserted(list.size() - newDataSet.size(), list.size() - 1);
    }

    public void addData(FragmentAccountHistory.AccountHistoryEntry newData){
        this.list.add(newData);
        notifyItemInserted(this.list.size() - 1);
    }

    public void insertData(int index, FragmentAccountHistory.AccountHistoryEntry newData){
        this.list.add(index, newData);
        notifyItemInserted(index);
    }

    public void insertData( FragmentAccountHistory.AccountHistoryEntry newData){
        this.list.add(0, newData);
        notifyItemInserted(0);
    }

    public void clear(){
        int size = list.size();
        this.list.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void removeRange(int itemStart, int itemCount){
        int i = 0;
        for (; i < itemCount; i++) {
            if(itemStart + i < this.list.size()) {
                this.list.remove(itemStart);
            } else {
                break;
            }
        }
        notifyItemRangeRemoved(itemStart, i);
    }

    public void updateUnitPrice(String ticker) {
        for (int i = 0; i < this.list.size(); i++) {
            if(this.list.get(i).TickerName.equals(ticker))
                notifyItemChanged(i);
        }
    }

    @Override
    public AccountHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the layout
        View photoView = inflater.inflate(R.layout.wallet_transaction_entry_card, parent, false);
        AccountHistoryViewHolder viewHolder = new AccountHistoryViewHolder(photoView);
        return viewHolder;
    }

    @Override
    public void
    onBindViewHolder(final AccountHistoryViewHolder viewHolder,
                     final int position)
    {
        double unitValuePerUsd = Global.getTokenPrice(new Pair<>(list.get(position).TickerName, "tether/USDT"));
        if(unitValuePerUsd == 0) {
            unitValuePerUsd = Global.getTokenPrice(new Pair<>(list.get(position).TickerName, "USD"));
        }
        final int index = viewHolder.getAdapterPosition();
        viewHolder.Height
                .setText(list.get(position).Height == - 1 ? "" : String.format(Locale.US, "TX: %d", list.get(position).Height));
        viewHolder.TickerImage
                .setImageResource(list.get(position).TickerImage);
        viewHolder.TickerName
                .setText(list.get(position).TickerName);
        viewHolder.Amount
                .setText(String.format(Locale.US, "%.8f", list.get(position).Amount));
        viewHolder.AmountUsd
                .setText(String.format("%sUSD", String.format(Locale.US, "%.8f", list.get(position).Amount * unitValuePerUsd)));
        viewHolder.ValueUsdPerUnit
                .setText(String.format("%sUSD", String.format(Locale.US, "%.8f", unitValuePerUsd)));
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                listener.click(index);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(
            RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
