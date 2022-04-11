package com.lyrawallet.Api.ApiWebActions;

import android.util.Pair;

import org.bouncycastle.util.StringList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ApiDex {
    public static class AllBalances {
        public static class AllBalancesEntry {
            int AccountType = 0;
            String IntSymbol = null;
            String ExtSymbol = null;
            String ExtProvider = null;
            String ExtAddress = null;
            String OwnerAccountId = null;
            String RelatedTx = null;
            String Name = null;
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
            public String getIntSymbol() {
                return IntSymbol;
            }
            public String getExtSymbol() {
                return ExtSymbol;
            }
            public String getExtProvider() {
                return ExtProvider;
            }
            public String getExtAddress() {
                return ExtAddress;
            }
            public String getOwnerAccountId() {
                return OwnerAccountId;
            }
            public String getRelatedTx() {
                return RelatedTx;
            }
            public String getName() {
                return Name;
            }
            public String getDestinationAccountId() {
                return DestinationAccountId;
            }
            public String getAccountID() {
                return AccountID;
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
            public String getTimeStamp() {
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
        }

        List<ApiDex.AllBalances.AllBalancesEntry> BalancesList = new ArrayList<>();

        public List<ApiDex.AllBalances.AllBalancesEntry> getBalancesList() {
            return BalancesList;
        }

        public AllBalances fromJson(String data) {
            try {
                JSONObject assertsObject = new JSONObject(data);
                JSONArray arrObject = assertsObject.getJSONArray("blockDatas");
                for (int i = 0; i < arrObject.length(); i++) {
                    ApiDex.AllBalances.AllBalancesEntry entry = new ApiDex.AllBalances.AllBalancesEntry();
                    String ent = arrObject.getString(i);
                    JSONObject tokenObj = new JSONObject(ent);
                    if(!tokenObj.isNull("AccountType"))
                        entry.AccountType = tokenObj.getInt("AccountType");
                    entry.IntSymbol = tokenObj.getString("IntSymbol");
                    entry.ExtSymbol = tokenObj.getString("ExtSymbol");
                    entry.ExtProvider = tokenObj.getString("ExtProvider");
                    entry.ExtAddress = tokenObj.getString("ExtAddress");
                    entry.OwnerAccountId = tokenObj.getString("OwnerAccountId");
                    entry.RelatedTx = tokenObj.getString("RelatedTx");
                    entry.Name = tokenObj.getString("Name");
                    if(!tokenObj.isNull("DestinationAccountId"))
                        entry.DestinationAccountId = tokenObj.getString("DestinationAccountId");
                    entry.AccountID = tokenObj.getString("AccountID");
                    if (!tokenObj.isNull("Balances")) {
                        JSONObject balances = tokenObj.getJSONObject("Balances");
                        for (Iterator<String> it = balances.keys(); it.hasNext(); ) {
                            String key = it.next();
                            entry.Balances.add(new Pair<>(key, balances.getDouble(key)));
                        }
                    }
                    entry.Fee = tokenObj.getDouble("Fee");
                    entry.FeeCode = tokenObj.getString("FeeCode");
                    entry.FeeType = tokenObj.getInt("FeeType");
                    entry.NonFungibleToken = tokenObj.getString("NonFungibleToken");
                    entry.VoteFor = tokenObj.getString("VoteFor");
                    entry.Height = tokenObj.getLong("Height");
                    entry.TimeStamp = tokenObj.getString("TimeStamp");
                    entry.Version = tokenObj.getInt("Version");
                    entry.BlockType = tokenObj.getInt("BlockType");
                    entry.PreviousHash = tokenObj.getString("PreviousHash");
                    entry.ServiceHash = tokenObj.getString("ServiceHash");
                    if (!tokenObj.isNull("Tags")) {
                        JSONObject tags = tokenObj.getJSONObject("Tags");
                        for (Iterator<String> it = tags.keys(); it.hasNext(); ) {
                            String key = it.next();
                            entry.Tags.add(new Pair<>(key, tags.getString(key)));
                        }
                    }
                    entry.Hash = tokenObj.getString("Hash");
                    entry.Signature = tokenObj.getString("Signature");

                    BalancesList.add(entry);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }
    }

    public static class AllSupported {
        public static class AllSupportedEntry {
            String Name = null;
            String CoinGeckoName = null;
            String Url = null;
            String Symbol = null;
            String NetworkProvider = null;
            String Contract = null;
            double MinDeposit = 0.0;
            double DepositFee = 0.0;
            String ConfirmationInfo = null;
            double MinWithdraw = 0.0;
            double WithdrawFee = 0.0;
            double DailyWithdrawLimit = 0.0;

            public String getName() { return Name ; }
            public String getCoinGeckoName() { return CoinGeckoName ; }
            public String getUrl() { return Url ; }
            public String getSymbol() { return Symbol ; }
            public String getNetworkProvider() { return NetworkProvider ; }
            public String getContract() { return Contract ; }
            public double getMinDeposit() { return MinDeposit ; }
            public double getDepositFee() { return DepositFee ; }
            public String getConfirmationInfo() { return ConfirmationInfo ; }
            public double getMinWithdraw() { return MinWithdraw ; }
            public double getWithdrawFee() { return WithdrawFee ; }
            public double getDailyWithdrawLimit() { return DailyWithdrawLimit ; }
        }

        List<ApiDex.AllSupported.AllSupportedEntry> SupportedList = new ArrayList<>();

        public List<ApiDex.AllSupported.AllSupportedEntry> getSuportedList() {
            return SupportedList;
        }
        public AllSupported fromJson(String data) {
            try {
                JSONObject assertsObject = new JSONObject(data);
                JSONArray arrObject = assertsObject.getJSONArray("asserts");
                for (int i = 0; i < arrObject.length(); i++) {
                    ApiDex.AllSupported.AllSupportedEntry entry = new ApiDex.AllSupported.AllSupportedEntry();
                    JSONObject tokenObj = arrObject.getJSONObject(i);
                    entry.Name = tokenObj.getString("name");
                    if(!tokenObj.isNull("coinGeckoName"))
                        entry.CoinGeckoName = tokenObj.getString("coinGeckoName");
                    entry.Url = tokenObj.getString("url");
                    entry.Symbol = tokenObj.getString("symbol");
                    entry.NetworkProvider = tokenObj.getString("networkProvider");
                    entry.Contract = tokenObj.getString("contract");
                    entry.MinDeposit = tokenObj.getDouble("minDeposit");
                    entry.DepositFee = tokenObj.getDouble("depositFee");
                    entry.ConfirmationInfo = tokenObj.getString("confirmationInfo");
                    entry.MinWithdraw = tokenObj.getDouble("minWithdraw");
                    entry.WithdrawFee = tokenObj.getDouble("withdrawFee");
                    entry.DailyWithdrawLimit = tokenObj.getDouble("dailyWithdrawLimit");
                    SupportedList.add(entry);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }
    }
}
