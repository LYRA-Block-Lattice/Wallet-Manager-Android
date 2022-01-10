package com.lyrawallet;

import android.app.Activity;
import android.content.Intent;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.lyrawallet.PreferencesLoad.PreferencesLoad;
import com.lyrawallet.Storage.StorageCommon;
import com.lyrawallet.Ui.FragmentWalletManagement.FragmentImportWallet;
import com.lyrawallet.Ui.FragmentWalletManagement.FragmentNewAccount;
import com.lyrawallet.Ui.FragmentWalletManagement.FragmentNewWallet;
import com.lyrawallet.Ui.FragmentWalletManagement.FragmentRecoverAccount;
import com.lyrawallet.Ui.UiHelpers;
import com.lyrawallet.Ui.FragmentPreferences.FragmentPreferencesRoot;
import com.lyrawallet.Accounts.Accounts;
import com.lyrawallet.Api.Network.NetworkWebHttps;
import com.lyrawallet.Ui.FragmentAccount.FragmentAccount;
import com.lyrawallet.Ui.FragmentWalletManagement.FragmentOpenWallet;
import com.lyrawallet.Ui.FragmentReceive.FragmentReceive;
import com.lyrawallet.Ui.FragmentSend.FragmentSend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.List;

import kotlin.UInt;
import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem;
import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class MainActivity extends AppCompatActivity implements NetworkWebHttps.WebHttpsTaskInformer {
    private static MainActivity Instance = null;
    private static int DeviceOrientation = 0;
    private static String ImportWalletName = null;
    private Handler UserInputTimeoutHandler;
    //BottomNavigationView bottomNavigationView;
    CurvedBottomNavigationView bottomNavigationView;
    protected static MainActivity getInstance() {
        return Instance;
    }
    protected static int getDeviceOrientation() {
        return DeviceOrientation;
    }
    protected static void setDeviceOrientation(int orientation) {
        DeviceOrientation = orientation;
    }
    /********************************** Save file dialog & Open file dialog ***********************/
    protected void backUpWallet(int procedure) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/" + Global.getDefaultWalletExtension());
        intent.putExtra(Intent.EXTRA_TITLE, Global.getWalletName() + "." + Global.getDefaultWalletExtension());
        Instance.startActivityForResult(intent, procedure);
    }
    protected void importWallet(int procedure, String walletName) {
        ImportWalletName = walletName;
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/" + Global.getDefaultWalletExtension());
        intent.setType("*/*");
        Instance.startActivityForResult(intent, procedure);
    }
    /******************* Navigation, separate them from button events, for re-usage ***************/
    protected void toOpenWallet() {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, FragmentOpenWallet.newInstance())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        Global.setVisiblePage(Global.visiblePage.OPEN_WALLET);
    }
    protected void toImportWallet() {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, FragmentImportWallet.newInstance())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        Global.setVisiblePage(Global.visiblePage.IMPORT_WALLET);
    }
    protected void toNewWallet() {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, FragmentNewWallet.newInstance())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        Global.setVisiblePage(Global.visiblePage.NEW_WALLET);
    }
    protected void toNewAccount() {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, FragmentNewAccount.newInstance())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        Global.setVisiblePage(Global.visiblePage.NEW_ACCOUNT);
    }
    protected void toRecoverAccount() {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, FragmentRecoverAccount.newInstance())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        Global.setVisiblePage(Global.visiblePage.RECOVER_ACCOUNT);
    }
    protected void toWallet() {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, Global.getDashboard())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        Global.setVisiblePage(Global.visiblePage.WALLET);
    }
    protected void toReceive() {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, Global.getMyAccountReceive())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        Global.setVisiblePage(Global.visiblePage.MY_ACCOUNT_RECEIVE);
    }
    protected void toSend() {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, Global.getMyAccountSend())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        Global.setVisiblePage(Global.visiblePage.MY_ACCOUNT_SEND);
    }
    protected void toSettings() {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, Global.getSettings())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        Global.setVisiblePage(Global.visiblePage.SETTINGS);
    }

    protected void setVisiblePage(Global.visiblePage p) {
        Global.setVisiblePage(p);
        switch(p) {
            case IMPORT_WALLET:
                toImportWallet();
                break;
            case NEW_WALLET:
                toNewWallet();
                break;
            case NEW_ACCOUNT:
                toNewAccount();
                break;
            case RECOVER_ACCOUNT:
                toRecoverAccount();
                break;
            case WALLET:
                toWallet();
                break;
            case MY_ACCOUNT:
                break;
            case MY_ACCOUNT_RECEIVE:
                toReceive();
                break;
            case MY_ACCOUNT_SEND:
                toSend();
                break;
            case SETTINGS:
                toSettings();
                break;
            default:
                toOpenWallet();
                break;
        }
    }
    /********************************** Go to other windows ***************************************/
    // Buttons events, for UI navigation.
    public void dashboard(View view) {
        toWallet();
    }
    public void send(View view) {
        toSend();
    }
    public void receive(View view) {
        toReceive();
    }
    public void settings(View view) {
        toSettings();
    }
    public void openWallet(View view) {
        toOpenWallet();
    }
    public void closeWallet(View view) {
        finish();
        System.exit(0);
    }
    public void closeWallet() {
        finish();
        System.exit(0);
    }
    public void stopHandler() {
        UserInputTimeoutHandler.removeCallbacks(UserInputTimeoutRunnable);
    }
    public void startHandler(int inactivityTime) {
        UserInputTimeoutHandler.postDelayed(UserInputTimeoutRunnable, (long) inactivityTime * 60*1000);
    }
    /************************************** Events ************************************************/
    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("SHOWN_WINDOW", Global.getVisiblePage().ordinal());
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Global.visiblePage p = Global.visiblePage.values()[savedInstanceState.getInt("SHOWN_WINDOW")];
        // Show the same page as when the view was destroyed.
        setVisiblePage(p);
        Accounts.restoreAccountSelectSpinner(this);
    }
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Instance = this;
        // At the moment force device to stay in portrait mode.
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Load user preferences.
        new PreferencesLoad();
        // Get the path of the working directory, we cann not get it in a static function in Global class.
        Global.setWalletPath(getApplicationContext().getFilesDir().getPath() + File.separator);
        setContentView(R.layout.content_main);
        // Fix UI when Soft Keyboard is visible, avoiding buttons to disappear when soft keyboard is visible..
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        // Create persistent pages and store the pointer for less processing power usage avoiding destruction and reconstruction.
        if(Global.getDashboard() == null) {
            Global.setDashboard(FragmentAccount.newInstance("", ""));
        }
        if(Global.getMyAccountReceive() == null) {
            Global.setMyAccountReceive(FragmentReceive.newInstance("", ""));
        }
        if(Global.getMyAccountSend() == null) {
            Global.setMyAccountSend(FragmentSend.newInstance("", ""));
        }
        if(Global.getSettings() == null) {
            Global.setSettings(new FragmentPreferencesRoot());
        }
        // If the view was restored, load the last visible page enum.
        if(savedInstanceState != null) {
            Global.setVisiblePage(Global.visiblePage.values()[savedInstanceState.getInt("SHOWN_WINDOW")]);
        }
        // On first launch.
        if(Global.getVisiblePage() == null) {
            // Show the Open Wallet page.
            setVisiblePage(Global.visiblePage.OPEN_WALLET);
        }
        UserInputTimeoutHandler = new Handler();
        int inactivity = Global.getInactivityTimeForClose();
        if(inactivity != -1) {
            startHandler(inactivity);
        }
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        CbnMenuItem[] menuItems = new CbnMenuItem[]{
                new CbnMenuItem(
                        R.drawable.ic_outline_payments_48, // the icon
                        R.drawable.ic_outline_payments_48, // the AVD that will be shown in FAB
                        R.id.staking // optional if you use Jetpack Navigation
                ),
                new CbnMenuItem(
                        R.drawable.ic_round_swap_horiz_48,
                        R.drawable.ic_round_swap_horiz_48,
                        R.id.swap
                ),
                new CbnMenuItem(
                        R.drawable.ic_outline_account_balance_wallet_48,
                        R.drawable.ic_outline_account_balance_wallet_48,
                        R.id.wallet
                ),
                new CbnMenuItem(
                        R.drawable.ic_outline_grid_view_48,
                        R.drawable.ic_outline_grid_view_48,
                        R.id.dex
                ),
                new CbnMenuItem(
                        R.drawable.ic_baseline_more_horiz_48,
                        R.drawable.ic_baseline_more_horiz_48,
                        R.id.settings
                )
        };
        bottomNavigationView.setMenuItems(menuItems, 2);
        bottomNavigationView.setOnMenuItemClickListener ((CbnMenuItem cbnMenuItem, Integer index) -> {
            switch (cbnMenuItem.component3()) {
                case R.id.staking:
                case R.id.swap:
                    toSend();
                    return null;
                case R.id.wallet:
                    toWallet();
                    return null;
                case R.id.dex:
                case R.id.settings:
                    toSettings();
                    return null;
                default:
                    break;
            }
            return null;
        });

        /*new WebHttps(this).execute("https://api.latoken.com/v2/ticker", "MainCallHttps1");
        new WebHttps(this).execute("https://api.latoken.com/v2/ticker", "MainCallHttps2");*/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        Accounts accounts = new Accounts(this);
        //accounts.loadAccountsFromInternalContainer();
        return true;
    }
    @Override
    public void onWebHttpsTaskDone(NetworkWebHttps instance) {
        // Dummy event catcher, for further implementation.
        System.out.println(instance.getInstanceName() + ": " + instance.getContent());
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // Dummy event catcher, for further implementation.
        super.onConfigurationChanged(newConfig);
        int orientation = newConfig.orientation;
        if(getDeviceOrientation() != orientation) {
            setDeviceOrientation(orientation);

        }
    }
    @Override
    public boolean onKeyDown(int key_code, KeyEvent key_event) {
        if (key_code== KeyEvent.KEYCODE_BACK) {
            // Prevent back key to take effect, implemented for further development.
            super.onKeyDown(key_code, key_event);
            return false;
        }
        return false;
    }
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        int inactivity = Global.getInactivityTimeForClose();
        stopHandler();//stop first and then start
        if(inactivity != -1) {
            startHandler(inactivity);
        }
    }
    private final Runnable UserInputTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            closeWallet();
        }
    };
    // Need to be in main activity, they don't work elsewhere, so I put them here.
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        Uri uri = null;
        if (resultData != null) {
            uri = resultData.getData();
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == StorageCommon.BACKUP_WALLET) {
                    try {
                        OutputStream output = getContentResolver().openOutputStream(uri);
                        FileInputStream fileInputStream = new FileInputStream(Global.getWalletPath());
                        byte[] bytes = new byte[(int) fileInputStream.available()];
                        fileInputStream.read(bytes, 0, bytes.length);
                        output.write(bytes);
                        output.flush();
                        output.close();
                    } catch (IOException ignored) {
                        UiHelpers.closeKeyboard(findViewById(R.id.nav_host_fragment_content_main));
                        Snackbar.make(this.findViewById(R.id.nav_host_fragment_content_main), getString(R.string.str_something_went_wrong_when_beackup_wallet), Snackbar.LENGTH_LONG)
                                .setAction("", null).show();
                    }
                } else if (requestCode == StorageCommon.IMPORT_WALLET) {
                    try {
                        InputStream fileInputStream = getContentResolver().openInputStream(uri);
                        String path = Global.getWalletPath(ImportWalletName);
                        File dstFile = new File(path);
                        if (!dstFile.exists()) {
                            FileWriter fiw = new FileWriter(dstFile);
                            byte[] bytes = new byte[(int) fileInputStream.available()];
                            fileInputStream.read(bytes, 0, bytes.length);
                            fiw.write(new String(bytes));
                            fiw.flush();
                            fiw.close();
                            toOpenWallet();
                        } else {
                            UiHelpers.closeKeyboard(findViewById(R.id.nav_host_fragment_content_main));
                            Snackbar.make(findViewById(R.id.nav_host_fragment_content_main), "File exists", Snackbar.LENGTH_LONG)
                                    .setAction("", null).show();
                        }
                    } catch (IOException e) {
                        UiHelpers.closeKeyboard(findViewById(R.id.nav_host_fragment_content_main));
                        Snackbar.make(findViewById(R.id.nav_host_fragment_content_main), getString(R.string.str_something_went_wrong_when_importing_wallet), Snackbar.LENGTH_LONG)
                                .setAction("", null).show();
                    }
                }
            }
        }
    }
}