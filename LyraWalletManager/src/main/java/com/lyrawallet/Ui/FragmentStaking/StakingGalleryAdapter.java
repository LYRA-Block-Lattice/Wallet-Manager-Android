package com.lyrawallet.Ui.FragmentStaking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lyrawallet.R;
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
    FragmentStaking.ClickListener SelectedListener;
    FragmentStaking.ClickListener StakeMoreListener;
    FragmentStaking.ClickListener UnstakeListener;
    int selectedItem = -1;

    public StakingGalleryAdapter(List<FragmentStaking.StakingEntry> list,
                                        Context context,
                                 FragmentStaking.ClickListener selectedListener,
                                 FragmentStaking.ClickListener stakeMoreListener,
                                 FragmentStaking.ClickListener unstakeListener)
    {
        this.SelectedListener = selectedListener;
        this.StakeMoreListener = stakeMoreListener;
        this.UnstakeListener = unstakeListener;
        this.list = list;
        this.Context = context;
    }

    void setSelected(int index) {
        if (index == selectedItem) {
            this.notifyItemChanged(selectedItem);
            selectedItem = -1;
        } else {
            this.notifyItemChanged(index);
            this.notifyItemChanged(selectedItem);
            selectedItem = index;
        }
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
        if(time >= 95617627200000L)
            return "?";
        Date endDate = new Date(time);
        Calendar c = Calendar.getInstance();
        c.setTime(endDate);
        c.add(Calendar.DATE, days);
        endDate = c.getTime();
        Format format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
        return format.format(endDate);
    }

    public boolean isExpired(long time, int days){
        if(time >= 95617627200000L)
            return true;
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
    public StakingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the layout
        View photoView = inflater.inflate(R.layout.entry_card_staking_account, parent, false);
        return new StakingViewHolder(photoView);
    }

    @Override
    public void
    onBindViewHolder(@NonNull final StakingViewHolder viewHolder,
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

        if(isExpired) {
            viewHolder.Unstake.setEnabled(true);
        } else {
            viewHolder.Unstake.setEnabled(false);
        }

        if(selectedItem == index) {
            viewHolder.StakeMore.setVisibility(View.VISIBLE);
            viewHolder.Unstake.setVisibility(View.VISIBLE);
            //this.notifyItemChanged(index);
        } else {
            viewHolder.StakeMore.setVisibility(View.GONE);
            viewHolder.Unstake.setVisibility(View.GONE);
        }
        if(list.get(position).Amount == 0f) {
            viewHolder.StakeMore.setText(R.string.Stake);
            viewHolder.Unstake.setEnabled(false);
        }

        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectedListener.click(index);
            }
        });
        viewHolder.StakeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StakeMoreListener.click(index);
            }
        });
        viewHolder.Unstake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UnstakeListener.click(index);
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
