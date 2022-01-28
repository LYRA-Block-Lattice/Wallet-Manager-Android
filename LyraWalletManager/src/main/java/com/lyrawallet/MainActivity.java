package com.lyrawallet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
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
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.lyrawallet.Accounts.Accounts;
import com.lyrawallet.Api.ApiRpc;
import com.lyrawallet.Api.Network.NetworkWebHttps;
import com.lyrawallet.Crypto.CryptoSignatures;
import com.lyrawallet.PreferencesLoad.PreferencesLoad;
import com.lyrawallet.Storage.StorageCommon;
import com.lyrawallet.Ui.FragmentManagerUser;
import com.lyrawallet.Ui.UiHelpers;
import com.lyrawallet.Ui.UtilGetData;

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
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import np.com.susanthapa.curved_bottom_navigation.CbnMenuItem;
import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class MainActivity extends AppCompatActivity implements NetworkWebHttps.WebHttpsTaskInformer {
    private static MainActivity Instance = null;
    private static String ImportWalletName = null;
    private Handler UserInputTimeoutHandler;
    protected static Boolean PushToBackStack = true;
    protected static MainActivity getInstance() {
        return Instance;
    }
    Timer timerBalance;
    Timer timerHistory;
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
    /********************************** Go to other windows ***************************************/
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
        FragmentManagerUser.setVisiblePage(p);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (Global.getVisiblePage().ordinal() < Global.visiblePage.OPEN_WALLET.ordinal()) {
                    CurvedBottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
                    bottomNavigationView.onMenuItemClick(Global.getVisiblePage().ordinal());
                }
            }
        }, 50);
    }
    @SuppressLint({"SourceLockedOrientationActivity", "ObsoleteSdkInt"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Instance = this;
        // At the moment force device to stay in portrait mode.
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1011);
        } else {
            System.setProperty("java.net.preferIPv4Stack" , "true");
        }
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
        } else {
            // Set initial price list.
            Global.setTokenPrice(new Pair<String, String>("LYR", "USD"), 0.00025f);
        }
        // On first launch.
        if (Global.getVisiblePage() == null) {
            // Show the Open Wallet page.
            FragmentManagerUser.setVisiblePage(Global.visiblePage.OPEN_WALLET);
        }
        UserInputTimeoutHandler = new Handler();
        int inactivity = Global.getInactivityTimeForClose();
        if (inactivity != -1) {
            startHandler(inactivity);
        }
        CurvedBottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        CbnMenuItem[] menuItems = new CbnMenuItem[]{
                /*new CbnMenuItem(
                        R.drawable.ic_outline_payments_24_, // the icon
                        R.drawable.ic_outline_payments_24, // the AVD that will be shown in FAB
                        Global.visiblePage.STAKING.ordinal() // optional if you use Jetpack Navigation
                ),*/
                new CbnMenuItem(
                        R.drawable.ic_outline_swap_horiz_24_,
                        R.drawable.ic_round_swap_horiz_24,
                        Global.visiblePage.SWAP.ordinal()
                ),
                new CbnMenuItem(
                        R.drawable.ic_outline_account_balance_wallet_24_,
                        R.drawable.ic_outline_account_balance_wallet_24,
                        Global.visiblePage.ACCOUNT.ordinal()
                ),
                /*new CbnMenuItem(
                        R.drawable.ic_outline_grid_view_24_,
                        R.drawable.ic_outline_grid_view_24,
                        Global.visiblePage.DEX.ordinal()
                ),*/
                new CbnMenuItem(
                        R.drawable.ic_outline_more_horiz_24_,
                        R.drawable.ic_baseline_more_horiz_24,
                        Global.visiblePage.MORE.ordinal()
                )
        };
        new Handler().postDelayed(new Runnable() {
            public void run() {
            }
        }, 100);
        if (Global.getVisiblePage().ordinal() < Global.visiblePage.OPEN_WALLET.ordinal()) {
            bottomNavigationView.setMenuItems(menuItems, Global.getVisiblePage().ordinal());
        } else {
            bottomNavigationView.setMenuItems(menuItems, Global.visiblePage.ACCOUNT.ordinal());
            bottomNavigationView.setVisibility(View.GONE);
        }
        bottomNavigationView.setOnMenuItemClickListener((CbnMenuItem cbnMenuItem, Integer index) -> {
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            FragmentManager.BackStackEntry entry =  fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1);
            //if(index != Integer.parseInt(entry.getName())) {
            if(PushToBackStack) { // If the back button is press, we need to ignore this event, the page is pop from tha backstack.
                FragmentManagerUser.setVisiblePage(Global.visiblePage.values()[index]);
            }
            PushToBackStack = true;
            return null;
        });
        FragmentManagerUser.setVisiblePage(Global.getVisiblePage());
        /*new WebHttps(this).execute("https://api.latoken.com/v2/ticker", "MainCallHttps1");
        new WebHttps(this).execute("https://api.latoken.com/v2/ticker", "MainCallHttps2");*/

        // Call get history/het prices every 120 seconds.
    }
    void restoreTimers() {
        timerHistory = new Timer();
        timerHistory.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new NetworkWebHttps(getInstance()).execute("https://api.latoken.com/v2/ticker/LYR/USDT", "MainCallHttpsLyrUsdtPair");
                runOnUiThread(new Runnable() {
                    public void run() {
                        new ApiRpc().act(new ApiRpc.Action().actionPool("LyrPriceInUSD", "LYR", "tether/USDT"));
                    }
                });
                /*if( Global.getSelectedAccountName().length() != 0) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            new ApiRpc().act(new ApiRpc.Action().actionHistory(Global.str_api_rpc_purpose_history_disk_storage,
                                    Global.getCurrentNetworkName(), Global.getSelectedAccountName(), Global.getSelectedAccountId()));
                        }
                    });
                }*/
            }
        }, 1000, 120 * 1000);
        timerBalance = new Timer();
        timerBalance.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if( Global.getSelectedAccountName().length() != 0) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            new ApiRpc().act(new ApiRpc.Action().actionBalance(Global.getSelectedAccountId()));
                        }
                    });
                }
            }
        }, 1000, 30 * 1000);
    }
    @Override
    protected void onResume() {
        super.onResume();
        //Toast.makeText(getApplicationContext(), "onResumed called", Toast.LENGTH_LONG).show();
        restoreTimers();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //Toast.makeText(getApplicationContext(), "onPause called", Toast.LENGTH_LONG).show();
        timerBalance.cancel();
        timerHistory.cancel();
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
        if(instance.getInstanceName().equals("MainCallHttpsLyrUsdtPair")) {
            try {
                JSONObject obj = new JSONObject(instance.getContent());
                Global.setTokenPrice(new Pair<String, String>("LYR", "USD"), Double.parseDouble(obj.getString("lastPrice")));
            } catch (JSONException | NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
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
        for(Fragment fragment : fragments){
            if(fragment != null && fragment.isVisible())
                return fragment;
        }
        return null;
    }
    public static void goBack() {
        FragmentManager fragmentManager = getInstance().getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
            FragmentManager.BackStackEntry entry =  fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 2);
            CurvedBottomNavigationView bottomNavigationView = getInstance().findViewById(R.id.bottomNavigationView);
            Global.setVisiblePage(Global.visiblePage.values()[Integer.parseInt(Objects.requireNonNull(entry.getName()))]);
            if (Global.getVisiblePage().ordinal() < Global.visiblePage.OPEN_WALLET.ordinal()) {
                // If not visible, we will return to the same position, select event will not be triggered, so we need to let "PushToBackStack" ad true;
                if(bottomNavigationView.getVisibility() == View.VISIBLE) {
                    PushToBackStack = false;
                }
                bottomNavigationView.onMenuItemClick(Global.getVisiblePage().ordinal());
            } else {
                bottomNavigationView.setVisibility(View.GONE);
            }
        } else {
            //getInstance().finish();
            //System.exit(0);
        }
    }
    @Override
    public boolean onKeyDown(int key_code, KeyEvent key_event) {
        if (key_code== KeyEvent.KEYCODE_BACK) {
            // Prevent back key to take effect, implemented for further development.
            super.onKeyDown(key_code, key_event);
            goBack();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1011) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Here user granted the permission
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(MainActivity.this, "Permission denied to read your Camera", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // Need to be in main activity, don't work elsewhere, so I put them here.
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
                            new FragmentManagerUser().goToOpenWallet();
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
                                    double amount = 0f;
                                    if(!obj.isNull("amount")) {
                                        amount = Double.parseDouble(obj.getString("amount"));
                                    }
                                    if(!obj.isNull("ticker")) {
                                        String ticker = obj.getString("ticker");
                                        Spinner tokenSpinner = (Spinner) findViewById(R.id.sendTokenSelectSpinner);
                                        if(tokenSpinner != null) {
                                            SpinnerAdapter adapter = tokenSpinner.getAdapter();
                                            List<Pair<String, Double>> balances = UtilGetData.getAvailableTokenList();
                                            for (Pair<String, Double> t: balances) {
                                                if(t.first.equals(ticker)) {
                                                    double max = t.second;
                                                    if(ticker.equals("LYR")) {
                                                        max -= GlobalLyra.LYRA_TX_FEE;
                                                    }
                                                    if(amount > max) {
                                                        EditText tokenAmountEditText = (EditText) findViewById(R.id.send_token_amount_value);
                                                        if(tokenAmountEditText != null) {
                                                            tokenAmountEditText.setError(getString(R.string.main_activity_not_enough_tokens_into_account));
                                                        }
                                                    }
                                                    break;
                                                }
                                            }
                                            int i = 0;
                                            for (; i < adapter.getCount(); i++) {
                                                if(ticker.equals(adapter.getItem(i).toString())) {
                                                    tokenSpinner.setSelection(i);
                                                    break;
                                                }
                                            }
                                            EditText tokenAmountEditText = (EditText) findViewById(R.id.send_token_amount_value);
                                            if(tokenAmountEditText != null) {
                                                if(i != adapter.getCount()) {
                                                    tokenAmountEditText.setText(String.format(Locale.US, "%f", amount));
                                                } else { // Token not found in current account.
                                                    tokenAmountEditText.setError(getString(R.string.main_activity_token_not_found_in_current_account));
                                                    tokenAmountEditText.setText(String.format(Locale.US, "%f", 0f));
                                                }
                                            }
                                            System.out.println(adapter);
                                        }
                                    }
                                } catch (JSONException | NumberFormatException e) {
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