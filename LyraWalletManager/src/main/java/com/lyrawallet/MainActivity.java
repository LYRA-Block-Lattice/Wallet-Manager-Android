package com.lyrawallet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.lyrawallet.Accounts.Accounts;
import com.lyrawallet.Api.ApiRpc;
import com.lyrawallet.Api.Network.NetworkWebHttps;
import com.lyrawallet.Crypto.CryptoSignatures;
import com.lyrawallet.PreferencesLoad.PreferencesLoad;
import com.lyrawallet.Storage.StorageCommon;
import com.lyrawallet.Ui.FragmentManager;
import com.lyrawallet.Ui.UiHelpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem;
import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class MainActivity extends AppCompatActivity implements NetworkWebHttps.WebHttpsTaskInformer {
    private static MainActivity Instance = null;
    private static String ImportWalletName = null;
    private Handler UserInputTimeoutHandler;
    protected static MainActivity getInstance() {
        return Instance;
    }
    protected boolean selectOnly = false;
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
    protected void setVisiblePage(Global.visiblePage p) {
        Global.setVisiblePage(p);
        if(selectOnly) {
            selectOnly = false;
            return;
        }
        switch(p) {
            case STAKING:
                new FragmentManager().goToStaking();
                break;
            case SWAP:
                new FragmentManager().goToSwap();
                break;
            case ACCOUNT:
                new FragmentManager().goToAccount();
                break;
            case DEX:
                new FragmentManager().goToDex();
                break;
            case MORE:
                new FragmentManager().goToMore();
                break;
            case IMPORT_WALLET:
                new FragmentManager().goToImportWallet();
                break;
            case NEW_WALLET:
                new FragmentManager().goToNewWallet();
                break;
            case NEW_ACCOUNT:
                new FragmentManager().goToNewAccount();
                break;
            case RECOVER_ACCOUNT:
                new FragmentManager().goToRecoverAccount();
                break;
            case RECEIVE:
                new FragmentManager().goToReceive();
                break;
            case SEND:
                new FragmentManager().goToSend();
                break;
            case SETTINGS:
                new FragmentManager().goToPreferences();
                break;
            default:
                new FragmentManager().goToOpenWallet();
                break;
        }
    }
    /********************************** Go to other windows ***************************************/
    // Buttons events, for UI navigation.
    public void send(View view) {
        new FragmentManager().goToSend();
    }
    public void receive(View view) {
        new FragmentManager().goToReceive();
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
        //Accounts.restoreAccountSelectSpinner(this);
        setVisiblePage(p);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (Global.getVisiblePage().ordinal() < Global.visiblePage.OPEN_WALLET.ordinal()) {
                    CurvedBottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
                    bottomNavigationView.onMenuItemClick(Global.getVisiblePage().ordinal());
                }
            }
        }, 50);
    }
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Instance = this;
        // At the moment force device to stay in portrait mode.
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestPermissions(new String[]{Manifest.permission.CAMERA}, 1011);
        // Load user preferences.
        new PreferencesLoad();
        // Get the path of the working directory, we cann not get it in a static function in Global class.
        Global.setWalletPath(getApplicationContext().getFilesDir().getPath() + File.separator);
        setContentView(R.layout.content_main);
        // Fix UI when Soft Keyboard is visible, avoiding buttons to disappear when soft keyboard is visible..
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        // Create persistent pages and store the pointer for less processing power usage avoiding destruction and reconstruction.
        // If the view was restored, load the last visible page enum.
        if (savedInstanceState != null) {
            Global.setVisiblePage(Global.visiblePage.values()[savedInstanceState.getInt("SHOWN_WINDOW")]);
        }
        // On first launch.
        if (Global.getVisiblePage() == null) {
            // Show the Open Wallet page.
            setVisiblePage(Global.visiblePage.OPEN_WALLET);
        }
        UserInputTimeoutHandler = new Handler();
        int inactivity = Global.getInactivityTimeForClose();
        if (inactivity != -1) {
            startHandler(inactivity);
        }
        CurvedBottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        CbnMenuItem[] menuItems = new CbnMenuItem[]{
                new CbnMenuItem(
                        R.drawable.ic_outline_payments_24, // the icon
                        R.drawable.ic_outline_payments_24, // the AVD that will be shown in FAB
                        Global.visiblePage.STAKING.ordinal() // optional if you use Jetpack Navigation
                ),
                new CbnMenuItem(
                        R.drawable.ic_round_swap_horiz_24,
                        R.drawable.ic_round_swap_horiz_24,
                        Global.visiblePage.SWAP.ordinal()
                ),
                new CbnMenuItem(
                        R.drawable.ic_outline_account_balance_wallet_24,
                        R.drawable.ic_outline_account_balance_wallet_24,
                        Global.visiblePage.ACCOUNT.ordinal()
                ),
                new CbnMenuItem(
                        R.drawable.ic_outline_grid_view_24,
                        R.drawable.ic_outline_grid_view_24,
                        Global.visiblePage.DEX.ordinal()
                ),
                new CbnMenuItem(
                        R.drawable.ic_baseline_more_horiz_24,
                        R.drawable.ic_baseline_more_horiz_24,
                        Global.visiblePage.MORE.ordinal()
                )
        };
        if (Global.getVisiblePage().ordinal() < Global.visiblePage.OPEN_WALLET.ordinal()) {
            bottomNavigationView.setMenuItems(menuItems, Global.getVisiblePage().ordinal());
        } else {
            bottomNavigationView.setMenuItems(menuItems, 2);
            bottomNavigationView.setVisibility(View.GONE);
        }
        bottomNavigationView.setOnMenuItemClickListener((CbnMenuItem cbnMenuItem, Integer index) -> {
            setVisiblePage(Global.visiblePage.values()[cbnMenuItem.component3()]);
            return null;
        });
        setVisiblePage(Global.getVisiblePage());
        /*new WebHttps(this).execute("https://api.latoken.com/v2/ticker", "MainCallHttps1");
        new WebHttps(this).execute("https://api.latoken.com/v2/ticker", "MainCallHttps2");*/

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if( Global.getSelectedAccountName().length() != 0) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            new ApiRpc().act(new ApiRpc.Action().actionHistory(Global.str_api_rpc_purpose_history_disk_storage, Global.getCurrentNetworkName(), Global.getSelectedAccountName(), Global.getSelectedAccountId()));
                        }
                    });
                }
            }
        }, 1000, 60 * 1000);
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
        if(Global.getDeviceOrientation() != orientation) {
            Global.setDeviceOrientation(orientation);

        }
    }
    public Fragment getVisibleFragment(){
        androidx.fragment.app.FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if(fragments != null){
            for(Fragment fragment : fragments){
                if(fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }
    @Override
    public boolean onKeyDown(int key_code, KeyEvent key_event) {
        if (key_code== KeyEvent.KEYCODE_BACK) {
            // Prevent back key to take effect, implemented for further development.
            super.onKeyDown(key_code, key_event);
            androidx.fragment.app.FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 1) {
                fragmentManager.popBackStack();
                androidx.fragment.app.FragmentManager.BackStackEntry entry =  fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 2);
                CurvedBottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
                Global.setVisiblePage(Global.visiblePage.values()[Integer.parseInt(entry.getName())]);
                if (Global.getVisiblePage().ordinal() < Global.visiblePage.OPEN_WALLET.ordinal()) {
                    selectOnly = true;
                    bottomNavigationView.onMenuItemClick(Global.getVisiblePage().ordinal());
                } else {
                    //bottomNavigationView.setMenuItems(menuItems, 2);
                    bottomNavigationView.setVisibility(View.GONE);
                }
            } else {
                finish();
                System.exit(0);
            }
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1011: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Here user granted the permission
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your Camera", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
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
                        Snackbar.make(this.findViewById(R.id.nav_host_fragment_content_main), getString(R.string.str_something_went_wrong_when_backup_wallet), Snackbar.LENGTH_LONG)
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
                            new FragmentManager().goToOpenWallet();
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
                } else if (requestCode == StorageCommon.QR_SCAN) {
                    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, resultData);
                    if(result != null) {
                        if(result.getContents() != null){
                            EditText recipientAddressEditText = (EditText) findViewById(R.id.send_token_recipient_address_value);
                            if(recipientAddressEditText != null) {
                                try {
                                    JSONObject obj = new JSONObject(result.getContents());
                                    if(!obj.isNull("id")) {
                                        String id = obj.getString("id");
                                        recipientAddressEditText.setText(id);
                                    }
                                    if(!obj.isNull("amount")) {
                                        double amount = obj.getDouble("amount");
                                        EditText tokenAmountEditText = (EditText) findViewById(R.id.send_token_amount_value);
                                        if(tokenAmountEditText != null) {
                                            tokenAmountEditText.setText(String.format(Locale.US, "%f", amount));
                                        }
                                    }
                                    if(!obj.isNull("ticker")) {
                                        String ticker = obj.getString("ticker");
                                        Spinner tokenSpinner = (Spinner) findViewById(R.id.sendTokenSelectSpinner);
                                        if(tokenSpinner != null) {
                                            SpinnerAdapter adapter = tokenSpinner.getAdapter();
                                            for (int i = 0; i < adapter.getCount(); i++) {
                                                Object item = adapter.getItem(i);
                                                if(ticker.equals(item.toString())) {
                                                    tokenSpinner.setSelection(i);
                                                    break;
                                                }
                                            }
                                            System.out.println(adapter);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    if (CryptoSignatures.validateAccountId(result.getContents())) {
                                        recipientAddressEditText.setText(result.getContents());
                                    } else {
                                        Toast.makeText(this, "Scanned: Invalid address", Toast.LENGTH_LONG).show();
                                        recipientAddressEditText.setText("");
                                    }
                                }
                            }
                        }
                    } else {
                        super.onActivityResult(requestCode, resultCode, resultData);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //handle cancel
            }
        }
    }
}