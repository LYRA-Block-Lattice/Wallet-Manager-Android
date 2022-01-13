package com.lyrawallet.Ui.FragmentAccount;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.lyrawallet.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AccountHistoryGaleryAdapter extends RecyclerView.Adapter<AccountHistoryViewHolder> {
    List<FragmentAccount.AccountHistoryEntry> list
            = Collections.emptyList();

    Context context;
    FragmentAccount.ClickListener listener;

    public AccountHistoryGaleryAdapter(List<FragmentAccount.AccountHistoryEntry> list,
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


    @Override
    public AccountHistoryViewHolder
    onCreateViewHolder(ViewGroup parent,
                       int viewType) {
        Context context
                = parent.getContext();
        LayoutInflater inflater
                = LayoutInflater.from(context);
        // Inflate the layout
        View photoView
                = inflater
                .inflate(R.layout.wallet_transaction_entry_card,
                        parent, false);

        AccountHistoryViewHolder viewHolder
                = new AccountHistoryViewHolder(photoView);
        return viewHolder;
    }

    @Override
    public void
    onBindViewHolder(final AccountHistoryViewHolder viewHolder,
                     final int position)
    {
        final int index = viewHolder.getAdapterPosition();
        viewHolder.TickerImage
                .setImageResource(list.get(position).TickerImage);
        viewHolder.TickerName
                .setText(list.get(position).TickerName);
        viewHolder.Quantity
                .setText(String.format("%.3f", list.get(position).Quantity));
        viewHolder.QuantityUsd
                .setText(String.format("%.3f", list.get(position).Quantity * list.get(position).ValueUsdPerUnit) + "USD");
        viewHolder.ValueUsdPerUnit
                .setText(String.format("%f", list.get(position).ValueUsdPerUnit) + "USD");
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
    }}
