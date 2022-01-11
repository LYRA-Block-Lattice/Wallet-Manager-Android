package com.lyrawallet.Api;

import static com.lyrawallet.Api.ApiRpc.ProfitingType.MERCHANT;
import static com.lyrawallet.Api.ApiRpc.ProfitingType.NODE;
import static com.lyrawallet.Api.ApiRpc.ProfitingType.ORACLE;
import static com.lyrawallet.Api.ApiRpc.ProfitingType.YIELD;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;

import com.lyrawallet.Api.ApiRpcActions.ApiRpcActionsHistory;
import com.lyrawallet.Api.Network.NetworkRpc;
import com.lyrawallet.Global;
import com.lyrawallet.MainActivity;
import com.lyrawallet.R;
import com.lyrawallet.Ui.UiHelpers;

import org.json.JSONException;
import org.json.JSONObject;

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

        String getApi() {
            return Api;
        }
        String getNetwork() {
            return Network;
        }
        public String getActionPurpose() {
            return ActionPurpose;
        }
        String getAccName() {
            return AccountName;
        }
        String getAccountId() {
            return AccountId;
        }
        String getDestinationAccountId() {
            return DestinationAccountId;
        }
        String getName() {
            return Name;
        }
        String getDomain() {
            return Domain;
        }
        String getToken0 () {
            return Token0;
        }
        String getToken1() {
            return Token1;
        }
        String getProfitingAccountId() {
            return ProfitingAccountId;
        }
        String getTokenToSwap() {
            return TokenToSwap;
        }
        String getPoolId() {
            return PoolId;
        }
        String getSwapFrom() {
            return SwapFrom;
        }
        String getVoteFor() {
            return VoteFor;
        }
        String getStakingAccountId() {
            return StakingAccountId;
        }

        String getPType() {
            return PType;
        }
        String getSupply() {
            return Supply;
        }
        String getShareRatio() {
            return ShareRatio;
        }
        String getToken0Amount() {
            return Token0Amount;
        }
        String getToken1Amount() {
            return Token1Amount;
        }
        String getAmount() {
            return Amount;
        }
        String getAmountToSwap() {
            return AmountToSwap;
        }
        String getAmountToGet() {
            return AmountToGet;
        }
        String getSlippage() {
            return Slippage;
        }
        String getMaxVoter() {
            return MaxVoter;
        }
        String getDaysToStake() {
            return DaysToStake;
        }
        String getCompoundMode() {
            return CompoundMode;
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
            return this;
        }
        public Action actionReceive(String accountId) {
            if(Api == null) {
                Api = "Receive";
            } else {
                Api = Api + "/" + "Receive";
            }
            AccountId = accountId;
            return this;
        }
        public Action actionSend(String accountId, double amount, String token, String destinationId) {
            if(Api == null) {
                Api = "Send";
            } else {
                Api = Api + "/" + "Send";
            }
            AccountId = accountId;
            Amount = String.format(Locale.US,"%.8f", amount);
            Token0 = token;
            DestinationAccountId = destinationId;
            return this;
        }
        public Action actionBalance(String accountId) {
            if(Api == null) {
                Api = "Balance";
            } else {
                Api = Api + "/" + "Balance";
            }
            AccountId = accountId;
            return this;
        }
        public Action actionToken(String accountId, String name, String domain, double supply) {
            if(Api == null) {
                Api = "Token";
            } else {
                Api = Api + "/" + "Token";
            }
            AccountId = accountId;
            Name = name;
            Domain = domain;
            Supply = String.format(Locale.US,"%.8f", supply);
            return this;
        }
        public Action actionCteateProfitingAccount(String accountId, String name, ProfitingType pType, double shareRatio, int maxVoter) {
            if(Api == null) {
                Api = "CreateProfitingAccount";
            } else {
                Api = Api + "/" + "CreateProfitingAccount";
            }
            AccountId = accountId;
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
            return this;
        }
        public Action actionPool(String token0, String token1) {
            if(Api == null) {
                Api = "Pool";
            } else {
                Api = Api + "/" + "Pool";
            }
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
            Token0 = token0;
            Token1 = token1;
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
                new NetworkRpc(this, password).execute(action.toString(), action.getApi(), action.getAccountId());
                break;
            case "Send":
                new NetworkRpc(this, password).execute(action.toString(), action.getApi(), action.getAccountId(),
                        action.getAmount(), action.getDestinationAccountId(), action.getToken0());
                break;
            case "Token":
                new NetworkRpc(this, password).execute(action.toString(), action.getApi(), action.getAccountId(),
                        action.getName(), action.getDomain(), action.getSupply());
                break;
            case "CreateProfitingAccount":

                new NetworkRpc(this, password).execute(action.toString(), action.getApi(), action.getAccountId(),
                        action.getName(), action.getPType(), action.getShareRatio(),
                        action.getMaxVoter());
                break;
            case "CreateStakingAccount":
                new NetworkRpc(this, password).execute(action.toString(), action.getApi(), action.getAccountId(),
                        action.getName(), action.getVoteFor(),
                        action.getDaysToStake(), action.getCompoundMode());
                break;
            case "AddStaking":
                new NetworkRpc(this, password).execute(action.toString(), action.getApi(), action.getAccountId(),
                        action.getStakingAccountId(), action.getAmount());
                break;
            case "CreateDividends":
                new NetworkRpc(this, password).execute(action.toString(), action.getApi(), action.getAccountId(),
                        action.getProfitingAccountId());
                break;
            case "CreatePool":
            case "RemoveLiquidity":
                new NetworkRpc(this, password).execute(action.toString(), action.getApi(), action.getAccountId(),
                        action.getToken0(), action.getToken1());
                break;
            case "AddLiquidity":
                new NetworkRpc(this, password).execute(action.toString(), action.getApi(), action.getAccountId(),
                        action.getToken0(), action.getToken0Amount(),
                        action.getToken1(), action.getToken1Amount());
                break;
            case "Swap":
                new NetworkRpc(this, password).execute(action.toString(), action.getApi(), action.getAccountId(),
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
        if(action.getApi().split("/").length == 0) {
            return false;
        }
        switch(action.getApi().split("/")[0]) {
            case "History":
                new NetworkRpc(this).execute(action.toString(), "History", action.getAccountId(),
                        "0", String.valueOf(System.currentTimeMillis()), "0");
                return true;
            case "Balance":
                new NetworkRpc(this).execute(action.toString(), "Balance", action.getAccountId());
                return true;
            case "Pool":
                new NetworkRpc(this).execute(action.toString(), "Pool", action.getAccountId(),
                        action.getToken0(), action.getToken1());
                return true;
            case "PoolCalculate":
                new NetworkRpc(this).execute(action.toString(), "PoolCalculate", action.getAccountId(),
                        action.getPoolId(), action.getSwapFrom(), action.getAmount(), action.getSlippage());
                return true;
            default:
                break;
        }
        final EditText passEditText = new EditText(ParentInstance);
        // Put EditText in password mode
        passEditText.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        passEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        View v = new View(ParentInstance);
        UiHelpers.showKeyboard(v.getRootView(), passEditText);
        AlertDialog dialog = new AlertDialog.Builder(ParentInstance)
                .setTitle(R.string.str_dialog_title)
                .setMessage(R.string.str_dialog_message)
                .setView(passEditText)
                .setPositiveButton(R.string.str_dialog_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password = String.valueOf(passEditText.getText());
                        actWithPwd(action, password);
                    }
                })
                .setNegativeButton(R.string.str_dialog_cancel, null)
                .create();
        dialog.show();
        return true;
    }

    static public String getHistoryFileName(Action ac) {
        return Global.getWalletName() + "_" + ac.getAccName() + "_" + ac.getNetwork();
    }

    @Override
    public void onRpcTaskDone(String[] output) {
        ApiRpc.Action ac = new ApiRpc.Action(output[1]);
        if(output[0].equals("History")) {
            ApiRpcActionsHistory.store(ac, output[2]);
        } else if(output[0].equals("Receive")) {
            ReceiveResult = output[0] + "^" + output[1] + "^" + output[2];
        } else if(output[0].equals("Send")) {
            ReceiveResult = output[0] + "^" + output[1] + "^" + output[2];
        }
        ReceiveResult = output[0] + "^" + output[1] + "^" + output[2];
        System.out.println(ReceiveResult);
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
    }
}
