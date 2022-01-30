package com.lyrawallet.Ui.FragmentStaking;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.lyrawallet.Global;
import com.lyrawallet.GlobalLyra;
import com.lyrawallet.R;
import com.lyrawallet.Ui.FragmentAccountHistory.FragmentAccountHistory;
import com.lyrawallet.Ui.UiHelpers;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StakingGalleryAdapter extends RecyclerView.Adapter<StakingViewHolder> {
    List<FragmentStaking.StakingEntry> list = Collections.emptyList();
    Context Context;
    FragmentStaking.ClickListener Listener;

    public StakingGalleryAdapter(List<FragmentStaking.StakingEntry> list,
                                        Context context,
                                 FragmentStaking.ClickListener listener)
    {
        this.Listener = listener;
        this.list = list;
        this.Context = context;
    }

    public List<FragmentStaking.StakingEntry> getDataSet(){
        return list;
    }

    public void setDataSet(List<FragmentStaking.StakingEntry> newDataSet){
        this.list = newDataSet;
        notifyDataSetChanged();
    }

    public void addDataSet(List<FragmentStaking.StakingEntry> newDataSet){
        this.list.addAll(newDataSet);
        notifyItemRangeInserted(list.size() - newDataSet.size(), list.size() - 1);
    }

    public void addData(FragmentStaking.StakingEntry newData){
        this.list.add(newData);
        notifyItemInserted(this.list.size() - 1);
    }

    public void insertData(int index, FragmentStaking.StakingEntry newData){
        this.list.add(index, newData);
        notifyItemInserted(index);
    }

    public void insertData( FragmentStaking.StakingEntry newData){
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

    public String getExpirationTime(long time, int days){
        Date endDate = new Date(time);
        Calendar c = Calendar.getInstance();
        c.setTime(endDate);
        c.add(Calendar.DATE, days);
        endDate = c.getTime();
        Format format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
        return format.format(endDate);
    }

    public boolean isExpired(long time, int days){
        Date endDate = new Date(time);
        Calendar c = Calendar.getInstance();
        c.setTime(endDate);
        c.add(Calendar.DATE, days);
        endDate = c.getTime();
        Date currentDate = new Date();
        if(endDate.getTime() < currentDate.getTime())
            return true;
        return false;
    }

    @Override
    public StakingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the layout
        View photoView = inflater.inflate(R.layout.staking_account_entry_card, parent, false);
        return new StakingViewHolder(photoView);
    }

    @Override
    public void
    onBindViewHolder(final StakingViewHolder viewHolder,
                     final int position)
    {

        final int index = viewHolder.getAdapterPosition();
        boolean isExpired = isExpired(list.get(position).ExpiryDate, list.get(position).Days);
        viewHolder.AccountName
                .setText(list.get(position).AccountName);
        viewHolder.StakingAccountId
                .setText(UiHelpers.getShortAccountId(list.get(position).StakingAccountId, 7));
        viewHolder.ProfitingAccountId
                .setText(UiHelpers.getShortAccountId(list.get(position).ProfitingAccountId, 7));
        viewHolder.ExpiryDate
                .setText(getExpirationTime(list.get(position).ExpiryDate, list.get(position).Days));
        viewHolder.Validity
                .setText(String.format(Locale.US, "%d %s", list.get(position).Days, list.get(position).Days == 1 ? Context.getString(R.string.Day) : Context.getString(R.string.Days)));
        viewHolder.Amount
                .setText(String.format(Locale.US, "%.8f LYR", list.get(position).Amount));
        if(isExpired) {
            viewHolder.Expired
                    .setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_money_off_csred_24, 0);
        } else {
            viewHolder.Expired
                    .setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_outline_attach_money_24, 0);
        }
        viewHolder.Expired
                .setText(isExpired ? Context.getText(R.string.Expired) : Context.getText(R.string.Valid));
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Listener.click(index);
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
