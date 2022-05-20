package com.lyrawallet.Ui;

import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.lyrawallet.Global;
import com.lyrawallet.MainActivity;
import com.lyrawallet.R;
import com.lyrawallet.Ui.FragmentAccount.FragmentAccount;
import com.lyrawallet.Ui.FragmentAccountHistory.FragmentAccountHistory;
import com.lyrawallet.Ui.FragmentAccountHistory.FragmentTransactionDetailsDialog;
import com.lyrawallet.Ui.FragmentDex.FragmentDex;
import com.lyrawallet.Ui.FragmentMore.FragmentMore;
import com.lyrawallet.Ui.FragmentPreferences.FragmentPreferencesRoot;
import com.lyrawallet.Ui.FragmentReceive.FragmentReceive;
import com.lyrawallet.Ui.FragmentReceive.FragmentReceiveDialog;
import com.lyrawallet.Ui.FragmentSend.FragmentSend;
import com.lyrawallet.Ui.FragmentStaking.FragmentStaking;
import com.lyrawallet.Ui.FragmentStaking.StakingProfitingListInfo.FragmentProfitingListingInfo;
import com.lyrawallet.Ui.FragmentSwap.FragmentSwap;
import com.lyrawallet.Ui.FragmentWalletManagement.FragmentImportWallet;
import com.lyrawallet.Ui.FragmentWalletManagement.FragmentNewAccount;
import com.lyrawallet.Ui.FragmentWalletManagement.FragmentNewWallet;
import com.lyrawallet.Ui.FragmentWalletManagement.FragmentOpenWallet;
import com.lyrawallet.Ui.FragmentWalletManagement.FragmentRecoverAccount;

import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class FragmentManagerUser extends MainActivity {
    /******************* Navigation, separate them from button events, for re-usage ***************/
    public static void setVisiblePage(Global.visiblePage p) {
        setVisiblePage(p, null);
    }
    public static void setVisiblePage(Global.visiblePage p, String[] params) {
        Global.setVisiblePage(p);
        PushToBackStack = true;
        switch(p) {
            case STAKING:
                new FragmentManagerUser().goToStaking();
                break;
            case SWAP:
                new FragmentManagerUser().goToSwap();
                break;
            case ACCOUNT:
                new FragmentManagerUser().goToAccount();
                break;
            case ACCOUNT_HISTORY:
                new FragmentManagerUser().goToAccountHistory();
                break;
            case DEX:
                new FragmentManagerUser().goToDex();
                break;
            case MORE:
                new FragmentManagerUser().goToMore();
                break;
            case IMPORT_WALLET:
                new FragmentManagerUser().goToImportWallet();
                break;
            case NEW_WALLET:
                new FragmentManagerUser().goToNewWallet();
                break;
            case NEW_ACCOUNT:
                new FragmentManagerUser().goToNewAccount();
                break;
            case RECOVER_ACCOUNT:
                new FragmentManagerUser().goToRecoverAccount();
                break;
            case RECEIVE:
                new FragmentManagerUser().goToReceive();
                break;
            case SEND:
                new FragmentManagerUser().goToSend();
                break;
            case SETTINGS:
                new FragmentManagerUser().goToPreferences();
                break;
            case DIALOG_RECEIVE:
                new FragmentManagerUser().goToDialogReceive();
                break;
            case DIALOG_TRANSACTION_DETAIL:
                new FragmentManagerUser().goToDialogTransactionDetail(params);
                break;
            case DIALOG_STAKING_DETAIL:
                new FragmentManagerUser().goToDialogStakingDetail(params);
                break;
            case DIALOG_CREATE_STAKING:
                new FragmentManagerUser().goToDialogCreateStaking();
                break;
            default:
                new FragmentManagerUser().goToOpenWallet();
                break;
        }
    }

    public int getCurrentFragment() {
        FragmentManager fragmentManager = getInstance().getSupportFragmentManager();
        if(fragmentManager.getBackStackEntryCount() != 0) {
            FragmentManager.BackStackEntry entry =  fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1);
            return Integer.parseInt(entry.getName());
        }
        return -1;
    }

    public void clearBackStack() {
        FragmentManager fragmentManager = getInstance().getSupportFragmentManager();
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            fragmentManager.popBackStack();
        }
    }

    public void goToOpenWallet() {
        Global.setVisiblePage(Global.visiblePage.OPEN_WALLET);
        if(getCurrentFragment() != Global.visiblePage.OPEN_WALLET.ordinal()) {
            getInstance().getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack(String.valueOf(Global.visiblePage.OPEN_WALLET.ordinal()))
                    .replace(R.id.nav_host_fragment_content_main, new FragmentOpenWallet(), "OPEN_WALLET")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    }

    public void goToImportWallet() {
        Global.setVisiblePage(Global.visiblePage.IMPORT_WALLET);
        if(getCurrentFragment() != Global.visiblePage.IMPORT_WALLET.ordinal()) {
            getInstance().getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack(String.valueOf(Global.visiblePage.IMPORT_WALLET.ordinal()))
                    .replace(R.id.nav_host_fragment_content_main, new FragmentImportWallet(), "IMPORT_WALLET")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    }

    public void goToNewWallet() {
        Global.setVisiblePage(Global.visiblePage.NEW_WALLET);
        if(getCurrentFragment() != Global.visiblePage.NEW_WALLET.ordinal()) {
            getInstance().getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack(String.valueOf(Global.visiblePage.NEW_WALLET.ordinal()))
                    .replace(R.id.nav_host_fragment_content_main, new FragmentNewWallet(), "NEW_WALLET")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    }

    public void goToNewAccount() {
        Global.setVisiblePage(Global.visiblePage.NEW_ACCOUNT);
        if(getCurrentFragment() != Global.visiblePage.NEW_ACCOUNT.ordinal()) {
            getInstance().getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack(String.valueOf(Global.visiblePage.NEW_ACCOUNT.ordinal()))
                    .replace(R.id.nav_host_fragment_content_main, new FragmentNewAccount(), "NEW_ACCOUNT")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    }

    public void goToRecoverAccount() {
        Global.setVisiblePage(Global.visiblePage.RECOVER_ACCOUNT);
        if(getCurrentFragment() != Global.visiblePage.RECOVER_ACCOUNT.ordinal()) {
            getInstance().getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack(String.valueOf(Global.visiblePage.RECOVER_ACCOUNT.ordinal()))
                    .replace(R.id.nav_host_fragment_content_main, new FragmentRecoverAccount(), "RECOVER_ACCOUNT")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    }

    public void goToStaking() {
        clearBackStack();
        Global.setVisiblePage(Global.visiblePage.STAKING);
        if(getCurrentFragment() != Global.visiblePage.STAKING.ordinal()) {
            getInstance().getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack(String.valueOf(Global.visiblePage.STAKING.ordinal()))
                    .replace(R.id.nav_host_fragment_content_main, new FragmentStaking(), "STAKING")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
        CurvedBottomNavigationView bottomNavigationView = getInstance().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.onMenuItemClick(Global.visiblePage.STAKING.ordinal());
    }

    public void goToSwap() {
        clearBackStack();
        Global.setVisiblePage(Global.visiblePage.SWAP);
        if(getCurrentFragment() != Global.visiblePage.SWAP.ordinal()) {
            getInstance().getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack(String.valueOf(Global.visiblePage.SWAP.ordinal()))
                    .replace(R.id.nav_host_fragment_content_main, new FragmentSwap(), "SWAP")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
        CurvedBottomNavigationView bottomNavigationView = getInstance().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.onMenuItemClick(Global.visiblePage.SWAP.ordinal());
    }

    public void goToAccount() {
        clearBackStack();
            Global.setVisiblePage(Global.visiblePage.ACCOUNT);
        if(getCurrentFragment() != Global.visiblePage.ACCOUNT.ordinal()) {
            getInstance().getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack(String.valueOf(Global.visiblePage.ACCOUNT.ordinal()))
                    .replace(R.id.nav_host_fragment_content_main, new FragmentAccount(), "ACCOUNT")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
        CurvedBottomNavigationView bottomNavigationView = getInstance().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.onMenuItemClick(Global.visiblePage.ACCOUNT.ordinal());
    }

    public void goToDex() {
        clearBackStack();
        Global.setVisiblePage(Global.visiblePage.DEX);
        if(getCurrentFragment() != Global.visiblePage.DEX.ordinal()) {
            getInstance().getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack(String.valueOf(Global.visiblePage.DEX.ordinal()))
                    .replace(R.id.nav_host_fragment_content_main, new FragmentDex(), "DEX")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
        CurvedBottomNavigationView bottomNavigationView = getInstance().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.onMenuItemClick(Global.visiblePage.DEX.ordinal());
    }

    public void goToMore() {
        clearBackStack();
        Global.setVisiblePage(Global.visiblePage.MORE);
        if(getCurrentFragment() != Global.visiblePage.MORE.ordinal()) {
            getInstance().getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack(String.valueOf(Global.visiblePage.MORE.ordinal()))
                    .replace(R.id.nav_host_fragment_content_main, new FragmentMore(), "MORE")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
        CurvedBottomNavigationView bottomNavigationView = getInstance().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.onMenuItemClick(Global.visiblePage.MORE.ordinal());
    }

    public void goToReceive() {
        Global.setVisiblePage(Global.visiblePage.RECEIVE);
        if(getCurrentFragment() != Global.visiblePage.RECEIVE.ordinal()) {
            getInstance().getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack(String.valueOf(Global.visiblePage.RECEIVE.ordinal()))
                    .replace(R.id.nav_host_fragment_content_main, new FragmentReceive(), "RECEIVE")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    }

    public void goToReceive(String ticker, String provider, String contractAddress, String depositAddress, double minDeposit, double depositionFee, int confirmations) {
        Global.setVisiblePage(Global.visiblePage.RECEIVE);
        if(getCurrentFragment() != Global.visiblePage.RECEIVE.ordinal()) {
            getInstance().getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack(String.valueOf(Global.visiblePage.RECEIVE.ordinal()))
                    .replace(R.id.nav_host_fragment_content_main, new FragmentReceive(ticker, provider, contractAddress, depositAddress, minDeposit, depositionFee, confirmations), "RECEIVE_EXT_TOKEN")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    }

    public void goToSend() {
        Global.setVisiblePage(Global.visiblePage.SEND);
        if(getCurrentFragment() != Global.visiblePage.SEND.ordinal()) {
            getInstance().getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack(String.valueOf(Global.visiblePage.SEND.ordinal()))
                    .replace(R.id.nav_host_fragment_content_main, new FragmentSend(), "SEND")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    }

    public void goToSend(String ticker, String provider, String contractAddress, double amountToSend, double withdrawFee) {
        Global.setVisiblePage(Global.visiblePage.SEND);
        if(getCurrentFragment() != Global.visiblePage.SEND.ordinal()) {
            getInstance().getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack(String.valueOf(Global.visiblePage.SEND.ordinal()))
                    .replace(R.id.nav_host_fragment_content_main, new FragmentSend(ticker, provider, contractAddress, amountToSend, withdrawFee), "SEND")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    }

    public void goToPreferences() {
        Global.setVisiblePage(Global.visiblePage.SETTINGS);
        if(getCurrentFragment() != Global.visiblePage.SETTINGS.ordinal()) {
            getInstance().getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack(String.valueOf(Global.visiblePage.SETTINGS.ordinal()))
                    .replace(R.id.nav_host_fragment_content_main, new FragmentPreferencesRoot(), "SETTINGS")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    }

    public void goToAccountHistory() {
        Global.setVisiblePage(Global.visiblePage.ACCOUNT_HISTORY);
        if(getCurrentFragment() != Global.visiblePage.ACCOUNT_HISTORY.ordinal()) {
            getInstance().getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack(String.valueOf(Global.visiblePage.ACCOUNT_HISTORY.ordinal()))
                    .replace(R.id.nav_host_fragment_content_main, new FragmentAccountHistory(), "ACCOUNT_HISTORY")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
        CurvedBottomNavigationView bottomNavigationView = getInstance().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.onMenuItemClick(Global.visiblePage.ACCOUNT.ordinal());
    }

    public void goToDialogReceive() {
        Global.setVisiblePage(Global.visiblePage.DIALOG_RECEIVE);
        if(getCurrentFragment() != Global.visiblePage.DIALOG_RECEIVE.ordinal()) {
            getInstance().getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack(String.valueOf(Global.visiblePage.DIALOG_RECEIVE.ordinal()))
                    .replace(R.id.nav_host_fragment_content_main, new FragmentReceiveDialog(), "DIALOG_RECEIVE")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    }

    public void goToDialogTransactionDetail(String[] params) {
        Global.setVisiblePage(Global.visiblePage.DIALOG_TRANSACTION_DETAIL);
        if(getCurrentFragment() != Global.visiblePage.DIALOG_TRANSACTION_DETAIL.ordinal()) {
            getInstance().getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack(String.valueOf(Global.visiblePage.DIALOG_TRANSACTION_DETAIL.ordinal()))
                    .replace(R.id.nav_host_fragment_content_main, new FragmentTransactionDetailsDialog(params), "DIALOG_TRANSACTION_DETAIL")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    }

    public void goToDialogStakingDetail(String[] params) {
        Global.setVisiblePage(Global.visiblePage.DIALOG_STAKING_DETAIL);
        if(getCurrentFragment() != Global.visiblePage.DIALOG_STAKING_DETAIL.ordinal()) {
            getInstance().getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack(String.valueOf(Global.visiblePage.DIALOG_STAKING_DETAIL.ordinal()))
                    .replace(R.id.nav_host_fragment_content_main, new FragmentTransactionDetailsDialog(params), "DIALOG_STAKING_DETAIL")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    }

    public void goToDialogCreateStaking() {
        Global.setVisiblePage(Global.visiblePage.DIALOG_CREATE_STAKING);
        if(getCurrentFragment() != Global.visiblePage.DIALOG_CREATE_STAKING.ordinal()) {
            getInstance().getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack(String.valueOf(Global.visiblePage.DIALOG_CREATE_STAKING.ordinal()))
                    .replace(R.id.nav_host_fragment_content_main, new FragmentProfitingListingInfo(), "DIALOG_CREATE_STAKING")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    }
}
