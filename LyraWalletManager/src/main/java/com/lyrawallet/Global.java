package com.lyrawallet;

import android.util.Pair;

import com.lyrawallet.Ui.Dashboard.FragmentDashboard;
import com.lyrawallet.Ui.MyAccountReceive.FragmentMyAccountReceive;
import com.lyrawallet.Ui.MyAccountSend.FragmentMyAccountSend;
import com.lyrawallet.Ui.Preferences.FragmentPreferencesRoot;

import java.util.List;

public class Global {
    public enum network {
        TESTNET,
        MAINNET,
        DEVNET
    }

    public enum language {
        ENG,
        ROM
    }

    public static network currentNetwork = network.TESTNET;
    public static language currentLanguage = language.ENG;

    public static String accountsContainer = null;
    public static List<Pair<String, String>> walletAccNameAndId = null;
    public static String walletName = null;
    public static String selectedAccountName = "";
    public static int selectedAccountNr = -1;
    public static int rpcConnectionTimeout = 200; // in 10mS steps
    public static String defaultWalletExtension = "lyr";
    public static int maxWalletsBackupAllowed = 100;
    public static String walletPath = "";
    public static int minCharAllowedOnPassword = 8;
    public static int minCharAllowedOnWalletName = 2;

    public static FragmentDashboard dashboard = null;
    public static FragmentMyAccountReceive myAccountReceive = null;
    public static FragmentMyAccountSend myAccountSend = null;
    public static FragmentPreferencesRoot settings = null;

    private static final String[] nodeAddressDevNet = new String[]{""};
    private static final String[] nodeAddressTestNet = new String[]{
            "wss://161.97.166.188:4504/api/v1/socket",
            "wss://173.212.228.110:4504/api/v1/socket",
            "wss://seed.testnet.lyra.live:443/api/v1/socket",
            "wss://seed2.testnet.lyra.live:443/api/v1/socket",
            "wss://seed3.testnet.lyra.live:443/api/v1/socket",
            "wss://seed3.testnet.lyra.live:443/api/v1/socket"
    };
    private static final String[] nodeAddressMainNet = new String[]{
            "wss://161.97.166.188:5504/api/v1/socket",
            "wss://173.212.228.110:5504/api/v1/socket",
            "wss://seed1.mainnet.lyra.live:443/api/v1/socket",
            "wss://seed2.mainnet.lyra.live:443/api/v1/socket",
            "wss://seed3.mainnet.lyra.live:443/api/v1/socket",
            "wss://seed3.mainnet.lyra.live:443/api/v1/socket"
    };

    public static String getWalletPath(String containerName) {
        return walletPath + containerName + "." + defaultWalletExtension;
    }

    public static String getWalletPath() {
        return walletPath + walletName + "." + defaultWalletExtension;
    }

    public static String getNodeAddress() {
        if(currentNetwork == network.DEVNET) {
            int nodeNr = (int) (Math.random() * nodeAddressDevNet.length - 1);
            return nodeAddressDevNet[nodeNr];
        } else if(currentNetwork == network.TESTNET) {
            int nodeNr = (int) (Math.random() * nodeAddressTestNet.length - 1);
            return nodeAddressTestNet[nodeNr];
        }else if(currentNetwork == network.MAINNET) {
            int nodeNr = (int) (Math.random() * nodeAddressMainNet.length - 1);
            return nodeAddressMainNet[nodeNr];
        }
        return "";
    }

    public static String getCurrentNetworkName() {
        switch(currentNetwork) {
            case DEVNET:
                return "DEVNET";
            case MAINNET:
                return "MAINNET";
            default:
                return "TESTNET";
        }
    }
}
