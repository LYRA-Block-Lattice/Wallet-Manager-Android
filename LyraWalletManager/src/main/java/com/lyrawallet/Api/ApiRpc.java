package com.lyrawallet.Api;

import static com.lyrawallet.Api.ApiRpc.ProfitingType.MERCHANT;
import static com.lyrawallet.Api.ApiRpc.ProfitingType.NODE;
import static com.lyrawallet.Api.ApiRpc.ProfitingType.ORACLE;
import static com.lyrawallet.Api.ApiRpc.ProfitingType.YIELD;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.lyrawallet.Api.ApiRpcActions.ApiRpcActionsBrokerAccounts;
import com.lyrawallet.Api.ApiRpcActions.ApiRpcActionsHistory;
import com.lyrawallet.Api.Network.NetworkRpc;
import com.lyrawallet.Global;
import com.lyrawallet.GlobalLyra;
import com.lyrawallet.MainActivity;
import com.lyrawallet.R;
import com.lyrawallet.Ui.FragmentManagerUser;
import com.lyrawallet.Ui.FragmentSwap.FragmentSwap;
import com.lyrawallet.Ui.UiDialog;
import com.lyrawallet.Ui.UiHelpers;
import com.lyrawallet.Ui.UiUpdates;
import com.lyrawallet.Util.Concatenate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class ApiRpc extends MainActivity implements NetworkRpc.RpcTaskInformer {

    public enum ProfitingType {
        NODE,
        ORACLE,
        MERCHANT,
        YIELD
    }
    // Global entry class
    public class QueueEntry {
        String Api = null;
        String NetworkName = null;
        String AccountName = null;
        String Data = null;
        String TokenName = null;
        String DestinationId = null;
        double Amount = 0.0f;
        QueueEntry(String api) {
            Api = api;
            NetworkName = Global.getCurrentNetworkName();
            AccountName = Global.getSelectedAccountName();
        }
        QueueEntry(String api, String tokenName, String destinationId, double amount) {
            Api = api;
            NetworkName = Global.getCurrentNetworkName();
            AccountName = Global.getSelectedAccountName();
            TokenName = tokenName;
            DestinationId = destinationId;
            Amount = amount;
        }
        public String getApi() {
            return Api;
        }
        public String getNetworkName() {
            return NetworkName;
        }
        public String getAccountName() {
            return AccountName;
        }
        public String getData() {
            return Data;
        }
        public String getTokenName() {
            return TokenName;
        }
        public String getDestinationId() {
            return DestinationId;
        }
        public double getAmount() {
            return Amount;
        }

        public void setData(String data) {
            Data = data;
        }
    }
    public static class Action {
        String Api = null;
        String Network = null;
        String ActionPurpose = null;
        String AccountName = null;
        String AccountId = null;
        String DestinationAccountId = null;
        String Name = null;
        String Domain = null;
        String Token0 = null;
        String Token1 = null;
        String ProfitingAccountId = null;
        String TokenToSwap = null;
        String PoolId = null;
        String SwapFrom = null;
        String VoteFor = null;
        String StakingAccountId = null;
        String PType = null;
        String Supply = null;
        String ShareRatio = null;
        String Token0Amount = null;
        String Token1Amount = null;
        String Amount = null;
        String AmountToSwap = null;
        String AmountToGet = null;
        String Slippage = null;
        String MaxVoter = null;
        String DaysToStake = null;
        String CompoundMode = null;
        String Begin = null;
        String End = null;

        public String getApi() {
            return Api;
        }
        public String getNetwork() {
            return Network;
        }
        public String getActionPurpose() {
            return ActionPurpose;
        }
        public String getAccName() {
            return AccountName;
        }
        public String getAccountId() {
            return AccountId;
        }
        public String getDestinationAccountId() {
            return DestinationAccountId;
        }
        public String getName() {
            return Name;
        }
        public String getDomain() {
            return Domain;
        }
        public String getToken0 () {
            return Token0;
        }
        public String getToken1() {
            return Token1;
        }
        public String getProfitingAccountId() {
            return ProfitingAccountId;
        }
        public String getTokenToSwap() {
            return TokenToSwap;
        }
        public String getPoolId() {
            return PoolId;
        }
        public String getSwapFrom() {
            return SwapFrom;
        }
        public String getVoteFor() {
            return VoteFor;
        }
        public String getStakingAccountId() {
            return StakingAccountId;
        }

        public String getPType() {
            return PType;
        }
        public String getSupply() {
            return Supply;
        }
        public String getShareRatio() {
            return ShareRatio;
        }
        public String getToken0Amount() {
            return Token0Amount;
        }
        public String getToken1Amount() {
            return Token1Amount;
        }
        public String getAmount() {
            return Amount;
        }
        public String getAmountToSwap() {
            return AmountToSwap;
        }
        public String getAmountToGet() {
            return AmountToGet;
        }
        public String getSlippage() {
            return Slippage;
        }
        public String getMaxVoter() {
            return MaxVoter;
        }
        public String getDaysToStake() {
            return DaysToStake;
        }
        public String getCompoundMode() {
            return CompoundMode;
        }
        public String getBegin() {
            return Begin;
        }
        public String getEnd() {
            return End;
        }

        ProfitingType convertPType() {
            switch(PType) {
                case "Oracle":
                    return ORACLE;
                case "Merchant":
                    return MERCHANT;
                case "Yield":
                    return YIELD;
                default:
                    return NODE;
            }
        }
        double valueOfSupply() {
            if(Supply == null){
                return -1f;
            }
            return Double.parseDouble(Supply);
        }
        double valueOfShareRatio() {
            if(ShareRatio == null){
                return -1f;
            }
            return Double.parseDouble(ShareRatio);
        }
        double valueOfToken0Amount() {
            if(Token0Amount == null){
                return -1f;
            }
            return Double.parseDouble(Token0Amount);
        }
        double valueOfToken1Amount() {
            if(Token1Amount == null){
                return -1f;
            }
            return Double.parseDouble(Token1Amount);
        }
        double valueOfAmount() {
            if(Amount == null){
                return -1f;
            }
            return Double.parseDouble(Amount);
        }
        double valueOfAmountToSwap() {
            if(AmountToSwap == null){
                return -1f;
            }
            return Double.parseDouble(AmountToSwap);
        }
        double valueOfAmountToGet() {
            if(AmountToGet == null){
                return -1f;
            }
            return Double.parseDouble(AmountToGet);
        }
        double valueOfSlippage() {
            if(Slippage == null){
                return -1f;
            }
            return Double.parseDouble(Slippage);
        }
        int valueOfMaxVoter() {
            if(MaxVoter == null){
                return -1;
            }
            return Integer.parseInt(MaxVoter);
        }
        int valueOfDaysToStake() {
            if(DaysToStake == null){
                return -1;
            }
            return Integer.parseInt(DaysToStake);
        }
        boolean valueOfCompoundMode() {
            if(CompoundMode == null){
                return false;
            }
            return Boolean.getBoolean(CompoundMode);
        }
        long valueBegin() {
            if(Begin == null){
                return 0;
            }
            return Long.valueOf(Begin, 10);
        }
        long valueEnd() {
            if(End == null){
                return 0;
            }
            return Long.valueOf(End, 10);
        }

        public Action() {

        }

        public Action(String actionString) {
            try {
                JSONObject obj = new JSONObject(actionString);
                if(!obj.isNull("api")) {
                    Api = obj.getString("api");
                }
                if(!obj.isNull("network")) {
                    Network = obj.getString("network");
                }
                if(!obj.isNull("action_purpose")) {
                    ActionPurpose = obj.getString("action_purpose");
                }
                if(!obj.isNull("account_name")) {
                    AccountName = obj.getString("account_name");
                }
                if(!obj.isNull("account_id")) {
                    AccountId = obj.getString("account_id");
                }
                if(!obj.isNull("destination_account_id")) {
                    DestinationAccountId = obj.getString("destination_account_id");
                }
                if(!obj.isNull("name")) {
                    Name = obj.getString("name");
                }
                if(!obj.isNull("domain")) {
                    Domain = obj.getString("domain");
                }
                if(!obj.isNull("token0")) {
                    Token0 = obj.getString("token0");
                }
                if(!obj.isNull("token1")) {
                    Token1 = obj.getString("token1");
                }
                if(!obj.isNull("profiting_account_id")) {
                    ProfitingAccountId = obj.getString("profiting_account_id");
                }
                if(!obj.isNull("token_to_swap")) {
                    TokenToSwap = obj.getString("token_to_swap");
                }
                if(!obj.isNull("pool_id")) {
                    PoolId = obj.getString("pool_id");
                }
                if(!obj.isNull("swap_from")) {
                    SwapFrom = obj.getString("swap_from");
                }
                if(!obj.isNull("vote_for")) {
                    VoteFor = obj.getString("vote_for");
                }
                if(!obj.isNull("staking_account_id")) {
                    StakingAccountId = obj.getString("staking_account_id");
                }
                if(!obj.isNull("p_type")) {
                    PType = obj.getString("p_type");
                }
                if(!obj.isNull("supply")) {
                    Supply = obj.getString("supply");
                }
                if(!obj.isNull("share_ratio")) {
                    ShareRatio = obj.getString("share_ratio");
                }
                if(!obj.isNull("token0_amount")) {
                    Token0Amount = obj.getString("token0_amount");
                }
                if(!obj.isNull("token1_amount")) {
                    Token1Amount = obj.getString("token1_amount");
                }
                if(!obj.isNull("amount")) {
                    Amount = obj.getString("amount");
                }
                if(!obj.isNull("amount_to_swap")) {
                    AmountToSwap = obj.getString("amount_to_swap");
                }
                if(!obj.isNull("amount_to_get")) {
                    AmountToGet = obj.getString("amount_to_get");
                }
                if(!obj.isNull("slippage")) {
                    Slippage = obj.getString("slippage");
                }
                if(!obj.isNull("max_voter")) {
                    MaxVoter = obj.getString("max_voter");
                }
                if(!obj.isNull("days_to_stake")) {
                    DaysToStake = obj.getString("days_to_stake");
                }
                if(!obj.isNull("compound_mode")) {
                    CompoundMode = obj.getString("compound_mode");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public String toString() {
            JSONObject obj = new JSONObject();
            try {
                if (Api != null) {
                    obj.put("api", Api);
                }
                if (Network != null) {
                    obj.put("network", Network);
                }
                if (ActionPurpose != null) {
                    obj.put("action_purpose", ActionPurpose);
                }
                if (AccountName != null) {
                    obj.put("account_name", AccountName);
                }
                if (AccountId != null) {
                    obj.put("account_id", AccountId);
                }
                if (DestinationAccountId != null) {
                    obj.put("destination_account_id", DestinationAccountId);
                }
                if (Name != null) {
                    obj.put("name", Name);
                }
                if (Domain != null) {
                    obj.put("domain", Domain);
                }
                if (Token0 != null) {
                    obj.put("token0", Token0);
                }
                if (Token1 != null) {
                    obj.put("token1", Token1);
                }
                if (ProfitingAccountId != null) {
                    obj.put("profiting_account_id", ProfitingAccountId);
                }
                if (TokenToSwap != null) {
                    obj.put("token_to_swap", TokenToSwap);
                }
                if (PoolId != null) {
                    obj.put("pool_id", PoolId);
                }
                if (SwapFrom != null) {
                    obj.put("swap_from", SwapFrom);
                }
                if (VoteFor != null) {
                    obj.put("vote_for", VoteFor);
                }
                if (StakingAccountId != null) {
                    obj.put("staking_account_id", StakingAccountId);
                }
                if (PType != null) {
                    obj.put("p_type", PType);
                }
                if (Supply != null) {
                    obj.put("supply", Supply);
                }
                if (ShareRatio != null) {
                    obj.put("share_ratio", ShareRatio);
                }
                if (Token0Amount != null) {
                    obj.put("token0_amount", Token0Amount);
                }
                if (Token1Amount != null) {
                    obj.put("token1_amount", Token1Amount);
                }
                if (Amount != null) {
                    obj.put("amount", Amount);
                }
                if (AmountToSwap != null) {
                    obj.put("amount_to_swap", AmountToSwap);
                }
                if (AmountToGet != null) {
                    obj.put("amount_to_get", AmountToGet);
                }
                if (Slippage != null) {
                    obj.put("slippage", Slippage);
                }
                if (MaxVoter != null) {
                    obj.put("max_voter", MaxVoter);
                }
                if (DaysToStake != null) {
                    obj.put("days_to_stake", DaysToStake);
                }
                if (CompoundMode != null) {
                    obj.put("compound_mode", CompoundMode);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return obj.toString();
        }

        public Action actionHistory(String actionPurpose, String network, String accountName, String accountId) {
            actionHistory(accountName, accountId);
            ActionPurpose = actionPurpose;
            Network = network;
            return this;
        }
        public Action actionHistory(String accountName, String accountId) {
            actionHistory(accountId);
            AccountName = accountName;
            return this;
        }
        public Action actionHistory(String accountId) {
            if(Api == null) {
                Api = "History";
                ActionPurpose = "";
                AccountName = null;
            } else {
                Api = Api + "/" + "History";
            }
            AccountId = accountId;
            AccountName = Global.getSelectedAccountName();
            Network = Global.getCurrentNetwork();
            return this;
        }
        public Action actionReceive(String actionPurpose, String accountId) {
            ActionPurpose = actionPurpose;
            return actionReceive(accountId);
        }
        public Action actionReceive(String accountId) {
            if(Api == null) {
                Api = "Receive";
            } else {
                Api = Api + "/" + "Receive";
            }
            AccountId = accountId;
            AccountName = Global.getSelectedAccountName();
            Network = Global.getCurrentNetwork();
            return this;
        }
        public Action actionSend(String accountId, double amount, String token, String destinationId) {
            if(Api == null) {
                Api = "Send";
            } else {
                Api = Api + "/" + "Send";
            }
            AccountId = accountId;
            AccountName = Global.getSelectedAccountName();
            Network = Global.getCurrentNetwork();
            Amount = String.format(Locale.US,"%.8f", amount);
            Token0 = token;
            DestinationAccountId = destinationId;
            return this;
        }
        public Action actionBalance(String actionPurpose, String accountId) {
            ActionPurpose = actionPurpose;
            return actionBalance(accountId);
        }
        public Action actionBalance(String accountId) {
            if(Api == null) {
                Api = "Balance";
            } else {
                Api = Api + "/" + "Balance";
            }
            AccountId = accountId;
            AccountName = Global.getSelectedAccountName();
            Network = Global.getCurrentNetwork();
            return this;
        }
        public Action actionToken(String accountId, String name, String domain, double supply) {
            if(Api == null) {
                Api = "Token";
            } else {
                Api = Api + "/" + "Token";
            }
            AccountId = accountId;
            AccountName = Global.getSelectedAccountName();
            Network = Global.getCurrentNetwork();
            Name = name;
            Domain = domain;
            Supply = String.format(Locale.US,"%.8f", supply);
            return this;
        }
        public Action actionCreateProfitingAccount(String accountId, String name, ProfitingType pType, double shareRatio, int maxVoter) {
            if(Api == null) {
                Api = "CreateProfitingAccount";
            } else {
                Api = Api + "/" + "CreateProfitingAccount";
            }
            AccountId = accountId;
            AccountName = Global.getSelectedAccountName();
            Network = Global.getCurrentNetwork();
            Name = name;
            switch(pType) {
                case ORACLE: PType = "Oracle"; break;
                case MERCHANT: PType = "Merchant"; break;
                case YIELD: PType = "Yield"; break;
                default: PType = "Node"; break;
            }
            ShareRatio = String.format(Locale.US,"%.8f", shareRatio);
            MaxVoter = String.format(Locale.US,"%d", maxVoter);
            return this;
        }
        public Action actionCreateStakingAccount(String accountId, String name, String voteFor, int daysToStake, boolean compoundMode) {
            if(Api == null) {
                Api = "CreateStakingAccount";
            } else {
                Api = Api + "/" + "CreateStakingAccount";
            }
            AccountId = accountId;
            AccountName = Global.getSelectedAccountName();
            Network = Global.getCurrentNetwork();
            Name = name;
            VoteFor = voteFor;
            DaysToStake = String.format(Locale.US,"%d", daysToStake);
            CompoundMode = String.format(Locale.US,"%b", compoundMode);
            return this;
        }
        public Action actionAddStaking(String accountId, String stakingAccountId, double amount) {
            if(Api == null) {
                Api = "AddStaking";
            } else {
                Api = Api + "/" + "AddStaking";
            }
            AccountId = accountId;
            AccountName = Global.getSelectedAccountName();
            Network = Global.getCurrentNetwork();
            StakingAccountId = stakingAccountId;
            Amount = String.format(Locale.US,"%.8f", amount);
            return this;
        }
        public Action actionCreateDividends(String accountId, String profitingAccountId) {
            if(Api == null) {
                Api = "CreateDividends";
            } else {
                Api = Api + "/" + "CreateDividends";
            }
            AccountId = accountId;
            AccountName = Global.getSelectedAccountName();
            Network = Global.getCurrentNetwork();
            ProfitingAccountId = profitingAccountId;
            return this;
        }
        public Action actionGetBrokerAccounts(String accountId) {
            if(Api == null) {
                Api = "GetBrokerAccounts";
            } else {
                Api = Api + "/" + "GetBrokerAccounts";
            }
            AccountId = accountId;
            AccountName = Global.getSelectedAccountName();
            Network = Global.getCurrentNetwork();
            return this;
        }
        public Action actionPool(String actionPurpose, String token0, String token1) {
            ActionPurpose = actionPurpose;
            return actionPool(token0, token1);
        }
        public Action actionPool(String token0, String token1) {
            if(Api == null) {
                Api = "Pool";
            } else {
                Api = Api + "/" + "Pool";
            }
            AccountName = Global.getSelectedAccountName();
            Network = Global.getCurrentNetwork();
            Token0 = token0;
            Token1 = token1;
            return this;
        }
        public Action actionCreatePool(String accountId, String token0, String token1) {
            if(Api == null) {
                Api = "CreatePool";
            } else {
                Api = Api + "/" + "CreatePool";
            }
            AccountId = accountId;
            AccountName = Global.getSelectedAccountName();
            Network = Global.getCurrentNetwork();
            Token0 = token0;
            Token1 = token1;
            return this;
        }
        public Action actionAddLiquidity(String accountId, String token0, double token0Amount, String token1, double token1Amount) {
            if(Api == null) {
                Api = "AddLiquidity";
            } else {
                Api = Api + "/" + "AddLiquidity";
            }
            AccountId = accountId;
            AccountName = Global.getSelectedAccountName();
            Network = Global.getCurrentNetwork();
            Token0 = token0;
            Token0Amount = String.format(Locale.US,"%.8f", token0Amount);
            Token1 = token1;
            Token1Amount = String.format(Locale.US,"%.8f", token1Amount);
            return this;
        }
        public Action actionPoolCalculate(String poolId, String swapFrom, double amount, double slippage) {
            if(Api == null) {
                Api = "PoolCalculate";
            } else {
                Api = Api + "/" + "PoolCalculate";
            }
            AccountName = Global.getSelectedAccountName();
            Network = Global.getCurrentNetwork();
            PoolId = poolId;
            SwapFrom = swapFrom;
            Amount = String.format(Locale.US,"%.8f", amount);
            Slippage = String.format(Locale.US,"%.8f", slippage);
            return this;
        }
        public Action actionSwap(String accountId, String token0, String token1, String tokenToSwap, double amountToSwap, double amountToGet) {
            if(Api == null) {
                Api = "Swap";
            } else {
                Api = Api + "/" + "Swap";
            }
            AccountId = accountId;
            AccountName = Global.getSelectedAccountName();
            Network = Global.getCurrentNetwork();
            Token0 = token0;
            Token1 = token1;
            TokenToSwap = tokenToSwap;
            AmountToSwap = String.format(Locale.US,"%.8f", amountToSwap);
            AmountToGet = String.format(Locale.US,"%.8f", amountToGet);
            return this;
        }
        public Action actionRemoveLiquidity(String accountId, String token0, String token1) {
            if(Api == null) {
                Api = "RemoveLiquidity";
            } else {
                Api = Api + "/" + "RemoveLiquidity";
            }
            AccountId = accountId;
            AccountName = Global.getSelectedAccountName();
            Network = Global.getCurrentNetwork();
            Token0 = token0;
            Token1 = token1;
            return this;
        }
        public Action actionFindAllProfitingAccounts(long begin, long end) {
            if(Api == null) {
                Api = "FindAllProfitingAccounts";
            } else {
                Api = Api + "/" + "FindAllProfitingAccounts";
            }
            AccountName = Global.getSelectedAccountName();
            Network = Global.getCurrentNetwork();
            Begin = String.valueOf(begin);
            End = String.valueOf(end);
            return this;
        }
    }

    public String ReceiveResult = null;
    public String SendResult = null;

    public ApiRpc() {
        ParentInstance = getInstance();
    }

    private boolean actWithPwd(Action action, String password) {
        switch(action.Api) {
            case "Receive":
                new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL, this, password).execute(action.toString(), action.getApi(), action.getAccountId());
                break;
            case "Send":
                new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL, this, password).execute(action.toString(), action.getApi(), action.getAccountId(),
                        action.getAmount(), action.getDestinationAccountId(), action.getToken0());
                break;
            case "Token":
                new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL, this, password).execute(action.toString(), action.getApi(), action.getAccountId(),
                        action.getName(), action.getDomain(), action.getSupply());
                break;
            case "CreateProfitingAccount":

                new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL, this, password).execute(action.toString(), action.getApi(), action.getAccountId(),
                        action.getName(), action.getPType(), action.getShareRatio(),
                        action.getMaxVoter());
                break;
            case "CreateStakingAccount":
                new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL, this, password).execute(action.toString(), action.getApi(), action.getAccountId(),
                        action.getName(), action.getVoteFor(),
                        action.getDaysToStake(), action.getCompoundMode());
                break;
            case "AddStaking":
                new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL, this, password).execute(action.toString(), action.getApi(), action.getAccountId(),
                        action.getStakingAccountId(), action.getAmount());
                break;
            case "CreateDividends":
                new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL, this, password).execute(action.toString(), action.getApi(), action.getAccountId(),
                        action.getProfitingAccountId());
                break;
            case "CreatePool":
            case "RemoveLiquidity":
                new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL, this, password).execute(action.toString(), action.getApi(), action.getAccountId(),
                        action.getToken0(), action.getToken1());
                break;
            case "AddLiquidity":
                new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL, this, password).execute(action.toString(), action.getApi(), action.getAccountId(),
                        action.getToken0(), action.getToken0Amount(),
                        action.getToken1(), action.getToken1Amount());
                break;
            case "Swap":
                new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL, this, password).execute(action.toString(), action.getApi(), action.getAccountId(),
                        action.getToken0(), action.getToken1(), action.getTokenToSwap(),
                        action.getAmountToSwap(), action.getAmountToGet());
                break;
            default:
                break;
        }
        return true;
    }
    private static MainActivity ParentInstance = null;

    public boolean act(Action action) {
        System.out.println("1-Acting on: " + action.getApi());
        if(action.getApi().split("/").length == 0) {
            return false;
        }
        System.out.println("2-Acting on: " + action.getApi());
        switch(action.getApi().split("/")[0]) {
            case "History":
                new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL, this).execute(action.toString(), "History", action.getAccountId(),
                "0", String.valueOf(System.currentTimeMillis()), "0");
                return true;
            case "Balance":
                new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL, this).execute(action.toString(), "Balance", action.getAccountId());
                return true;
            case "Pool":
                new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL, this).execute(action.toString(), "Pool", action.getAccountId(),
                        action.getToken0(), action.getToken1());
                return true;
            case "PoolCalculate":
                new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL, this).execute(action.toString(), "PoolCalculate", action.getAccountId(),
                        action.getPoolId(), action.getSwapFrom(), action.getAmount(), action.getSlippage());
                return true;
            case "GetBrokerAccounts":
                new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL, this).execute(action.toString(), "GetBrokerAccounts", action.getAccountId());
                return true;
            case "FindAllProfitingAccounts": // Not implemented
                new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL, this).execute(action.toString(), action.getApi(), action.getAccountId(),
                        action.Begin, action.End);
                return true;
            default:
                break;
        }
        if(Global.getWalletPassword().length() < Global.MinCharAllowedOnPassword) {
            final EditText passEditText = new EditText(ParentInstance);
            // Put EditText in password mode
            passEditText.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            passEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            View v = new View(ParentInstance);
            UiHelpers.showKeyboard(v.getRootView(), passEditText);
            AlertDialog dialog = new AlertDialog.Builder(ParentInstance)
                    .setTitle(R.string.Unlock_wallet)
                    .setMessage(R.string.Enter_wallet_password)
                    .setView(passEditText)
                    .setPositiveButton(R.string.Accept, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String password = String.valueOf(passEditText.getText());
                            actWithPwd(action, password);
                            if(action.getActionPurpose() == null || !action.getActionPurpose().equals("Receive")) {
                                UiDialog.showDialogStatus(R.string.send_sending);
                            }
                        }
                    })
                    .setNegativeButton(R.string.Cancel, null)
                    .create();
            dialog.show();
        } else {
            actWithPwd(action, Global.getWalletPassword());
            if(action.getActionPurpose() == null || !action.getActionPurpose().equals("Receive")) {
                UiDialog.showDialogStatus(R.string.send_sending);
            }
        }

        /*Pair<Integer, String> h = Global.getWalletHistory(Concatenate.getHistoryFileName());
        if(h != null) {
            String newH = ApiRpcActionsHistory.HistoryEntry.toJson( h.second, action.getAccountId(), action.getDestinationAccountId(),
                    new Pair<String, Double>(action.getToken0(), Double.parseDouble(action.getAmount())));
            ApiRpcActionsHistory.store(action, newH);
        }*/
        return true;
    }

    void getBalanceAfterAction() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                getInstance().runOnUiThread(new Runnable() {
                    public void run() {
                        new ApiRpc().act(new ApiRpc.Action().actionBalance(Global.getSelectedAccountId()));
                    }
                });
            }
        }, 2000);
    }

    /*void signAfterAction() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                getInstance().runOnUiThread(new Runnable() {
                    public void run() {
                        new ApiRpc().act(new ApiRpc.Action().actionHistory(Global.str_api_rpc_purpose_history_disk_storage,
                                Global.getCurrentNetworkName(), Global.getSelectedAccountName(), Global.getSelectedAccountId()));
                    }
                });
            }
        }, 3000);
    }*/

    public static void runActionHistory() {
        new ApiRpc().act(new ApiRpc.Action().actionHistory(Global.str_api_rpc_purpose_history_disk_storage,
                Global.getCurrentNetworkName(), Global.getSelectedAccountName(), Global.getSelectedAccountId()));
    }

    @Override
    public void onRpcTaskDone(String[] output) {
        ApiRpc.Action ac = new ApiRpc.Action(output[1]);
        getInstance().runOnUiThread(new Runnable() {
            public void run() {
                Activity activity = getInstance();
                if(activity == null) {
                    return;
                }
                if (output[0].equals("History")) {
                    ApiRpcActionsHistory.store(ac, output[2]);
                } else if (output[0].equals("Send")) {
                    EditText recipientAddressEditText = (EditText) activity.findViewById(R.id.sendTokenRecipientAddressValue);
                    EditText tokenAmountEditText = (EditText) activity.findViewById(R.id.sendTokenAmountValue);
                    if(recipientAddressEditText != null && tokenAmountEditText != null) {
                        try {
                            JSONObject objCmd = new JSONObject(output[1]);
                            if (!objCmd.isNull("account_id") && !objCmd.isNull("destination_account_id")) {
                                // Filter to show the message for the currently selected account and selected destination.
                                if (objCmd.getString("account_id").equals(Global.getSelectedAccountId()) &&
                                        objCmd.getString("destination_account_id").equals(recipientAddressEditText.getText().toString())) {
                                    JSONObject objRsp = new JSONObject(output[2]);
                                    if (!objRsp.isNull("txHash")) { // If thHash is present, the transaction is successfully send.
                                        Spinner tokenSpinner = (Spinner) activity.findViewById(R.id.sendTokenSelectSpinner);
                                        SpinnerAdapter adapter = tokenSpinner.getAdapter();
                                        String tokenToSend = adapter.getItem(tokenSpinner.getSelectedItemPosition()).toString();
                                        getBalanceAfterAction();
                                        try {
                                            UiDialog.showDialogStatus(R.string.send_successful, String.format(Locale.US, "%s: %f %s\n%s: %s-%d (%s)\n%s: %s",
                                                    activity.getString(R.string.Send1), Double.parseDouble(tokenAmountEditText.getText().toString()), tokenToSend,
                                                    activity.getString(R.string.From), activity.getString(R.string.Wallet), Global.getSelectedAccountNr() + 1, UiHelpers.getShortAccountId(Global.getSelectedAccountId(), 4),
                                                    activity.getString(R.string.To), UiHelpers.getShortAccountId(recipientAddressEditText.getText().toString(), 7)),
                                                    ApiRpc.class.getDeclaredMethod("runActionHistory")
                                            );
                                        } catch (NoSuchMethodException e) {
                                            e.printStackTrace();
                                        }
                                        recipientAddressEditText.setText("");
                                        tokenAmountEditText.setText("");
                                        new FragmentManagerUser().goToAccount();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                } else if(output[0].equals("Receive")) {
                    getBalanceAfterAction();
                } else if(output[0].equals("Balance")) {
                    try {
                        JSONObject objCmd = new JSONObject(output[2]);
                        if(!objCmd.isNull("unreceived")) {
                            if (objCmd.getBoolean("unreceived")) {
                                if(ac.getActionPurpose() != null) {
                                    if (ac.getActionPurpose().equals("Receive")) {
                                        new ApiRpc().act(new ApiRpc.Action().actionReceive("Receive", Global.getSelectedAccountId()));
                                    }
                                } else if(Global.getWalletPassword().length() >= Global.MinCharAllowedOnPassword) {
                                    new ApiRpc().act(new ApiRpc.Action().actionReceive("Receive", Global.getSelectedAccountId()));
                                }
                            } else {
                                Pair<Integer, List<ApiRpcActionsHistory.HistoryEntry>> h = Global.getWalletHistory(Concatenate.getHistoryFileName());
                                if(h != null && h.second != null) {
                                    if(objCmd.getLong("height") != h.second.get(h.second.size() - 1).getHeight()) {
                                        new ApiRpc().act(new ApiRpc.Action().actionHistory(Global.str_api_rpc_purpose_history_disk_storage,
                                                Global.getCurrentNetworkName(), Global.getSelectedAccountName(), Global.getSelectedAccountId()));
                                    }
                                } else {
                                    new ApiRpc().act(new ApiRpc.Action().actionHistory(Global.str_api_rpc_purpose_history_disk_storage,
                                            Global.getCurrentNetworkName(), Global.getSelectedAccountName(), Global.getSelectedAccountId()));
                                }
                            }
                        }
                    } catch (JSONException | NullPointerException | ArrayIndexOutOfBoundsException e) {
                        //Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                } else if(output[0].equals("Pool")) {
                    if (output[2].equals("error")) {
                        UiUpdates.setPoolData(null);
                     } else if(ac.getActionPurpose() != null) {
                        if(ac.getActionPurpose().equals("PoolCalculate")) {
                            try {
                                UiUpdates.setPoolData(new UiUpdates.PoolData(output[2]));
                                try {
                                    try {
                                        EditText swapFromValueEditText = (EditText) activity.findViewById(R.id.swapFromValueEditText);
                                        Spinner tokenFromSpinner = (Spinner) activity.findViewById(R.id.swapFromValueSpinner);
                                        String from = GlobalLyra.symbolToDomain(tokenFromSpinner.getSelectedItem().toString());
                                        new ApiRpc().act(new ApiRpc.Action().actionPoolCalculate(UiUpdates.getPoolData().getPoolId(), GlobalLyra.symbolToDomain(from),
                                                Double.parseDouble(swapFromValueEditText.getText().toString()), 0.01f));
                                    } catch (NullPointerException ignored) { }
                                } catch (NumberFormatException e) {
                                    FragmentSwap.setProgressBarVisibility(activity, View.GONE);
                                    e.printStackTrace();
                                }
                                if(Global.getDebugEnabled())
                                    Toast.makeText(activity, "\"Pool\" fetch done", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        } else if(ac.getActionPurpose().equals("LyrPriceInUSD")) {
                            try {
                                UiUpdates.setPoolData(new UiUpdates.PoolData(output[2]));
                                JSONObject obj = new JSONObject(output[2]);
                                JSONObject objBalance = obj.getJSONObject("balance");
                                if( UiUpdates.getPoolData().token0Is("tether/USDT") && UiUpdates.getPoolData().token0Is("LYR")) {
                                    Global.setTokenPrice(new Pair<>("LYR", "tether/USDT"), objBalance.getDouble("tether/USDT") / objBalance.getDouble("LYR"));
                                }
                            } catch (JSONException | NullPointerException e) {
                                e.printStackTrace();
                                Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                } else if (output[0].equals("PoolCalculate")) {
                    try {
                        UiUpdates.setPoolCalculateData(new UiUpdates.PoolCalculateData(output[2]));
                        if (Global.getDebugEnabled())
                            Toast.makeText(activity, "\"PoolCalculate\" fetch done", Toast.LENGTH_SHORT).show();
                    } catch (JSONException | NullPointerException e) {
                        Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                } else if (output[0].equals("Swap")) {
                    if(output[2].equals("error"))
                        UiDialog.showDialogStatus(R.string.str_an_error_occurred, R.string.str_please_contact_lyra_period_inc);
                    else {
                        FragmentSwap.clearAccountFromTo(activity);
                        try {
                            UiDialog.showDialogStatus(R.string.swap_swap_complete,
                                    ApiRpc.class.getDeclaredMethod("runActionHistory"));
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                    }
                    //getBalanceAfterAction();
                } else if (output[0].equals("AddLiquidity")) {
                    if(output[2].equals("error"))
                        UiDialog.showDialogStatus(R.string.str_an_error_occurred);
                    else {
                        FragmentSwap.clearAccountFromTo(activity);
                        try {
                            UiDialog.showDialogStatus(R.string.swap_add_liquidity_complete,
                                    ApiRpc.class.getDeclaredMethod("runActionHistory"));
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (output[0].equals("GetBrokerAccounts")) {
                    Global.setBrokerAccounts(Concatenate.getHistoryFileName(ac), new ApiRpcActionsBrokerAccounts().fromJson(output[2]));
                } else if(output[0].equals("FindAllProfitingAccounts")) {
                    System.out.println(output[2]);
                }
                ReceiveResult = output[0] + "^" + output[1] + "^" + output[2];
                System.out.println("Transaction end, result is:" + ReceiveResult);
            }
        });
    }
    @Override
    public void onRpcNewEvent(String[] output) {
        /*
        if(output[0].equals("Receive")) {
            SendResult = output[0] + "^" + output[1] + "^" + output[2];
        } else if(output[0].equals("Send")) {
            SendResult = output[0] + "^" + output[1] + "^" + output[2];
        } else if(output[0].equals("History")) {
            SendResult = output[0] + "^" + output[1] + "^" + output[2];
        }
        SendResult = output[0] + "^" + output[1] + "^" + output[2];
        System.out.println(SendResult);
         */
        /*Snackbar.make(getInstance().findViewById(R.id.nav_host_fragment_content_main), output[2], Snackbar.LENGTH_LONG)
                .setAction("", null).show();*/
    }
}
