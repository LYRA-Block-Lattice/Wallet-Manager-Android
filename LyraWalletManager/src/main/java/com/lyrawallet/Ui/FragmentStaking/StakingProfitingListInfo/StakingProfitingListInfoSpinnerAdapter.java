package com.lyrawallet.Ui.FragmentStaking.StakingProfitingListInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lyrawallet.R;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class StakingProfitingListInfoSpinnerAdapter extends ArrayAdapter<String> {
    private int Resource = 0;
    private Context ctx;
    double AmountToStake = 0;

    String[] AccountName;
    String[] AccountId;
    String[] AccountType;
    Double[] ShareRatio;
    Integer[] Seats;
    Long[] TimeStamp;
    Double[] TotalProfit;
    Double[] TotalStaked;
    Double[] YourShareWillBe;
    boolean[] fetch;

    public StakingProfitingListInfoSpinnerAdapter(Context context, int resource,
                                                  String[] accountName, String[] accountId, String[] accountType, Double[] shareRatio,
                                                  Integer[] seats, Long[] timeStamp, Double[] totalProfit,
                                                  Double[] totalStaked, Double[] yourShareWillBe) {
        super(context,  R.layout.profiting_account_info_entry, R.id.profitingAccountInfoAccountName, accountName);
        Resource = resource;
        this.ctx = context;
        this.AccountName = accountName;
        this.AccountId = accountId;
        this.AccountType = accountType;
        this.ShareRatio = shareRatio;
        this.Seats = seats;
        this.TimeStamp = timeStamp;
        this.TotalProfit = totalProfit;
        this.TotalStaked = totalStaked;
        this.YourShareWillBe = yourShareWillBe;
        fetch = new boolean[yourShareWillBe.length];
    }

    public void setAmountToStake(double amount) {
        AmountToStake = amount;
        notifyDataSetChanged();
    }
    public double getAmountToStake() { return AmountToStake; }

    public void setTotalStaked(int selectedAccount, double totalAmountStaked) {
        TotalStaked[selectedAccount] = totalAmountStaked;
        notifyDataSetChanged();
    }

    public void setYourShareWillBe(int selectedAccount, double yourShareWillBe) {
        YourShareWillBe[selectedAccount] = yourShareWillBe;
        notifyDataSetChanged();
    }

    public void setFetch(int selectedAccount) {
        fetch[selectedAccount] = true;
        notifyDataSetChanged();
    }

    public void clearFetch() {
        Arrays.fill(fetch, false);
        Arrays.fill(YourShareWillBe, 0d);
        notifyDataSetChanged();
    }

    public String getTime(long time){
        Date endDate = new Date(time);
        Format format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
        return format.format(endDate);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(Resource, parent, false);

        TextView accountName
                = (TextView) row
                .findViewById(R.id.profitingAccountInfoAccountName);
        TextView accountType
                = (TextView) row
                .findViewById(R.id.profitingAccountInfoAccountType);
        TextView shareRatio
                = (TextView) row
                .findViewById(R.id.profitingAccountInfoShareRatio);
        TextView seats
                = (TextView) row
                .findViewById(R.id.profitingAccountInfoSeats);
        TextView timeStamp
                = (TextView) row
                .findViewById(R.id.profitingAccountInfoTimeStamp);
        TextView totalProfit
                = (TextView) row
                .findViewById(R.id.profitingAccountInfoTotalProfit);
        TextView totalStaked
                = (TextView) row
                .findViewById(R.id.profitingAccountInfoTotalStaked);
        TextView yourShareWillBe
                = (TextView) row
                .findViewById(R.id.profitingAccountInfoYourShare);
        TextView fetchStatus
                = (TextView) row
                .findViewById(R.id.profitingAccountFetchStatusTextView);

        accountName.setText(AccountName[position]);
        accountType.setText(AccountType[position]);
        shareRatio.setText(String.format(Locale.US, "%.3f%%", ShareRatio[position] * 100));
        seats.setText(String.format(Locale.US, "%d", Seats[position]));
        timeStamp.setText(getTime(TimeStamp[position]));
        totalProfit.setText(String.format(Locale.US, "%.8f", TotalProfit[position]));
        totalStaked.setText(String.format(Locale.US, "%.8f", TotalStaked[position]));
        yourShareWillBe.setText(String.format(Locale.US, "%.3f%%", YourShareWillBe[position] * 100));
        String f = (String) (fetch[position] ? getContext().getText(R.string.Fetch) : getContext().getText(R.string.Not_fetch));
        fetchStatus.setText(f);

        return row;
    }
}
