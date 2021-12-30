package com.lyrawallet;

import android.content.pm.ActivityInfo;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.lyrawallet.Ui.Preferences.PreferencesRoot;
import com.lyrawallet.Accounts.Accounts;
import com.lyrawallet.Api.Network.WebHttps;
import com.lyrawallet.Api.Rpc;
import com.lyrawallet.Storage.KeyStorage;
import com.lyrawallet.Ui.Dashboard.Dashboard;
import com.lyrawallet.Ui.WalletManagement.OpenWallet;
import com.lyrawallet.Ui.MyAccountReceive.MyAccountReceive;
import com.lyrawallet.Ui.MyAccountSend.MyAccountSend;

import java.io.File;

public class MainActivity extends AppCompatActivity implements WebHttps.WebHttpsTaskInformer, Rpc.RpcTaskInformer {
    //private ActivityMainBinding binding = null;
    private static MainActivity instance = null;
    int deviceOrientation = 0;
    void showOpenWalletButtons() {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, OpenWallet.newInstance())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
    void showDashboardButtons() {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, Global.dashboard)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
    void showReceiveButtons() {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, Global.myAccountReceive)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
    void showSendButtons() {
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.nav_host_fragment_content_main, Global.myAccountSend)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
    void showSettingsButtons() {
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
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Get the path of the working directory.
        Global.walletPath = getApplicationContext().getFilesDir().getPath() + File.separator;
        setContentView(R.layout.content_main);
        // Fix UI when Soft Keyboard is visible.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        // A temporary password for debug purposes.
        instance = this;
        if(Global.dashboard == null) {
            Global.dashboard = Dashboard.newInstance("", "");
        }
        if(Global.myAccountReceive == null) {
            Global.myAccountReceive = MyAccountReceive.newInstance("", "");
        }
        if(Global.myAccountSend == null) {
            Global.myAccountSend = MyAccountSend.newInstance("", "");
        }
        if(Global.settings == null) {
            Global.settings = new PreferencesRoot();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.nav_host_fragment_content_main, OpenWallet.newInstance())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        new Rpc(instance).execute("MainCallRpc1",
                "wss://161.97.166.188:4504/api/v1/socket",
                "History",
                "LF6LpgcrANQWrErPcLREAbKzJg9DLeuXXa45cz5hKsUng7aJ2zCrAgHbtkSXv5dXiEfUB8ypN8i3daUkmiJwcX8cbXSv5U",
                "0", String.valueOf(System.currentTimeMillis()), "0");
        new Rpc(this).execute("MainCallRpc2",
                "wss://161.97.166.188:4504/api/v1/socket",
                "Receive",
                "LF6LpgcrANQWrErPcLREAbKzJg9DLeuXXa45cz5hKsUng7aJ2zCrAgHbtkSXv5dXiEfUB8ypN8i3daUkmiJwcX8cbXSv5U");
        new WebHttps(this).execute("https://api.latoken.com/v2/ticker", "MainCallHttps1");
        new WebHttps(this).execute("https://api.latoken.com/v2/ticker", "MainCallHttps2");
        /*List<Pair<String, String>> accList = new ArrayList<Pair<String, String>>();
        accList.add(new Pair<>("Test1", Global.pK));
        accList.add(new Pair<>("Test2", "dkrwRdqNjEEshpLuEPPqc6zM1HM3nzGjsYts39zzA1iUypcpj"));
        Global.walletPassword = "87654321";
        System.out.println("Create keystore result: " + KeyStorage.containerSave("test1", Global.walletPassword, accList));
*/
        //List<Pair<String, String>> recoveredKeys = KeyStorage.containerRead("test1", Global.walletPassword);

//        System.out.println("Key storage check: " + KeyStorage.containerExists("test1", Global.walletPassword));
        System.out.println(KeyStorage.decrypt(Global.accountsContainer, Global.walletPassword));

        //assert getSupportActionBar() != null;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        Accounts accounts = new Accounts(this);
        accounts.loadAccountsFromInternalContainer();
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
    public void dashboard(View view) {
        showDashboardButtons();
    }
    public void send(View view) {
        showSendButtons();
    }
    public void receive(View view) {
        showReceiveButtons();
    }
    public void settings(View view) {
        showSettingsButtons();
    }
    public void openWallet(View view) {
        showOpenWalletButtons();
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

    public void closeWallet(View view) {
        finish();
        System.exit(0);
    }
}