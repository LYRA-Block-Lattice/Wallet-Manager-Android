package com.lyrawallet;

import android.util.Pair;

import com.lyrawallet.Accounts.Accounts;
import com.lyrawallet.Ui.FragmentAccount.FragmentAccount;
import com.lyrawallet.Ui.FragmentReceive.FragmentReceive;
import com.lyrawallet.Ui.FragmentSend.FragmentSend;
import com.lyrawallet.Ui.FragmentPreferences.FragmentPreferencesRoot;

import java.util.List;

public class Global {
// Global enumerations
public enum visiblePage {
    OPEN_WALLET,
    IMPORT_WALLET,
    NEW_WALLET,
    NEW_ACCOUNT,
    RECOVER_ACCOUNT,
    WALLET,
    MY_ACCOUNT,
    MY_ACCOUNT_RECEIVE,
    MY_ACCOUNT_SEND,
    SETTINGS,
}

    // Global constants
    public final static String[] networkName = { "TESTNET", "MAINNET", "DEVNET" };
    public final static String[] languageName = {"EN", "RO"};
    public final static String DefaultWalletExtension = "lyr";
    public final static int RpcConnectionTimeout = 100; // in 10mS steps
    public final static int MaxWalletsBackupAllowed = 100;
    public final static int MinCharAllowedOnPassword = 8;
    public final static int MinCharAllowedOnWalletName = 2;
    public final static int MaxRpcConnectRetry = 5;

    public final static String str_api_rpc_purpose_history_disk_storage = "disk_storage";
// Global variables
    private static visiblePage VisiblePage;

    private static String CurrentNetwork = networkName[0];
    private static String CurrentLanguage = languageName[0];

    private static String AccountsContainer = null;
    private static List<Pair<String, String>> WalletAccNameAndIdList = null;
    private static String WalletName = null;
    private static String ReceiveWalletPassword = null;
    private static String SelectedAccountName = "";
    private static int SelectedAccountNr = -1;
    private static String WalletPath = "";
    private static int InactivityTimeForClose = -1;

    private static FragmentAccount Dashboard = null;
    private static FragmentReceive MyAccountReceive = null;
    private static FragmentSend MyAccountSend = null;
    private static FragmentPreferencesRoot Settings = null;


    final static String[] NodeAddressDevNet = new String[]{""};
    final static String[] NodeAddressTestNet = new String[]{
            "wss://173.212.228.110:4504/api/v1/socket",
            "wss://161.97.166.188:4504/api/v1/socket",
            "wss://seed.testnet.lyra.live:443/api/v1/socket",
            "wss://seed2.testnet.lyra.live:443/api/v1/socket",
            "wss://seed3.testnet.lyra.live:443/api/v1/socket",
            "wss://seed3.testnet.lyra.live:443/api/v1/socket"
    };
    final static String[] NodeAddressMainNet = new String[]{
            "wss://173.212.228.110:5504/api/v1/socket",
            "wss://161.97.166.188:5504/api/v1/socket",
            "wss://seed1.mainnet.lyra.live:443/api/v1/socket",
            "wss://seed2.mainnet.lyra.live:443/api/v1/socket",
            "wss://seed3.mainnet.lyra.live:443/api/v1/socket",
            "wss://seed3.mainnet.lyra.live:443/api/v1/socket"
    };

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

    public static void setDashboard(FragmentAccount dash) {
        Dashboard = dash;
    }
    public static FragmentAccount getDashboard() {
        return Dashboard;
    }

    public static void setMyAccountReceive(FragmentReceive myAccReceive) {
        MyAccountReceive = myAccReceive;
    }
    public static FragmentReceive getMyAccountReceive() {
        return MyAccountReceive;
    }

    public static void setMyAccountSend(FragmentSend myAccSend) {
        MyAccountSend = myAccSend;
    }
    public static FragmentSend getMyAccountSend() {
        return MyAccountSend;
    }

    public static void setSettings(FragmentPreferencesRoot set) {
        Settings = set;
    }
    public static FragmentPreferencesRoot getSettings() {
        return Settings;
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
}
