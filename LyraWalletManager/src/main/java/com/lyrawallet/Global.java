package com.lyrawallet;

import static com.lyrawallet.R.mipmap.*;

import android.util.Pair;

import com.lyrawallet.Accounts.Accounts;
import com.lyrawallet.Api.ApiRpcActions.ApiRpcActionsHistory;
import com.lyrawallet.Ui.FragmentAccount.FragmentAccount;

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
        DIALOG_RECEIVE
    }

    // Global constants
    public final static String[] networkName = {"TESTNET", "MAINNET", "DEVNET"};
    public final static String[] languageName = {"EN", "RO"};
    public final static String DefaultWalletExtension = "lyr";
    public final static int RpcConnectionTimeout = 100; // in 10mS steps
    public final static int MaxWalletsBackupAllowed = 100;
    public final static int MinCharAllowedOnPassword = 8;
    public final static int MinCharAllowedOnWalletName = 2;
    public final static int MaxRpcConnectRetry = 5;
    // Preference key names
    public final static String keyCustomLyraPriceInUsd = "lyra_price_in_usd";

    public final static String str_api_rpc_purpose_history_disk_storage = "disk_storage";
    public final static String str_api_rpc_purpose_send_manual = "user_send";
    // Global variables
    private static visiblePage VisiblePage;

    private static String CurrentNetwork = networkName[0];
    private static String CurrentLanguage = languageName[0];

    private static String AccountsContainer = null;
    private static List<Pair<String, String>> WalletAccNameAndIdList = null;
    private static List<Pair<String, Pair<Integer, String>>> WalletHistory = null;
    private static List<Pair<String, List<FragmentAccount.AccountHistoryEntry>>> FragmentAccountHistory = null;
    private static String WalletName = null;
    private static String ReceiveWalletPassword = null;
    private static String SelectedAccountName = "";
    private static int SelectedAccountNr = -1;
    private static List<Pair<String, Boolean>>  UnreceivedBalance = null;
    private static String WalletPath = "";
    private static int InactivityTimeForClose = -1;
    private static boolean PasswordSaveAllowed = false;
    private static String WalletPassword = "";

    private static List<Pair<Double, Pair<String, String>>> TokenPrice = null;

    private static int DeviceOrientation = 0;


    final static String[] NodeAddressDevNet = new String[]{""};
    final static String[] NodeAddressTestNet = new String[]{
            "wss://173.212.228.110:4504/api/v1/socket",
            "wss://81.196.64.78:4504/api/v1/socket",
            "wss://161.97.166.188:4504/api/v1/socket",
            "wss://seed.testnet.lyra.live:443/api/v1/socket",
            "wss://seed2.testnet.lyra.live:443/api/v1/socket",
            "wss://seed3.testnet.lyra.live:443/api/v1/socket",
            "wss://seed3.testnet.lyra.live:443/api/v1/socket"
    };
    final static String[] NodeAddressMainNet = new String[]{
            "wss://173.212.228.110:5504/api/v1/socket",
            "wss://81.196.64.78:5504/api/v1/socket",
            "wss://161.97.166.188:5504/api/v1/socket",
            "wss://seed1.mainnet.lyra.live:443/api/v1/socket",
            "wss://seed2.mainnet.lyra.live:443/api/v1/socket",
            "wss://seed3.mainnet.lyra.live:443/api/v1/socket",
            "wss://seed3.mainnet.lyra.live:443/api/v1/socket"
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

    public static void setUnreceivedBalance(String accountName, boolean unreceived) {
        if(UnreceivedBalance != null) {
            for (int i = 0; i < UnreceivedBalance.size(); i++) {
                Pair<String, Boolean> acc = UnreceivedBalance.get(i);
                if (acc.first.equals(accountName)) {
                    UnreceivedBalance.remove(i);
                    UnreceivedBalance.add(new Pair<>(accountName, unreceived));
                    return;
                }
            }
        } else {
            UnreceivedBalance = new ArrayList<>();
        }
        UnreceivedBalance.add(new Pair<>(accountName, unreceived));
    }
    public static boolean getUnreceivedBalance(String accountName) {
        if(UnreceivedBalance != null) {
            for (int i = 0; i < UnreceivedBalance.size(); i++) {
                Pair<String, Boolean> acc = UnreceivedBalance.get(i);
                if (acc.first.equals(accountName)) {
                    return acc.second;
                }
            }
        }
        return false;
    }

    public static void setWalletHistory(String accountName, String history) {
        if(WalletHistory != null) {
            for (int i = 0; i < WalletHistory.size(); i++) {
                Pair<String, Pair<Integer, String>> acc = WalletHistory.get(i);
                if (acc.first.equals(accountName)) {
                    int cnt = acc.second.first;
                    WalletHistory.remove(i);
                    WalletHistory.add(new Pair<>(accountName, new Pair<>(cnt, history)));
                    setFragmentAccountHistory(accountName, ApiRpcActionsHistory.getData(new Pair<>(cnt, history)));
                    return;
                }
            }
        } else {
            WalletHistory = new ArrayList<>();
        }
        WalletHistory.add(new Pair<>(accountName, new Pair<>(0, history)));
        setFragmentAccountHistory(accountName, ApiRpcActionsHistory.getData(new Pair<>(0, history)));
    }

    public static Pair<Integer, String> getWalletHistory(String accountName) {
        if(WalletHistory != null) {
            for (int i = 0; i < WalletHistory.size(); i++) {
                Pair<String, Pair<Integer, String>> acc = WalletHistory.get(i);
                if (acc.first.equals(accountName)) {
                    return acc.second;
                }
            }
        }
        return null;
    }

    public static void setFragmentAccountHistory(String accountName, List<FragmentAccount.AccountHistoryEntry> history) {
        if(FragmentAccountHistory != null) {
            for (int i = 0; i < FragmentAccountHistory.size(); i++) {
                Pair<String, List<FragmentAccount.AccountHistoryEntry>> acc = FragmentAccountHistory.get(i);
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

    public static List<FragmentAccount.AccountHistoryEntry> getFragmentAccountHistory(String accountName) {
        if(FragmentAccountHistory != null) {
            for (int i = 0; i < FragmentAccountHistory.size(); i++) {
                Pair<String, List<FragmentAccount.AccountHistoryEntry>> acc = FragmentAccountHistory.get(i);
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

    public static int getMaxRpcConnectRetry() {
        return MaxRpcConnectRetry;
    }

    public static String getNodeAddress() {
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
        PasswordSaveAllowed = allowed;
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
            TokenPrice.add(new Pair<Double, Pair<String, String>>(price, pair));
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
}
