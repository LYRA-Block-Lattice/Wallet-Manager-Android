package com.lyrawallet;

import android.util.Pair;

import com.lyrawallet.Ui.FragmentDashboard.FragmentDashboard;
import com.lyrawallet.Ui.FragmentMyAccountReceive.FragmentMyAccountReceive;
import com.lyrawallet.Ui.FragmentMyAccountSend.FragmentMyAccountSend;
import com.lyrawallet.Ui.FragmentPreferences.FragmentPreferencesRoot;

import java.util.List;

public class Global {
// Global enumerations
    public enum network { TESTNET, MAINNET, DEVNET }
    public enum language { ENG, ROM }
// Global constants
    final static String DefaultWalletExtension = "lyr";
    final static int RpcConnectionTimeout = 200; // in 10mS steps
    final static int MaxWalletsBackupAllowed = 100;
    final static int MinCharAllowedOnPassword = 8;
    final static int MinCharAllowedOnWalletName = 2;
// Global variables
    private static network CurrentNetwork = network.TESTNET;
    private static language CurrentLanguage = language.ENG;

    private static String AccountsContainer = null;
    private static List<Pair<String, String>> WalletAccNameAndId = null;
    private static String WalletName = null;
    private static String ReceiveWalletPassword = null;
    private static String SelectedAccountName = "";
    private static int SelectedAccountNr = -1;
    private static String WalletPath = "";
    private static int InactivityTimeForClose = -1;

    private static FragmentDashboard Dashboard = null;
    private static FragmentMyAccountReceive MyAccountReceive = null;
    private static FragmentMyAccountSend MyAccountSend = null;
    private static FragmentPreferencesRoot Settings = null;


    final static String[] NodeAddressDevNet = new String[]{""};
    final static String[] NodeAddressTestNet = new String[]{
            "wss://161.97.166.188:4504/api/v1/socket",
            "wss://173.212.228.110:4504/api/v1/socket",
            "wss://seed.testnet.lyra.live:443/api/v1/socket",
            "wss://seed2.testnet.lyra.live:443/api/v1/socket",
            "wss://seed3.testnet.lyra.live:443/api/v1/socket",
            "wss://seed3.testnet.lyra.live:443/api/v1/socket"
    };
    final static String[] NodeAddressMainNet = new String[]{
            "wss://161.97.166.188:5504/api/v1/socket",
            "wss://173.212.228.110:5504/api/v1/socket",
            "wss://seed1.mainnet.lyra.live:443/api/v1/socket",
            "wss://seed2.mainnet.lyra.live:443/api/v1/socket",
            "wss://seed3.mainnet.lyra.live:443/api/v1/socket",
            "wss://seed3.mainnet.lyra.live:443/api/v1/socket"
    };

    public static void setCurrentNetwork(network net) {
        CurrentNetwork = net;
    }
    public static network getCurrentNetwork() {
        return CurrentNetwork;
    }

    public static void setCurrentLanguage(language lang) {
        CurrentLanguage = lang;
    }
    public static language getCurrentLanguage() {
        return CurrentLanguage;
    }

    public static void setAccountsContainer(String accContainer) {
        AccountsContainer = accContainer;
    }
    public static String getAccountsContainer() {
        return AccountsContainer;
    }

    public static void setWalletAccNameAndId(List<Pair<String, String>> wallAccNameId) {
        WalletAccNameAndId = wallAccNameId;
    }
    public static List<Pair<String, String>> getWalletAccNameAndId() {
        return WalletAccNameAndId;
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

    public static void setDashboard(FragmentDashboard dash) {
        Dashboard = dash;
    }
    public static FragmentDashboard getDashboard() {
        return Dashboard;
    }

    public static void setMyAccountReceive(FragmentMyAccountReceive myAccReceive) {
        MyAccountReceive = myAccReceive;
    }
    public static FragmentMyAccountReceive getMyAccountReceive() {
        return MyAccountReceive;
    }

    public static void setMyAccountSend(FragmentMyAccountSend myAccSend) {
        MyAccountSend = myAccSend;
    }
    public static FragmentMyAccountSend getMyAccountSend() {
        return MyAccountSend;
    }

    public static void setSettings(FragmentPreferencesRoot set) {
        Settings = set;
    }
    public static FragmentPreferencesRoot getSettings() {
        return Settings;
    }

    public static String getNodeAddress() {
        if(getCurrentNetwork() == network.DEVNET) {
            int nodeNr = (int) (Math.random() * NodeAddressDevNet.length - 1);
            return NodeAddressDevNet[nodeNr];
        } else if(getCurrentNetwork() == network.TESTNET) {
            int nodeNr = (int) (Math.random() * NodeAddressTestNet.length - 1);
            return NodeAddressTestNet[nodeNr];
        }else if(getCurrentNetwork() == network.MAINNET) {
            int nodeNr = (int) (Math.random() * NodeAddressMainNet.length - 1);
            return NodeAddressMainNet[nodeNr];
        }
        return "";
    }

    public static String getCurrentNetworkName() {
        switch(getCurrentNetwork()) {
            case DEVNET:
                return "DEVNET";
            case MAINNET:
                return "MAINNET";
            default:
                return "TESTNET";
        }
    }

    public static void setInactivityTimeForClose(int inactivity) {
        InactivityTimeForClose = inactivity;
    }
    public static int getInactivityTimeForClose() {
        return InactivityTimeForClose;
    }
}
