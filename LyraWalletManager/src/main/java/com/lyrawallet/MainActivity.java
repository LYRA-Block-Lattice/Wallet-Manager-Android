package com.lyrawallet;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
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
import com.lyrawallet.Ui.Preferences.FragmentPreferencesRoot;
import com.lyrawallet.Accounts.Accounts;
import com.lyrawallet.Api.Network.WebHttps;
import com.lyrawallet.Api.Rpc;
import com.lyrawallet.Ui.Dashboard.FragmentDashboard;
import com.lyrawallet.Ui.WalletManagement.FragmentOpenWallet;
import com.lyrawallet.Ui.MyAccountReceive.FragmentMyAccountReceive;
import com.lyrawallet.Ui.MyAccountSend.FragmentMyAccountSend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity implements WebHttps.WebHttpsTaskInformer, Rpc.RpcTaskInformer {
    protected static MainActivity instance = null;
    int deviceOrientation = 0;
    void showOpenWallet() {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, FragmentOpenWallet.newInstance())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
    void showDashboard() {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, Global.dashboard)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
    void showReceive() {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, Global.myAccountReceive)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
    void showSend() {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, Global.myAccountSend)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
    void showSettings() {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, Global.settings)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        new PreferencesLoad();
        //Get the path of the working directory.
        Global.walletPath = getApplicationContext().getFilesDir().getPath() + File.separator;
        setContentView(R.layout.content_main);
        // Fix UI when Soft Keyboard is visible.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if(Global.dashboard == null) {
            Global.dashboard = FragmentDashboard.newInstance("", "");
        }
        if(Global.myAccountReceive == null) {
            Global.myAccountReceive = FragmentMyAccountReceive.newInstance("", "");
        }
        if(Global.myAccountSend == null) {
            Global.myAccountSend = FragmentMyAccountSend.newInstance("", "");
        }
        if(Global.settings == null) {
            Global.settings = new FragmentPreferencesRoot();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, FragmentOpenWallet.newInstance())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        /*new Rpc(instance).execute("MainCallRpc1",
                "History",
                "LF6LpgcrANQWrErPcLREAbKzJg9DLeuXXa45cz5hKsUng7aJ2zCrAgHbtkSXv5dXiEfUB8ypN8i3daUkmiJwcX8cbXSv5U",
                "0", String.valueOf(System.currentTimeMillis()), "0");*/
        /*new Rpc(this).execute("MainCallRpc2",
                "Receive",
                "LF6LpgcrANQWrErPcLREAbKzJg9DLeuXXa45cz5hKsUng7aJ2zCrAgHbtkSXv5dXiEfUB8ypN8i3daUkmiJwcX8cbXSv5U");
        new WebHttps(this).execute("https://api.latoken.com/v2/ticker", "MainCallHttps1");
        new WebHttps(this).execute("https://api.latoken.com/v2/ticker", "MainCallHttps2");*/
        /* List<Pair<String, String>> accList = new ArrayList<Pair<String, String>>();
        accList.add(new Pair<>("Test1", Global.pK));
        accList.add(new Pair<>("Test2", "dkrwRdqNjEEshpLuEPPqc6zM1HM3nzGjsYts39zzA1iUypcpj"));*/
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
    public void onWebHttpsTaskDone(WebHttps instance) {
        System.out.println(instance.getInstanceName() + ": " + instance.getContent());
    }
    @Override
    public void onRpcTaskDone(String[] output) {
        System.out.println(output[0] + ", " + output[1] + ", " + output[2]);
    }
    @Override
    public void onRpcNewEvent(String[] output) {
        System.out.println("RPC: " + output[0] + ", " + output[1]);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = newConfig.orientation;
        if(deviceOrientation != orientation) {
            deviceOrientation = orientation;

        }
    }
    @Override
    public boolean onKeyDown(int key_code, KeyEvent key_event) {
        if (key_code== KeyEvent.KEYCODE_BACK) {
            super.onKeyDown(key_code, key_event);
            return false;
        }
        return false;
    }

    public void dashboard(View view) {
        showDashboard();
    }
    public void send(View view) {
        showSend();
    }
    public void receive(View view) {
        showReceive();
    }
    public void settings(View view) {
        showSettings();
    }
    public void openWallet(View view) {
        showOpenWallet();
    }
    public void closeWallet(View view) {
        finish();
        System.exit(0);
    }
/************************************** Save file dialog & Open file dialog **************************************/
    protected void backupWallet(int procedure) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/" + Global.defaultWalletExtension);
        intent.putExtra(Intent.EXTRA_TITLE, Global.walletName + "." + Global.defaultWalletExtension);
        instance.startActivityForResult(intent, procedure);
    }

    protected void importWallet(int procedure, String walletName) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/" + Global.defaultWalletExtension);
        intent.setType("*/*");
        instance.startActivityForResult(intent, procedure);
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
                        Snackbar.make(this.findViewById(R.id.nav_host_fragment_content_main), getString(R.string.str_something_went_wrong_when_beackup_wallet), Snackbar.LENGTH_LONG)
                                .setAction("", null).show();
                    }
                } else if (requestCode == StorageCommon.IMPORT_WALLET) {
                    try {
                        InputStream fileInputStream = getContentResolver().openInputStream(uri);
                        String path = Global.getWalletPath();
                        File dstFile = new File(path);
                        if (!dstFile.exists()) {
                            FileWriter fiw = new FileWriter(dstFile);
                            byte[] bytes = new byte[(int) fileInputStream.available()];
                            fileInputStream.read(bytes, 0, bytes.length);
                            fiw.write(new String(bytes));
                            fiw.flush();
                            fiw.close();
                            showOpenWallet();
                            Snackbar.make(findViewById(R.id.nav_host_fragment_content_main), "Saved OK", Snackbar.LENGTH_LONG)
                                    .setAction("", null).show();
                        } else {
                            Snackbar.make(findViewById(R.id.nav_host_fragment_content_main), "File exists", Snackbar.LENGTH_LONG)
                                    .setAction("", null).show();
                        }
                    } catch (IOException e) {
                        Snackbar.make(findViewById(R.id.nav_host_fragment_content_main), getString(R.string.str_something_went_wrong_when_importing_wallet), Snackbar.LENGTH_LONG)
                                .setAction("", null).show();
                    }
                }
                //walletName = null;
            }
        }
    }
}