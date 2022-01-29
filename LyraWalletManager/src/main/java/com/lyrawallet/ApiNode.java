package com.lyrawallet;

import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ApiNode {
    static class AllProfitingAccounts {
        static class Gens {
            static int AccountType = 0;
            static int PType = 0;
            static double ShareRatio = 0f;
            static int Seats = 0;
            static String ProfitHash = null;
            static String Name = null;
            static String OwnerAccountId = null;
            static String RelatedTx = null;
            static String SourceHash = null;
            static String AccountId = null;
            static List<Pair<String, Double>> Balances = new ArrayList<>();
            static double Fee = 0;
            static String FeeCode = null;
            static int FeeType = 0;
            static String NonFungibleToken = null;
            static String VoteFor = null;
            static long Height = 0;
            static long TimeStamp = 0;
            static int Version = 0;
            static int BlockType = 0;
            static String PreviousHash = null;
            static String ServiceHash = null;
            static List<Pair<String, String>> Tags = new ArrayList<>();
            static String Hash = null;
            static String Signature = null;

            public int getAccountType() { return AccountType; }
            public int getPType() { return PType; }
            public double getShareRatio() { return ShareRatio; }
            public int getSeats() { return Seats; }
            public String getProfitHash() { return ProfitHash; }
            public String getName() { return Name; }
            public String getOwnerAccountId() { return OwnerAccountId; }
            public String getRelatedTx() { return RelatedTx; }
            public String getSourceHash() { return SourceHash; }
            public String getAccountId() { return AccountId; }
            public List<Pair<String, Double>> getBalances() { return Balances; }
            public double getFee() { return Fee; }
            public String getFeeCode() { return FeeCode; }
            public int getFeeType() { return FeeType; }
            public String getNonFungibleToken() { return NonFungibleToken; }
            public String getVoteFor() { return VoteFor; }
            public long getHeight() { return Height; }
            public long getTimeStamp() { return TimeStamp; }
            public int getVersion() { return Version; }
            public int getBlockType() { return BlockType; }
            public String getPreviousHash() { return PreviousHash; }
            public String getServiceHash() { return ServiceHash; }
            public List<Pair<String, String>> getTags() { return Tags; }
            public String getHash() { return Hash; }
            public String getSignature() { return Signature; }
        }
        double TotalProfit = 0f;
        public double getTotalProfit() { return TotalProfit; }

        AllProfitingAccounts fromJson(String data) {
            try {
                JSONArray arrObject = new JSONArray(data);
                for (int i = 0; i < arrObject.length(); i++) {
                    JSONObject gensObj = arrObject.getJSONObject(i);
                    Gens.AccountType = gensObj.getInt("accountType");
                    Gens.PType = gensObj.getInt("pType");
                    Gens.ShareRatio = gensObj.getDouble("shareRito");
                    Gens.Seats = gensObj.getInt("seats");
                    Gens.ProfitHash = gensObj.getString("profitHash");
                    Gens.Name = gensObj.getString("name");
                    Gens.OwnerAccountId = gensObj.getString("ownerAccountId");
                    Gens.RelatedTx = gensObj.getString("relatedTx");
                    Gens.SourceHash = gensObj.getString("sourceHash");
                    Gens.AccountId = gensObj.getString("accountID");
                    JSONObject balances = gensObj.getJSONObject("balances");
                    if (!balances.isNull("balances")) {
                        for (Iterator<String> it = balances.keys(); it.hasNext(); ) {
                            String key = it.next();
                            Gens.Balances.add(new Pair<>(key, balances.getDouble(key)));
                        }
                    }
                    Gens.Fee = gensObj.getDouble("fee");
                    Gens.FeeCode = gensObj.getString("feeCode");
                    Gens.FeeType = gensObj.getInt("feeType");
                    Gens.NonFungibleToken = gensObj.getString("nonFungibleToken");
                    Gens.VoteFor = gensObj.getString("voteFor");
                    Gens.Height = gensObj.getLong("height");
                    java.sql.Timestamp ts = java.sql.Timestamp.valueOf(
                            gensObj.getString("timeStamp").replace("T", " ").split("\\.")[0]);
                    Gens.TimeStamp = ts.getTime();
                    Gens.Version = gensObj.getInt("version");
                    Gens.BlockType = gensObj.getInt("blockType");
                    Gens.PreviousHash = gensObj.getString("previousHash");
                    Gens.ServiceHash = gensObj.getString("serviceHash");
                    JSONObject tags = gensObj.getJSONObject("tags");
                    if (!tags.isNull("tags")) {
                        for (Iterator<String> it = tags.keys(); it.hasNext(); ) {
                            String key = it.next();
                            Gens.Tags.add(new Pair<>(key, tags.getString(key)));
                        }
                    }
                    Gens.Hash = gensObj.getString("hash");
                    Gens.Signature = gensObj.getString("signature");
                    TotalProfit = arrObject.getJSONObject(i).getDouble("totalprofit");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }
    }
}
