package com.lyrawallet.Ui.FragmentStaking;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lyrawallet.R;

public class StakingViewHolder extends RecyclerView.ViewHolder {
    TextView AccountName;
    TextView StakingAccountId;
    TextView ProfitingAccountId;
    TextView ExpiryDate;
    TextView Validity;
    TextView Amount;
    TextView Expired;
    Button StakeMore;
    Button Unstake;
    View view;

    StakingViewHolder(View itemView) {
        super(itemView);
        AccountName
                = (TextView) itemView
                .findViewById(R.id.stakingAccountNameTextView);
        StakingAccountId
                = (TextView) itemView
                .findViewById(R.id.stakingAccountIdTextView);
        ProfitingAccountId
                = (TextView) itemView
                .findViewById(R.id.profitingAccountIdTextView);
        ExpiryDate
                = (TextView) itemView
                .findViewById(R.id.profitingAccountExpirationDateTextView);
        Validity
                =  (TextView) itemView
                .findViewById(R.id.profitingAccountValidityTextView);
        Amount
                = (TextView) itemView
                .findViewById(R.id.profitingAccountAmountTextView);
        Expired
                = (TextView) itemView
                .findViewById(R.id.profitingAccountStatusTextView);
        StakeMore
                = (Button) itemView
                .findViewById(R.id.dialogAccountStatusStakeMore);
        Unstake
                = (Button) itemView
                .findViewById(R.id.dialogAccountStatusUnstake);
        view = itemView;
    }
}
