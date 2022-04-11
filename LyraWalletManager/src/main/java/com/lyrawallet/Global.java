package com.lyrawallet;

import android.util.Pair;

import com.lyrawallet.Accounts.Accounts;
import com.lyrawallet.Api.ApiRpcActions.ApiRpcActionsBrokerAccounts;
import com.lyrawallet.Api.ApiRpcActions.ApiRpcActionsHistory;
import com.lyrawallet.Ui.FragmentAccountHistory.FragmentAccountHistory;
import com.lyrawallet.Util.Concatenate;

import java.util.ArrayList;
import java.util.List;

public class Global {
    // Global enumerations
    public enum visiblePage {
        STAKING,
        SWAP,
        ACCOUNT,
        DEX,
        MORE,
        OPEN_WALLET,
        IMPORT_WALLET,
        NEW_WALLET,
        NEW_ACCOUNT,
        RECOVER_ACCOUNT,
        RECEIVE,
        SEND,
        SETTINGS,
        ACCOUNT_HISTORY,
        DIALOG_RECEIVE,
        DIALOG_TRANSACTION_DETAIL,
        DIALOG_STAKING_DETAIL,
        DIALOG_CREATE_STAKING
    }

    // Global constants
    public final static String[] networkName = {"TESTNET", "MAINNET", "DEVNET"};
    public final static String[] dexNetworkDns = {"https://testnet.lyra.live", "https://mainnet.lyra.live", "https://devnet.lyra.live"};
    public static final String[] dexNetworkApi = {"https://dextestnet.lyra.live/api/Dex", "https://dex.lyra.live/api/Dex", "https://dexdevnet.lyra.live/api/Dex"};
    public final static String[] languageName = {"EN", "RO"};
    public final static String DefaultWalletExtension = "lyr";
    public final static int RpcConnectionTimeout = 100; // in 10mS steps
    public final static int MaxWalletsBackupAllowed = 100;
    public final static int MinCharAllowedOnPassword = 8;
    public final static int MinCharAllowedOnWalletName = 2;
    public final static int MaxRpcConnectRetry = 5;
    public final static int MaxRpcConnectToNodeRetry = 2;
    // Preference key names
    public final static String keyCustomLyraPriceInUsd = "lyra_price_in_usd";

    public final static String str_api_rpc_purpose_history_disk_storage = "disk_storage";
    public final static String str_api_rpc_purpose_send_manual = "user_send";
    // Global variables
    private static visiblePage VisiblePage;
    private static boolean DebugEnabled = false;
    private static boolean RandomizeIpEnabled = false;

    private static String CurrentNetwork = networkName[0];
    private static String CurrentLanguage = languageName[0];

    private static String AccountsContainer = null;
    private static List<Pair<String, String>> WalletAccNameAndIdList = null;
    private static List<Pair<String, Pair<Integer, List<ApiRpcActionsHistory.HistoryEntry>>>> AccountHistory = null;
    private static List<Pair<String, List<FragmentAccountHistory.AccountHistoryEntry>>> FragmentAccountHistory = null;
    private static String WalletName = null;
    private static String ReceiveWalletPassword = null;
    private static String SelectedAccountName = "";
    private static int SelectedAccountNr = -1;
    private static String WalletPath = "";
    private static int InactivityTimeForClose = -1;
    private static boolean PasswordSaveAllowed = true;
    private static String WalletPassword = "";

    private static List<Pair<Double, Pair<String, String>>> TokenPrice = null;
    private static List<Pair<String, ApiRpcActionsBrokerAccounts>> BrokerAccounts = null;

    private static int DeviceOrientation = 0;


    static int SelectedNode = 0;
    final static String[] NodeAddressDevNet = new String[]{""};
    final static String[] NodeAddressTestNet = new String[]{
            "161.97.166.188:4504",
            "173.212.228.110:4504",
            "81.196.64.78:4504",
            "seed.testnet.lyra.live:443",
            "seed2.testnet.lyra.live:443",
            "seed3.testnet.lyra.live:443",
            "seed3.testnet.lyra.live:443"
    };
    final static String[] NodeAddressMainNet = new String[]{
            "173.212.228.110:5504",
            "81.196.64.78:5504",
            "161.97.166.188:5504",
            "seed1.mainnet.lyra.live:443",
            "seed2.mainnet.lyra.live:443",
            "seed3.mainnet.lyra.live:443",
            "seed3.mainnet.lyra.live:443"
    };

    public static int getDeviceOrientation() {
        return DeviceOrientation;
    }
    public static void setDeviceOrientation(int orientation) {
        DeviceOrientation = orientation;
    }

    public static void setVisiblePage(visiblePage v) {
        VisiblePage = v;
    }
    public static visiblePage getVisiblePage() {
        return VisiblePage;
    }

    public static void setDebugEnabled( boolean enabled) {
        DebugEnabled = enabled;
    }
    public static boolean getDebugEnabled() {
        return DebugEnabled;
    }

    public static void setRandomizeIpEnabled( boolean enabled) {
        RandomizeIpEnabled = enabled;
    }
    public static boolean getRandomizeIpEnabled() {
        return RandomizeIpEnabled;
    }

    public static void setCurrentNetwork(String net) {
        CurrentNetwork = net;
    }
    public static String getCurrentNetwork() {
        return CurrentNetwork;
    }

    public static void setCurrentLanguage(String lang) {
        CurrentLanguage = lang;
    }
    public static String getCurrentLanguage() {
        return CurrentLanguage;
    }

    public static void setAccountsContainer(String accContainer) {
        AccountsContainer = accContainer;
    }
    public static String getAccountsContainer() {
        return AccountsContainer;
    }

    public static void setWalletAccNameAndIdList(List<Pair<String, String>> wallAccNameId) {
        WalletAccNameAndIdList = wallAccNameId;
    }
    public static List<Pair<String, String>> getWalletAccNameAndIdList() {
        return WalletAccNameAndIdList;
    }

    public static void setWalletHistory(String accountName, String history) {
        List<ApiRpcActionsHistory.HistoryEntry> h = ApiRpcActionsHistory.loadHistory(history);
        if(AccountHistory != null) {
            for (int i = 0; i < AccountHistory.size(); i++) {
                Pair<String, Pair<Integer, List<ApiRpcActionsHistory.HistoryEntry>>> acc = AccountHistory.get(i);
                if (acc.first.equals(accountName)) {
                    int cnt = acc.second.first;
                    AccountHistory.remove(i);
                    AccountHistory.add(new Pair<>(accountName, new Pair<>(cnt, h)));
                    return;
                }
            }
        } else {
            AccountHistory = new ArrayList<>();
        }
        AccountHistory.add(new Pair<>(accountName, new Pair<>(0, ApiRpcActionsHistory.loadHistory(history))));
    }

    public static Pair<Integer, List<ApiRpcActionsHistory.HistoryEntry>> getWalletHistory(String accountName) {
        if(AccountHistory != null) {
            for (int i = 0; i < AccountHistory.size(); i++) {
                Pair<String, Pair<Integer, List<ApiRpcActionsHistory.HistoryEntry>>> acc = AccountHistory.get(i);
                if (acc.first.equals(accountName)) {
                    return acc.second;
                }
            }
        }
        return null;
    }

    public static void setFragmentAccountHistory(String accountName, List<FragmentAccountHistory.AccountHistoryEntry> history) {
        if(FragmentAccountHistory != null) {
            for (int i = 0; i < FragmentAccountHistory.size(); i++) {
                Pair<String, List<FragmentAccountHistory.AccountHistoryEntry>> acc = FragmentAccountHistory.get(i);
                if (acc.first.equals(accountName)) {
                    FragmentAccountHistory.remove(i);
                    FragmentAccountHistory.add(new Pair<>(accountName, history));
                    return;
                }
            }
        } else {
            FragmentAccountHistory = new ArrayList<>();
        }
        FragmentAccountHistory.add(new Pair<>(accountName, history));
    }

    public static double getAvailableToken(String ticker) {
        if(AccountHistory != null) {
            for (int i = 0; i < AccountHistory.size(); i++) {
                Pair<String, Pair<Integer, List<ApiRpcActionsHistory.HistoryEntry>>> acc = AccountHistory.get(i);
                if (acc.first.equals(Concatenate.getHistoryFileName())) {
                    List<ApiRpcActionsHistory.HistoryEntry> account = acc.second.second;
                    if(account.size() > 0) {
                        ApiRpcActionsHistory.HistoryEntry entry = account.get(account.size() - 1);
                        List<Pair<String, Double>> balances = entry.getBalances();
                        for (Pair<String, Double> b: balances) {
                            if(b.first.equals(ticker)) {
                                return b.second;
                            }
                        }
                    } else
                        return 0;
                }
            }
        }
        return 0;
    }

    public static List<FragmentAccountHistory.AccountHistoryEntry> getFragmentAccountHistory(String accountName) {
        if(FragmentAccountHistory != null) {
            for (int i = 0; i < FragmentAccountHistory.size(); i++) {
                Pair<String, List<FragmentAccountHistory.AccountHistoryEntry>> acc = FragmentAccountHistory.get(i);
                if (acc.first.equals(accountName)) {
                    return acc.second;
                }
            }
        }
        return null;
    }

    public static void setWalletName(String wallName) {
        WalletName = wallName;
    }
    public static String getWalletName() {
        return WalletName;
    }

    public static void setReceiveWalletPassword(String recWallPass) {
        ReceiveWalletPassword = recWallPass;
    }
    public static String getReceiveWalletPassword() {
        return ReceiveWalletPassword;
    }

    public static void setSelectedAccountName(String selAccName) {
        SelectedAccountName = selAccName;
    }
    public static String getSelectedAccountName() {
        return SelectedAccountName;
    }

    public static void setSelectedAccountNr(int selAccNr) {
        SelectedAccountNr = selAccNr;
    }
    public static int getSelectedAccountNr() {
        return SelectedAccountNr;
    }

    public static int getRpcConnectionTimeout() {
        return RpcConnectionTimeout;
    }

    public static String getDefaultWalletExtension() {
        return DefaultWalletExtension;
    }

    public static int getMaxWalletsBackupAllowed() {
        return MaxWalletsBackupAllowed;
    }

    public static void setWalletPath(String wallPath) {
        WalletPath = wallPath;
    }
    public static String getWalletPath(String containerName) {
        return WalletPath + containerName + "." + DefaultWalletExtension;
    }

    public static String getWalletPath() {
        return WalletPath + WalletName + "." + DefaultWalletExtension;
    }

    public static int getMinCharAllowedOnPassword() {
        return MinCharAllowedOnPassword;
    }

    public static int getMinCharAllowedOnWalletName() {
        return MinCharAllowedOnWalletName;
    }

    public static int getMaxRpcConnectToNodeRetry() {
        return MaxRpcConnectToNodeRetry;
    }

    public static int getMaxRpcConnectRetry() {
        return MaxRpcConnectRetry;
    }

    public static void incrementNodeNumber() {
        SelectedNode++;
        if (SelectedNode >= getNodeAddressTable().length) {
            SelectedNode = 0;
        }
        System.out.println("Push to next node IP: " + getNodeAddress());
    }

    public static String getNodeAddress() {
        if (getRandomizeIpEnabled()) {
            int nodeNr = 0;
            switch (getCurrentNetwork()) {
                case "DEVNET":
                    nodeNr = (int) (Math.random() * NodeAddressDevNet.length - 1);
                    return NodeAddressDevNet[nodeNr];
                case "MAINNET":
                    nodeNr = (int) (Math.random() * NodeAddressMainNet.length - 1);
                    return NodeAddressMainNet[nodeNr];
                default:
                    nodeNr = (int) (Math.random() * NodeAddressTestNet.length - 1);
                    return NodeAddressTestNet[nodeNr];
            }
        } else {
            if (SelectedNode >= getNodeAddressTable().length) {
                SelectedNode = 0;
            }
            switch (getCurrentNetwork()) {
                case "DEVNET":
                    return NodeAddressDevNet[SelectedNode];
                case "MAINNET":
                    return NodeAddressMainNet[SelectedNode];
                default:
                    return NodeAddressTestNet[SelectedNode];
            }
        }
    }

    public static String[] getNodeAddressTable() {
        switch (getCurrentNetwork()) {
            case "DEVNET":
                return NodeAddressDevNet;
            case "MAINNET":
                return NodeAddressMainNet;
            default:
                return NodeAddressTestNet;
        }
    }

    public static String getDexNetwork() {
        switch (getCurrentNetwork()) {
            case "DEVNET":
                return dexNetworkDns[2];
            case "MAINNET":
                return dexNetworkDns[1];
            default:
                return dexNetworkDns[0];
        }
    }

    public static String getDexNetworkApi() {
        switch (getCurrentNetwork()) {
            case "DEVNET":
                return dexNetworkApi[2];
            case "MAINNET":
                return dexNetworkApi[1];
            default:
                return dexNetworkApi[0];
        }
    }

    public static String getCurrentNetworkName() {
        return getCurrentNetwork();
    }

    public static String getSelectedAccountId() {
        return Accounts.getAccount();
    }

    public static void setInactivityTimeForClose(int inactivity) {
        InactivityTimeForClose = inactivity;
    }
    public static int getInactivityTimeForClose() {
        return InactivityTimeForClose;
    }

    public static void setPasswordSaveAllowed(boolean allowed) {
        //PasswordSaveAllowed = allowed;
    }
    public static boolean getPasswordSaveAllowed() {
        return PasswordSaveAllowed;
    }

    public static void setWalletPassword( String pass) {
        WalletPassword = pass;
    }
    public static String getWalletPassword() {
        return WalletPassword;
    }

    public static void setTokenPrice(Pair<String, String> pair, double price) {
        if(TokenPrice == null) {
            TokenPrice = new ArrayList<>();
        } else {
            for (int i = 0; i < TokenPrice.size(); i++) {
                Pair<Double, Pair<String, String>> p = TokenPrice.get(i);
                if((pair.first.equals(p.second.first) && pair.second.equals(p.second.second)) ||
                        (pair.first.equals(p.second.second) && pair.second.equals(p.second.first))) {
                    TokenPrice.remove(i);
                    TokenPrice.add(new Pair<Double, Pair<String, String>>(price, pair));
                    return;
                }
            }
        }
        TokenPrice.add(new Pair<Double, Pair<String, String>>(price, pair));
    }
    public static double getTokenPrice(Pair<String, String> pair) {
        for (int i = 0; i < TokenPrice.size(); i++) {
            Pair<Double, Pair<String, String>> p = TokenPrice.get(i);
            if(pair.first.equals(p.second.first) && pair.second.equals(p.second.second)) {
                return p.first;
            } else if (pair.first.equals(p.second.second) && pair.second.equals(p.second.first)) {
                return 1f / p.first;
            }
        }
        return 0f;
    }

    public static void setBrokerAccounts(String accountName, ApiRpcActionsBrokerAccounts brokerAccounts) {
        if(BrokerAccounts != null) {
            for (int i = 0; i < BrokerAccounts.size(); i++) {
                Pair<String, ApiRpcActionsBrokerAccounts> acc = BrokerAccounts.get(i);
                if (acc.first.equals(accountName)) {
                    BrokerAccounts.remove(i);
                    BrokerAccounts.add(new Pair<>(accountName, brokerAccounts));
                    return;
                }
            }
        } else {
            BrokerAccounts = new ArrayList<>();
        }
        BrokerAccounts.add(new Pair<>(accountName, brokerAccounts));
    }

    public static ApiRpcActionsBrokerAccounts getBrokerAccounts(String accountName) {
        if(BrokerAccounts != null) {
            for (int i = 0; i < BrokerAccounts.size(); i++) {
                Pair<String, ApiRpcActionsBrokerAccounts> acc = BrokerAccounts.get(i);
                if (acc.first.equals(accountName)) {
                    return acc.second;
                }
            }
        }
        return null;
    }
}
