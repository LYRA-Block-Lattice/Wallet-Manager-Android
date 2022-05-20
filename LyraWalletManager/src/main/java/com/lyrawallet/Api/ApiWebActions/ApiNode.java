package com.lyrawallet.Api.ApiWebActions;

import android.util.Pair;

import com.lyrawallet.GlobalLyra;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ApiNode {
    public static class AllProfitingAccounts {
        public static class AllProfitingAccountsEntry {
            int AccountType = 0;
            String PType = null;
            double ShareRatio = 0f;
            int Seats = 0;
            String ProfitHash = null;
            String Name = null;
            String OwnerAccountId = null;
            String RelatedTx = null;
            String SourceHash = null;
            String AccountId = null;
            List<Pair<String, Double>> Balances = new ArrayList<>();
            double Fee = 0;
            String FeeCode = null;
            int FeeType = 0;
            String NonFungibleToken = null;
            String VoteFor = null;
            long Height = 0;
            long TimeStamp = 0;
            int Version = 0;
            int BlockType = 0;
            String PreviousHash = null;
            String ServiceHash = null;
            List<Pair<String, String>> Tags = new ArrayList<>();
            String Hash = null;
            String Signature = null;

            public int getAccountType() {
                return AccountType;
            }
            public String getPType() {
                return PType;
            }
            public double getShareRatio() {
                return ShareRatio;
            }
            public int getSeats() {
                return Seats;
            }
            public String getProfitHash() {
                return ProfitHash;
            }
            public String getName() {
                return Name;
            }
            public String getOwnerAccountId() {
                return OwnerAccountId;
            }
            public String getRelatedTx() {
                return RelatedTx;
            }
            public String getSourceHash() {
                return SourceHash;
            }
            public String getAccountId() {
                return AccountId;
            }
            public List<Pair<String, Double>> getBalances() {
                return Balances;
            }
            public double getFee() {
                return Fee;
            }
            public String getFeeCode() {
                return FeeCode;
            }
            public int getFeeType() {
                return FeeType;
            }
            public String getNonFungibleToken() {
                return NonFungibleToken;
            }
            public String getVoteFor() {
                return VoteFor;
            }
            public long getHeight() {
                return Height;
            }
            public long getTimeStamp() {
                return TimeStamp;
            }
            public int getVersion() {
                return Version;
            }
            public int getBlockType() {
                return BlockType;
            }
            public String getPreviousHash() {
                return PreviousHash;
            }
            public String getServiceHash() {
                return ServiceHash;
            }
            public List<Pair<String, String>> getTags() {
                return Tags;
            }
            public String getHash() {
                return Hash;
            }
            public String getSignature() {
                return Signature;
            }

            double TotalProfit = 0f;

            public double getTotalProfit() {
                return TotalProfit;
            }
        }

        List<AllProfitingAccountsEntry> AccountList = new ArrayList<>();

        public List<AllProfitingAccountsEntry> getAccountList() {
            return AccountList;
        }

        public AllProfitingAccounts fromJson(String data) {
            try {
                JSONArray arrObject = new JSONArray(data);
                for (int i = 0; i < arrObject.length(); i++) {
                    JSONObject accObj = arrObject.getJSONObject(i);
                    JSONObject gensObj = accObj.getJSONObject("gens");
                    AllProfitingAccountsEntry entry = new AllProfitingAccountsEntry();
                    entry.AccountType = gensObj.getInt("accountType");
                    entry.PType = GlobalLyra.getProfitingAccountTypes(gensObj.getInt("pType"));
                    entry.ShareRatio = gensObj.getDouble("shareRito");
                    entry.Seats = gensObj.getInt("seats");
                    entry.ProfitHash = gensObj.getString("profitHash");
                    entry.Name = gensObj.getString("name");
                    entry.OwnerAccountId = gensObj.getString("ownerAccountId");
                    entry.RelatedTx = gensObj.getString("relatedTx");
                    entry.SourceHash = gensObj.getString("sourceHash");
                    entry.AccountId = gensObj.getString("accountID");
                    if (!gensObj.isNull("balances")) {
                        JSONObject balances = gensObj.getJSONObject("balances");
                        for (Iterator<String> it = balances.keys(); it.hasNext(); ) {
                            String key = it.next();
                            entry.Balances.add(new Pair<>(key, balances.getDouble(key)));
                        }
                    }
                    entry.Fee = gensObj.getDouble("fee");
                    entry.FeeCode = gensObj.getString("feeCode");
                    entry.FeeType = gensObj.getInt("feeType");
                    entry.NonFungibleToken = gensObj.getString("nonFungibleToken");
                    entry.VoteFor = gensObj.getString("voteFor");
                    entry.Height = gensObj.getLong("height");
                    java.sql.Timestamp ts = java.sql.Timestamp.valueOf(
                            gensObj.getString("timeStamp").replace("T", " ").split("\\.")[0]);
                    entry.TimeStamp = ts.getTime();
                    entry.Version = gensObj.getInt("version");
                    entry.BlockType = gensObj.getInt("blockType");
                    entry.PreviousHash = gensObj.getString("previousHash");
                    entry.ServiceHash = gensObj.getString("serviceHash");
                    if (!gensObj.isNull("tags")) {
                        JSONObject tags = gensObj.getJSONObject("tags");
                        for (Iterator<String> it = tags.keys(); it.hasNext(); ) {
                            String key = it.next();
                            entry.Tags.add(new Pair<>(key, tags.getString(key)));
                        }
                    }
                    entry.Hash = gensObj.getString("hash");
                    entry.Signature = gensObj.getString("signature");
                    entry.TotalProfit = accObj.getDouble("totalprofit");
                    AccountList.add(entry);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }
    }

    public static class FindAllStakings {
        public static class FindAllStakingsEntry {
            String StkAccount = null;
            String OwnerAccount = null;
            long Time = 0;
            int Days = 0;
            double Amount = 0f;
            boolean CompoundMode = false;

            public String getStkAccount() {
                return StkAccount;
            }

            public String getOwnerAccount() {
                return OwnerAccount;
            }

            public long getTime() {
                return Time;
            }

            public int getDays() {
                return Days;
            }

            public double getAmount() {
                return Amount;
            }

            public boolean getCompoundMode() {
                return CompoundMode;
            }
        }

        List<FindAllStakingsEntry> AccountList = new ArrayList<>();

        public List<FindAllStakingsEntry> getAccountList() {
            return AccountList;
        }

        public FindAllStakings fromJson(String data) {
            try {
                JSONArray arrObject = new JSONArray(data);
                for (int i = 0; i < arrObject.length(); i++) {
                    JSONObject accObj = arrObject.getJSONObject(i);
                    FindAllStakingsEntry entry = new FindAllStakingsEntry();
                    entry.StkAccount = accObj.getString("stkAccount");
                    entry.OwnerAccount = accObj.getString("ownerAccount");
                    java.sql.Timestamp ts = java.sql.Timestamp.valueOf(
                            accObj.getString("time").replace("T", " ").split("\\.")[0]);
                    entry.Time = ts.getTime();
                    entry.Days = accObj.getInt("days");
                    entry.Amount = accObj.getDouble("amount");
                    entry.CompoundMode = accObj.getBoolean("compoundMode");
                    AccountList.add(entry);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }
    }

    public static class GetPool {
            String PoolFactoryAccountId = null;
            String PoolAccountId = null;
            String Token0 = null;
            String Token1 = null;

            String RelatedTx = null;
            List<Pair<String, Double>> Shares = new ArrayList<>();

            String DestinationAccountId = null;
            String AccountID = null;
            List<Pair<String, Double>> Balances = new ArrayList<>();
            double Fee = 0.0;
            String FeeCode = null;
            int FeeType = 0;
            String NonFungibleToken = null;
            String VoteFor = null;
            long Height = 0;
            String TimeStamp = null;
            long Time = 0;
            int Version = 0;
            int BlockType = 0;
            String PreviousHash = null;
            String ServiceHash = null;
            List<Pair<String, String>> Tags = new ArrayList<>();
            String Hash = null;
            String Signature = null;
            int ResultBlockType = 0;
            int ResultCode = 0;
            String ResultMessage = null;

            public String getPoolFactoryAccountId() { return PoolFactoryAccountId; }
            public String getPoolAccountId() { return PoolAccountId; }
            public String getToken0() { return Token0; }
            public String getToken1() { return Token1; }

            public String getRelatedTx() { return RelatedTx; }
            public List<Pair<String, Double>> getShares() { return Shares; }

            public String getDestinationAccountId() { return DestinationAccountId; }
            public String getAccountID() { return AccountID; }
            public List<Pair<String, Double>> getBalances() { return Balances; }
            public double getFee() { return Fee; }
            public String getFeeCode() { return FeeCode; }
            public int getFeeType() { return FeeType; }
            public String getNonFungibleToken() { return NonFungibleToken; }
            public String getVoteFor() { return VoteFor; }
            public long getHeight() { return Height; }
            public String getTimeStamp() { return TimeStamp; }
            public int getVersion() { return Version; }
            public int getBlockType() { return BlockType; }
            public String getPreviousHash() { return PreviousHash; }
            public String getServiceHash() { return ServiceHash; }
            public List<Pair<String, String>> getTags() { return Tags; }
            public String getHash() { return Hash; }
            public String getSignature() { return Signature; }
            public int getResultBlockType() { return ResultBlockType; }
            public int getResultCode() { return ResultCode; }
            public String getResultMessage() { return ResultMessage; }

        public GetPool fromJson(String data) {
            try {
                JSONObject obj = new JSONObject(data);

                PoolFactoryAccountId = obj.getString("poolFactoryAccountId");
                PoolAccountId = obj.getString("poolAccountId");
                Token0 = obj.getString("token0");
                Token1 = obj.getString("token1");
                String blockData = obj.getString("blockData");
                JSONObject blockDataObj = new JSONObject(blockData);
                RelatedTx = blockDataObj.getString("RelatedTx");
                if (!blockDataObj.isNull("Shares")) {
                    JSONObject shares = blockDataObj.getJSONObject("Shares");
                    for (Iterator<String> it = shares.keys(); it.hasNext(); ) {
                        String key = it.next();
                        Shares.add(new Pair<>(key, ((double) shares.getLong(key)) / 1_000_000_000_000.0));
                    }
                }

                DestinationAccountId = blockDataObj.getString("DestinationAccountId");
                AccountID = blockDataObj.getString("AccountID");
                JSONObject balances = blockDataObj.getJSONObject("Balances");
                for (Iterator<String> it = balances.keys(); it.hasNext(); ) {
                    String key = it.next();
                    Balances.add(new Pair<>(key, ((double) balances.getLong(key)) / 100_000_000.0));
                }
                Fee = blockDataObj.getDouble("Fee");
                FeeCode = blockDataObj.getString("FeeCode");
                FeeType = blockDataObj.getInt("FeeType");
                NonFungibleToken = blockDataObj.getString("NonFungibleToken");
                VoteFor = blockDataObj.getString("VoteFor");
                Height = blockDataObj.getLong("Height");
                TimeStamp = blockDataObj.getString("TimeStamp");
                java.sql.Timestamp ts = java.sql.Timestamp.valueOf(
                        blockDataObj.getString("TimeStamp").replace("T", " ").split("\\.")[0]);
                Time = ts.getTime();
                Version = blockDataObj.getInt("Version");
                BlockType = blockDataObj.getInt("BlockType");
                PreviousHash = blockDataObj.getString("PreviousHash");
                ServiceHash = blockDataObj.getString("ServiceHash");
                if (!blockDataObj.isNull("Tags")) {
                    JSONObject tags = blockDataObj.getJSONObject("Tags");
                    for (Iterator<String> it = tags.keys(); it.hasNext(); ) {
                        String key = it.next();
                        Tags.add(new Pair<>(key, tags.getString(key)));
                    }
                }
                Hash = blockDataObj.getString("Hash");
                Signature = blockDataObj.getString("Signature");
                ResultBlockType = obj.getInt("resultBlockType");
                ResultCode = obj.getInt("resultCode");
                ResultMessage = obj.getString("resultMessage");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }
    }
}
