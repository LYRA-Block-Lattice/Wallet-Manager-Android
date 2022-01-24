package com.lyrawallet.Ui.FragmentAccount;

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
    List<FragmentAccount.AccountHistoryEntry> list = Collections.emptyList();
    Context context;
    FragmentAccount.ClickListener listener;

    public AccountHistoryGalleryAdapter(List<FragmentAccount.AccountHistoryEntry> list,
                                        Context context, FragmentAccount.ClickListener listener)
    {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    public List<FragmentAccount.AccountHistoryEntry> getDataSet(){
        return list;
    }

    public void setDataSet(List<FragmentAccount.AccountHistoryEntry> newDataSet){
        this.list = newDataSet;
        notifyDataSetChanged();
    }

    public void addDataSet(List<FragmentAccount.AccountHistoryEntry> newDataSet){
        this.list.addAll(newDataSet);
        notifyItemRangeInserted(list.size() - newDataSet.size(), list.size() - 1);
    }

    public void addData(FragmentAccount.AccountHistoryEntry newData){
        this.list.add(newData);
        notifyItemInserted(this.list.size() - 1);
    }

    public void insertData(int index, FragmentAccount.AccountHistoryEntry newData){
        this.list.add(index, newData);
        notifyItemInserted(index);
    }

    public void insertData( FragmentAccount.AccountHistoryEntry newData){
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
                .setText(String.format(Locale.US, "TX: %d", list.get(position).Height));
        viewHolder.TickerImage
                .setImageResource(list.get(position).TickerImage);
        viewHolder.TickerName
                .setText(list.get(position).TickerName);
        viewHolder.Quantity
                .setText(String.format(Locale.US, "%.8f", list.get(position).Quantity));
        viewHolder.QuantityUsd
                .setText(String.format("%sUSD", String.format(Locale.US, "%.8f", list.get(position).Quantity * unitValuePerUsd)));
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
