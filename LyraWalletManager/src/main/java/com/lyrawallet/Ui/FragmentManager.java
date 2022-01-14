package com.lyrawallet.Ui;

import android.view.View;

import androidx.fragment.app.FragmentTransaction;

import com.lyrawallet.Global;
import com.lyrawallet.MainActivity;
import com.lyrawallet.R;
import com.lyrawallet.Ui.FragmentAccount.FragmentAccount;
import com.lyrawallet.Ui.FragmentDex.FragmentDex;
import com.lyrawallet.Ui.FragmentMore.FragmentMore;
import com.lyrawallet.Ui.FragmentPreferences.FragmentPreferencesRoot;
import com.lyrawallet.Ui.FragmentReceive.FragmentReceive;
import com.lyrawallet.Ui.FragmentSend.FragmentSend;
import com.lyrawallet.Ui.FragmentStaking.FragmentStaking;
import com.lyrawallet.Ui.FragmentSwap.FragmentSwap;
import com.lyrawallet.Ui.FragmentWalletManagement.FragmentImportWallet;
import com.lyrawallet.Ui.FragmentWalletManagement.FragmentNewAccount;
import com.lyrawallet.Ui.FragmentWalletManagement.FragmentNewWallet;
import com.lyrawallet.Ui.FragmentWalletManagement.FragmentOpenWallet;
import com.lyrawallet.Ui.FragmentWalletManagement.FragmentRecoverAccount;

import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class FragmentManager extends MainActivity {
    public void goToOpenWallet() {
        Global.setVisiblePage(Global.visiblePage.OPEN_WALLET);
        getInstance().getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .addToBackStack(String.valueOf(Global.getVisiblePage().ordinal()))
                .replace(R.id.nav_host_fragment_content_main, new FragmentOpenWallet())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    public void goToImportWallet() {
        Global.setVisiblePage(Global.visiblePage.IMPORT_WALLET);
        getInstance().getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .addToBackStack(String.valueOf(Global.getVisiblePage().ordinal()))
                .replace(R.id.nav_host_fragment_content_main, new FragmentImportWallet())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    public void goToNewWallet() {
        Global.setVisiblePage(Global.visiblePage.NEW_WALLET);
        getInstance().getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .addToBackStack(String.valueOf(Global.getVisiblePage().ordinal()))
                .replace(R.id.nav_host_fragment_content_main, new FragmentNewWallet())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    public void goToNewAccount() {
        Global.setVisiblePage(Global.visiblePage.NEW_ACCOUNT);
        getInstance().getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .addToBackStack(String.valueOf(Global.getVisiblePage().ordinal()))
                .replace(R.id.nav_host_fragment_content_main, new FragmentNewAccount())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    public void goToRecoverAccount() {
        Global.setVisiblePage(Global.visiblePage.RECOVER_ACCOUNT);
        getInstance().getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .addToBackStack(String.valueOf(Global.getVisiblePage().ordinal()))
                .replace(R.id.nav_host_fragment_content_main, new FragmentRecoverAccount())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    public void goToStaking() {
        Global.setVisiblePage(Global.visiblePage.STAKING);
        getInstance().getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .addToBackStack(String.valueOf(Global.getVisiblePage().ordinal()))
                .replace(R.id.nav_host_fragment_content_main, new FragmentStaking())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        CurvedBottomNavigationView bottomNavigationView = getInstance().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
        selectOnly = true;
        bottomNavigationView.onMenuItemClick(Global.visiblePage.STAKING.ordinal());
    }

    public void goToSwap() {
        Global.setVisiblePage(Global.visiblePage.SWAP);
        getInstance().getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .addToBackStack(String.valueOf(Global.getVisiblePage().ordinal()))
                .replace(R.id.nav_host_fragment_content_main, new FragmentSwap())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        CurvedBottomNavigationView bottomNavigationView = getInstance().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
        selectOnly = true;
        bottomNavigationView.onMenuItemClick(Global.visiblePage.SWAP.ordinal());
    }

    public void goToAccount() {
        Global.setVisiblePage(Global.visiblePage.ACCOUNT);
        getInstance().getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .addToBackStack(String.valueOf(Global.getVisiblePage().ordinal()))
                .replace(R.id.nav_host_fragment_content_main, new FragmentAccount())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        CurvedBottomNavigationView bottomNavigationView = getInstance().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
        selectOnly = true;
        bottomNavigationView.onMenuItemClick(Global.visiblePage.ACCOUNT.ordinal());
    }

    public void goToDex() {
        Global.setVisiblePage(Global.visiblePage.DEX);
        getInstance().getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .addToBackStack(String.valueOf(Global.getVisiblePage().ordinal()))
                .replace(R.id.nav_host_fragment_content_main, new FragmentDex())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        CurvedBottomNavigationView bottomNavigationView = getInstance().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
        selectOnly = true;
        bottomNavigationView.onMenuItemClick(Global.visiblePage.DEX.ordinal());
    }

    public void goToMore() {
        Global.setVisiblePage(Global.visiblePage.MORE);
        getInstance().getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .addToBackStack(String.valueOf(Global.getVisiblePage().ordinal()))
                .replace(R.id.nav_host_fragment_content_main, new FragmentMore())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        CurvedBottomNavigationView bottomNavigationView = getInstance().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
        selectOnly = true;
        bottomNavigationView.onMenuItemClick(Global.visiblePage.MORE.ordinal());
    }

    public void goToReceive() {
        Global.setVisiblePage(Global.visiblePage.RECEIVE);
        getInstance().getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .addToBackStack(String.valueOf(Global.getVisiblePage().ordinal()))
                .replace(R.id.nav_host_fragment_content_main, new FragmentReceive())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    public void goToSend() {
        Global.setVisiblePage(Global.visiblePage.SEND);
        getInstance().getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .addToBackStack(String.valueOf(Global.getVisiblePage().ordinal()))
                .replace(R.id.nav_host_fragment_content_main, new FragmentSend())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    public void goToPreferences() {
        Global.setVisiblePage(Global.visiblePage.SETTINGS);
        getInstance().getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .addToBackStack(String.valueOf(Global.getVisiblePage().ordinal()))
                .replace(R.id.nav_host_fragment_content_main, new FragmentPreferencesRoot())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

}
