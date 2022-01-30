package com.lyrawallet.Api.ApiRpcActions;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ApiRpcActionsBrokerAccounts {
        String OwnerId = null;
        public class ProfitingEntry {
            String Name = null;
            String Type = null;
            double ShareRation = 0f;
            int Seats = 0;
            String ProfitingId = null;
            String OwnerId = null;

            public String getName() { return Name; }
            public String getType() { return Type; }
            public double getShareRation() { return ShareRation; }
            public int getSeats() { return Seats; }
            public String getProfitingId() { return ProfitingId; }
            public String getOwnerId() { return OwnerId; }
        }

        public class StakingEntry {
            String Name;
            String VotingId = null;
            String OwnerId = null;
            String StakingId = null;
            int Days = 0;
            long Start = 0;
            double Amount = 0f;

            public String getName() { return Name; }
            public String getVotingId() { return VotingId; }
            public String getOwnerId() { return OwnerId; }
            public String getStakingId() { return StakingId; }
            public int getDays() { return Days; }
            public long getStart() { return Start; }
            public double getAmount() { return Amount; }
        }
    List<ProfitingEntry> ProfitingAccount = null;
    List<StakingEntry> StakingAccount = null;

    public ApiRpcActionsBrokerAccounts fromJson(String data) {
        try {
            JSONObject obj = new JSONObject(data);
            OwnerId = obj.getString("owner");
            if(!obj.isNull("profits")) {
                ProfitingAccount = new ArrayList<>();
                JSONArray profitsArray = obj.getJSONArray("profits");
                for (int i = 0; i < profitsArray.length(); i++) {
                    JSONObject profitsObject = profitsArray.getJSONObject(i);
                    ProfitingEntry entry = new ProfitingEntry();
                    entry.Name = profitsObject.getString("name");
                    entry.Type = profitsObject.getString("type");
                    entry.ShareRation = profitsObject.getDouble("shareratio");
                    entry.Seats = profitsObject.getInt("seats");
                    entry.ProfitingId = profitsObject.getString("pftid");
                    entry.OwnerId = profitsObject.getString("owner");
                    ProfitingAccount.add(entry);
                }
            }
            if(!obj.isNull("stakings")) {
                StakingAccount = new ArrayList<>();
                JSONArray stakingsArray = obj.getJSONArray("stakings");
                for (int i = 0; i < stakingsArray.length(); i++) {
                    JSONObject profitsObject = stakingsArray.getJSONObject(i);
                    StakingEntry entry = new StakingEntry();
                    entry.Name = profitsObject.getString("name");
                    entry.VotingId = profitsObject.getString("voting");
                    entry.OwnerId = profitsObject.getString("owner");
                    entry.StakingId = profitsObject.getString("stkid");
                    entry.Days = profitsObject.getInt("days");
                    entry.Amount = profitsObject.getDouble("amount");
                    String start = profitsObject.getString("start");
                    //Instant utcDateTimeForCurrentDateTime = Instant.from(Instant.parse(start));
                    //entry.Start = utcDateTimeForCurrentDateTime.toEpochMilli();
                    java.sql.Timestamp ts = java.sql.Timestamp.valueOf(start.replace("T", " ").split("\\.")[0]);
                    entry.Start = ts.getTime();
                    StakingAccount.add(entry);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public List<ProfitingEntry> getProfitingAccountList() {
        return ProfitingAccount;
    }

    public List<StakingEntry> getStakingAccount() {
        return StakingAccount;
    }
}
