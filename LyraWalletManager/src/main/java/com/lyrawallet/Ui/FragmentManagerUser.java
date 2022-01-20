package com.lyrawallet.Ui;

import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.lyrawallet.Global;
import com.lyrawallet.MainActivity;
import com.lyrawallet.R;
import com.lyrawallet.Ui.FragmentAccount.FragmentAccount;
import com.lyrawallet.Ui.FragmentDex.FragmentDex;
import com.lyrawallet.Ui.FragmentMore.FragmentMore;
import com.lyrawallet.Ui.FragmentPreferences.FragmentPreferencesRoot;
import com.lyrawallet.Ui.FragmentReceive.FragmentReceive;
import com.lyrawallet.Ui.FragmentReceive.FragmentReceiveDialog;
import com.lyrawallet.Ui.FragmentSend.FragmentSend;
import com.lyrawallet.Ui.FragmentStaking.FragmentStaking;
import com.lyrawallet.Ui.FragmentSwap.FragmentSwap;
import com.lyrawallet.Ui.FragmentWalletManagement.FragmentImportWallet;
import com.lyrawallet.Ui.FragmentWalletManagement.FragmentNewAccount;
import com.lyrawallet.Ui.FragmentWalletManagement.FragmentNewWallet;
import com.lyrawallet.Ui.FragmentWalletManagement.FragmentOpenWallet;
import com.lyrawallet.Ui.FragmentWalletManagement.FragmentRecoverAccount;

import java.util.Objects;

import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class FragmentManagerUser extends MainActivity {
    public int getCurrentFragment() {
        FragmentManager fragmentManager = getInstance().getSupportFragmentManager();
        if(fragmentManager.getBackStackEntryCount() != 0) {
            FragmentManager.BackStackEntry entry =  fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1);
            return Integer.parseInt(entry.getName());
        }
        return -1;
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

}
