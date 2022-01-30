package com.lyrawallet.Ui.FragmentStaking.ProfitingListInfo;

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
import java.util.Date;
import java.util.Locale;

public class ProfitingListInfoSpinnerAdapter extends ArrayAdapter<String> {
    private int Resource = 0;
    private final Context ctx;
    String[] AccountName;
    String[] AccountType;
    Double[] ShareRatio;
    Integer[] Seats;
    Long[] TimeStamp;
    Double[] TotalProfit;
    Double[] TotalStaked;
    Double[] YourShareWillBe;

    public ProfitingListInfoSpinnerAdapter(Context context, int resource,
                                           String[] accountName, String[] accountType, Double[] shareRatio,
                                           Integer[] seats, Long[] timeStamp, Double[] totalProfit,
                                           Double[] totalStaked, Double[] yourShareWillBe) {
        super(context,  R.layout.profiting_account_info_entry, R.id.profitingAccountInfoAccountName, accountName);
        Resource = resource;
        this.ctx = context;
        this.AccountName = accountName;
        this.AccountType = accountType;
        this.ShareRatio = shareRatio;
        this.Seats = seats;
        this.TimeStamp = timeStamp;
        this.TotalProfit = totalProfit;
        this.TotalStaked = totalStaked;
        this.YourShareWillBe = yourShareWillBe;
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

        accountName.setText(AccountName[position]);
        accountType.setText(AccountType[position]);
        shareRatio.setText(String.format(Locale.US, "%.3f%%", ShareRatio[position] * 100));
        seats.setText(String.format(Locale.US, "%d", Seats[position]));
        timeStamp.setText(getTime(TimeStamp[position]));
        totalProfit.setText(String.format(Locale.US, "%.8f", TotalProfit[position]));
        totalStaked.setText(String.format(Locale.US, "%.8f", TotalStaked[position]));
        yourShareWillBe.setText(String.format(Locale.US, "%.3f", YourShareWillBe[position] * 100));

        return row;
    }
}
