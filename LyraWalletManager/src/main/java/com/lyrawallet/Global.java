package com.lyrawallet;

import android.util.Pair;
import android.widget.Spinner;

import androidx.navigation.NavController;

import com.lyrawallet.Ui.Dashboard.Dashboard;
import com.lyrawallet.Ui.MyAccountReceive.MyAccountReceive;
import com.lyrawallet.Ui.MyAccountSend.MyAccountSend;
import com.lyrawallet.Ui.Preferences.PreferencesRoot;

import java.util.List;

public class Global {
    public static String pK = "mjnQ4Zv73zkmRB6KKhm3SCv2kMjs45gWuHaW8HrFenb16FehG";
    public static String accountsContainer = null;
    public static String walletName = null;
    public static String walletPassword = null;
    public static String selectedAccountName = "";
    public static int selectedAccountNr = -1;
    public static int connectionTimeout = 500; // in 10mS steps
    public static String defaultWalletExtension = "lyr";
    public static int maxWalletsBackupAllowed = 100;
    public static String walletPath = "";
    public static int minCharAllowedOnPassword = 8;
    public static int minCharAllowedOnWalletName = 2;

    List<Pair<String, String>> accKeysList = null;

    public static NavController navController = null;
    public static Dashboard dashboard = null;
    public static MyAccountReceive myAccountReceive = null;
    public static MyAccountSend myAccountSend = null;
    public static PreferencesRoot settings = null;

    Spinner accountsSpinner = null;
}
