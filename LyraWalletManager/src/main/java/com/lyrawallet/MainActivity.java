package com.lyrawallet;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.lyrawallet.PreferencesLoad.PreferencesLoad;
import com.lyrawallet.Storage.StorageCommon;
import com.lyrawallet.Ui.UiHelpers;
import com.lyrawallet.Ui.FragmentPreferences.FragmentPreferencesRoot;
import com.lyrawallet.Accounts.Accounts;
import com.lyrawallet.Api.Network.NetworkWebHttps;
import com.lyrawallet.Ui.FragmentDashboard.FragmentDashboard;
import com.lyrawallet.Ui.FragmentWalletManagement.FragmentOpenWallet;
import com.lyrawallet.Ui.FragmentMyAccountReceive.FragmentMyAccountReceive;
import com.lyrawallet.Ui.FragmentMyAccountSend.FragmentMyAccountSend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity implements NetworkWebHttps.WebHttpsTaskInformer {
    private static MainActivity Instance = null;
    private static int DeviceOrientation = 0;
    protected static MainActivity getInstance() {
        return Instance;
    }
    protected static int getDeviceOrientation() {
        return DeviceOrientation;
    }
    protected static void setDeviceOrientation(int orientation) {
        DeviceOrientation = orientation;
    }
    private Handler UserInputTimeoutHandler;
    private Runnable UserInputTimeoutRunable;

    /******************* Navigation, separate them from button events, for re-usage ***************/
    protected void toOpenWallet() {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, FragmentOpenWallet.newInstance())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
    protected void toDashboard() {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, Global.getDashboard())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
    protected void toReceive() {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, Global.getMyAccountReceive())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
    protected void toSend() {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, Global.getMyAccountSend())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
    protected void toSettings() {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, Global.getSettings())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
    /************************************** Events ************************************************/
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Instance = this;
        // At the moment force device to stay in portrait mode.
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Load user preferences.
        new PreferencesLoad();
        // Get the path of the working directory, we cann not get it in a static function in Global class.
        Global.setWalletPath(getApplicationContext().getFilesDir().getPath() + File.separator);
        setContentView(R.layout.content_main);
        // Fix UI when Soft Keyboard is visible, avoiding buttons to disappear when soft keyboard is visible..
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        // Create persistent pages and store the pointer for less processing power usage avoiding destruction and reconstruction.
        if(Global.getDashboard() == null) {
            Global.setDashboard(FragmentDashboard.newInstance("", ""));
        }
        if(Global.getMyAccountReceive() == null) {
            Global.setMyAccountReceive(FragmentMyAccountReceive.newInstance("", ""));
        }
        if(Global.getMyAccountSend() == null) {
            Global.setMyAccountSend(FragmentMyAccountSend.newInstance("", ""));
        }
        if(Global.getSettings() == null) {
            Global.setSettings(new FragmentPreferencesRoot());
        }
        // Show the Open Wallet page.
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, FragmentOpenWallet.newInstance())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        UserInputTimeoutHandler = new Handler();
        UserInputTimeoutRunable = new Runnable() {
            @Override
            public void run() {
                closeWallet();
            }
        };
        int inactivity = Global.getInactivityTimeForClose();
        if(inactivity != -1) {
            startHandler(inactivity);
        }
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
    /********************************** Go to other windows ***************************************/
    // Buttons events, for UI navigation.
    public void dashboard(View view) {
        toDashboard();
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

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        int inactivity = Global.getInactivityTimeForClose();
        stopHandler();//stop first and then start
        if(inactivity != -1) {
            startHandler(inactivity);
        }
    }
    public void stopHandler() {
        UserInputTimeoutHandler.removeCallbacks(UserInputTimeoutRunable);
    }
    public void startHandler(int inactivityTime) {
        UserInputTimeoutHandler.postDelayed(UserInputTimeoutRunable, (long) inactivityTime * 60*1000);
    }
    /********************************** Save file dialog & Open file dialog ***********************/
    // Need to be in main activity, they don't work elsewhere, so I put them here.
    private static String importWalletName = null;

    protected void backUpWallet(int procedure) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/" + Global.getDefaultWalletExtension());
        intent.putExtra(Intent.EXTRA_TITLE, Global.getWalletName() + "." + Global.getDefaultWalletExtension());
        Instance.startActivityForResult(intent, procedure);
    }
    protected void importWallet(int procedure, String walletName) {
        importWalletName = walletName;
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/" + Global.getDefaultWalletExtension());
        intent.setType("*/*");
        Instance.startActivityForResult(intent, procedure);
    }
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
                        String path = Global.getWalletPath(importWalletName);
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