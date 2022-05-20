package com.lyrawallet.Ui.FragmentSwap;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.lyrawallet.Api.ApiRpc;
import com.lyrawallet.Api.ApiRpcActions.ApiRpcActionsHistory;
import com.lyrawallet.Api.ApiRpcActions.ApiRpcActionsPairPrice;
import com.lyrawallet.Api.ApiWebActions.ApiNode;
import com.lyrawallet.Api.Network.NetworkRpc;
import com.lyrawallet.Api.Network.NetworkWebHttps;
import com.lyrawallet.Global;
import com.lyrawallet.GlobalLyra;
import com.lyrawallet.MainActivity;
import com.lyrawallet.R;
import com.lyrawallet.Ui.FragmentAccountHistory.AccountHistoryGalleryAdapter;
import com.lyrawallet.Ui.TokensSpinnerAdapter;
import com.lyrawallet.Ui.UiDialog;
import com.lyrawallet.Ui.UiHelpers;
import com.lyrawallet.Ui.UiUpdates;
import com.lyrawallet.Ui.UtilGetData;
import com.lyrawallet.Util.Concatenate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import np.com.susanthapa.curved_bottom_navigation.CurvedBottomNavigationView;

public class FragmentSwap extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    int fromSelect = 0;
    String toSelect = "";
    static boolean poolSelected = false;
    static boolean fromLastEditTextChanged = true;
    private final Handler UserInputValueChangedHandler = new Handler();
    View thisView = null;
    UiUpdates.PoolData PoolData = null;
    UiUpdates.PoolCalculateData PoolCalculateData = null;
    double InternalLyrPrice = 0f;
    Timer timer1;
    Timer timer2;

    public FragmentSwap() {
        // Required empty public constructor
    }

    public static FragmentSwap newInstance(String param1, String param2) {
        FragmentSwap fragment = new FragmentSwap();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /*private static void setPoolValuesVisibility(Activity activity) {
        Spinner tokenFromSpinner = (Spinner) activity.findViewById(R.id.swapFromValueSpinner);
        Spinner tokenToSpinner = (Spinner) activity.findViewById(R.id.swapToValueSpinner);
        EditText swapFromValueEditText = (EditText) activity.findViewById(R.id.swapFromValueEditText);
        if(tokenFromSpinner == null || tokenToSpinner == null || swapFromValueEditText == null)
            return;
        try {
            Double.parseDouble(swapFromValueEditText.getText().toString());
            if(tokenFromSpinner.getSelectedItem().toString().length() == 0 || tokenToSpinner.getSelectedItem().toString().length() == 0)
                setPoolValuesVisibility(activity, View.GONE);
            else
                setPoolValuesVisibility(activity, View.VISIBLE);
        } catch (NumberFormatException e) {
            setPoolValuesVisibility(activity, View.GONE);
        }
    }*/

    public static void setPoolValuesVisibility(Activity activity, int visibility) {
        if(poolSelected) {
            return;
        }
        try {
            TextView swapTokenPairTextView = (TextView) activity.findViewById(R.id.swapTokenPairTextView);
            TextView swapEstimatedRatioTextView = (TextView) activity.findViewById(R.id.swapEstimatedRatioTextView);
            TextView swapYouWillSellTextView = (TextView) activity.findViewById(R.id.swapYouWillSellTextView);
            TextView swapYouWillGetTextView = (TextView) activity.findViewById(R.id.swapYouWillGetTextView);
            TextView swapPriceImpactTextView = (TextView) activity.findViewById(R.id.swapPriceImpactTextView);
            TextView swapPoolFeeTextView = (TextView) activity.findViewById(R.id.swapPoolFeeTextView);
            TextView swapNetworkFeeTextView = (TextView) activity.findViewById(R.id.swapNetworkFeeTextView);

            swapTokenPairTextView.setVisibility(visibility);
            swapEstimatedRatioTextView.setVisibility(visibility);
            swapYouWillSellTextView.setVisibility(visibility);
            swapYouWillGetTextView.setVisibility(visibility);
            swapPriceImpactTextView.setVisibility(visibility);
            swapPoolFeeTextView.setVisibility(visibility);
            swapNetworkFeeTextView.setVisibility(visibility);

            TextView swapTokenPairValueTextView = (TextView) activity.findViewById(R.id.swapTokenPairValueTextView);
            TextView swapEstimatedRatioValueTextView = (TextView) activity.findViewById(R.id.swapEstimatedRatioValueTextView);
            TextView swapYouWillSellValueTextView = (TextView) activity.findViewById(R.id.swapYouWillSellValueTextView);
            TextView swapYouWillGetValueTextView = (TextView) activity.findViewById(R.id.swapYouWillGetValueTextView);
            TextView swapPriceImpactValueTextView = (TextView) activity.findViewById(R.id.swapPriceImpactValueTextView);
            TextView swapPoolFeeValueTextView = (TextView) activity.findViewById(R.id.swapPoolFeeValueTextView);
            TextView swapNetworkFeeValueTextView = (TextView) activity.findViewById(R.id.swapNetworkFeeValueTextView);

            swapTokenPairValueTextView.setVisibility(visibility);
            swapEstimatedRatioValueTextView.setVisibility(visibility);
            swapYouWillSellValueTextView.setVisibility(visibility);
            swapYouWillGetValueTextView.setVisibility(visibility);
            swapPriceImpactValueTextView.setVisibility(visibility);
            swapPoolFeeValueTextView.setVisibility(visibility);
            swapNetworkFeeValueTextView.setVisibility(visibility);
        } catch (NullPointerException ignored) { }
    }

    public static void setSwapValues(Activity activity, String from, String to, double price, double sellValue, double buyValue, double priceImpact, double poolFee, double networkFee) {
        if(activity == null)
            return;
        Spinner tokenFromSpinner = (Spinner) activity.findViewById(R.id.swapFromValueSpinner);
        Spinner tokenToSpinner = (Spinner) activity.findViewById(R.id.swapToValueSpinner);
        try {
            if (!tokenFromSpinner.getSelectedItem().toString().equals(GlobalLyra.domainToSymbol(from)) ||
                    !tokenToSpinner.getSelectedItem().toString().equals(GlobalLyra.domainToSymbol(to))) {
                // if there are two requests for two different token pairs, make sure that the display will not be changed if the response is for different pair.
                return;
            }
        } catch (NullPointerException ignored) {
            return;
        }

        try {
            EditText swapToValueEditText = (EditText) activity.findViewById(R.id.swapToValueEditText);

            TextView swapTokenPairValueTextView = (TextView) activity.findViewById(R.id.swapTokenPairValueTextView);
            TextView swapEstimatedRatioValueTextView = (TextView) activity.findViewById(R.id.swapEstimatedRatioValueTextView);
            TextView swapYouWillSellValueTextView = (TextView) activity.findViewById(R.id.swapYouWillSellValueTextView);
            TextView swapYouWillGetValueTextView = (TextView) activity.findViewById(R.id.swapYouWillGetValueTextView);
            TextView swapPriceImpactValueTextView = (TextView) activity.findViewById(R.id.swapPriceImpactValueTextView);
            TextView swapPoolFeeValueTextView = (TextView) activity.findViewById(R.id.swapPoolFeeValueTextView);
            TextView swapNetworkFeeValueTextView = (TextView) activity.findViewById(R.id.swapNetworkFeeValueTextView);

            swapToValueEditText.setText(String.format(Locale.US, "%.8f", buyValue));

            swapTokenPairValueTextView.setText(String.format(Locale.US, "%s vs %s", GlobalLyra.domainToSymbol(from), GlobalLyra.domainToSymbol(to)));
            swapEstimatedRatioValueTextView.setText(String.format(Locale.US, "%.8f %s per %s", price, GlobalLyra.domainToSymbol(from), GlobalLyra.domainToSymbol(to)));
            swapYouWillSellValueTextView.setText(String.format(Locale.US, "%.8f %s", sellValue, GlobalLyra.domainToSymbol(from)));
            swapYouWillGetValueTextView.setText(String.format(Locale.US, "%.8f %s", buyValue, GlobalLyra.domainToSymbol(to)));
            swapPriceImpactValueTextView.setText(String.format(Locale.US, "%.8f %%", priceImpact * 100));
            swapPoolFeeValueTextView.setText(String.format(Locale.US, "%.3f %s", poolFee, GlobalLyra.domainToSymbol(from)));
            swapNetworkFeeValueTextView.setText(String.format(Locale.US, "%.3f LYR", networkFee));
            ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.swapProgressBar);
            progressBar.setVisibility(View.GONE);
            setPoolValuesVisibility(activity, View.VISIBLE);
        } catch (NullPointerException ignored) { }
    }

    public static void setProgressBarVisibility(Activity activity, int visibility) {
        try {
            ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.swapProgressBar);
            if (progressBar != null) {
                progressBar.setVisibility(visibility);
            }
        } catch (NullPointerException ignored) { }
    }

    public void stopHandler() {
        UserInputValueChangedHandler.removeCallbacks(UserInputTimeoutRunnable);
    }
    public void startHandler(int inactivityTime) {
        UserInputValueChangedHandler.postDelayed(UserInputTimeoutRunnable, (long) inactivityTime);
    }
    private final Runnable UserInputTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            stopHandler();
            try {
                Activity activity = getActivity();
                if(activity == null)
                    return;
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            Spinner tokenFromSpinner = (Spinner) activity.findViewById(R.id.swapFromValueSpinner);
                            Spinner tokenToSpinner = (Spinner) activity.findViewById(R.id.swapToValueSpinner);
                            EditText toAmountEditText = (EditText) activity.findViewById(R.id.swapToValueEditText);
                            if (tokenFromSpinner == null || tokenToSpinner == null || toAmountEditText == null) {
                                setProgressBarVisibility(activity, View.GONE);
                                setPoolValuesVisibility(activity, View.GONE);
                                if (toAmountEditText != null) {
                                    toAmountEditText.setText("");
                                }
                                return;
                            }
                            if (tokenFromSpinner.getSelectedItem() == null || tokenToSpinner.getSelectedItem() == null) {
                                setProgressBarVisibility(activity, View.GONE);
                                setPoolValuesVisibility(activity, View.GONE);
                                toAmountEditText.setText("");
                                TextView swapTotalLiquidityValueTextView = (TextView) activity.findViewById(R.id.swapTotalLiquidityValueTextView);
                                if (swapTotalLiquidityValueTextView != null) {
                                    swapTotalLiquidityValueTextView.setText("");
                                }
                                return;
                            }
                            String from = GlobalLyra.symbolToDomain(tokenFromSpinner.getSelectedItem().toString());
                            String to = GlobalLyra.symbolToDomain(tokenToSpinner.getSelectedItem().toString());
                            if (from.length() == 0 || to.length() == 0 ||
                                    tokenFromSpinner.getSelectedItem().toString().length() == 0 ||
                                    tokenToSpinner.getSelectedItem().toString().length() == 0) {
                                setProgressBarVisibility(activity, View.GONE);
                                setPoolValuesVisibility(activity, View.GONE);
                                toAmountEditText.setText("");
                                TextView swapTotalLiquidityValueTextView = (TextView) activity.findViewById(R.id.swapTotalLiquidityValueTextView);
                                if (swapTotalLiquidityValueTextView != null) {
                                    swapTotalLiquidityValueTextView.setText("");
                                }
                                return;
                            }
                            ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.swapProgressBar);
                            progressBar.setVisibility(View.VISIBLE);
                            /********************** Send request for Pool ************************/
                            NetworkRpc rpc = (NetworkRpc) new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL)
                                    .execute("", "Pool", GlobalLyra.symbolToDomain(from), GlobalLyra.symbolToDomain(to));
                            rpc.setListener(new NetworkRpc.RpcTaskListener() {
                                @Override
                                public void onRpcTaskFinished(String[] output) {
                                    activity.runOnUiThread(new Runnable() {
                                        public void run() {
                                            System.out.println("RPC POOL: " + output[0] + output[1] + output[2]);
                                            if (output[2].equals("error")) {
                                                UiUpdates.setPoolData(null);
                                                updatePoolLiquidity(getView());
                                                ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.swapProgressBar);
                                                progressBar.setVisibility(View.GONE);
                                            } else {
                                                try {
                                                    UiUpdates.setPoolData(new UiUpdates.PoolData(output[2]));
                                                    updatePoolLiquidity(getView());
                                                    try {
                                                        try {
                                                            EditText swapFromValueEditText = (EditText) activity.findViewById(R.id.swapFromValueEditText);
                                                            if (swapFromValueEditText.getText().toString().length() == 0) {
                                                                ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.swapProgressBar);
                                                                progressBar.setVisibility(View.GONE);
                                                            } else {
                                                                Spinner tokenFromSpinner = (Spinner) activity.findViewById(R.id.swapFromValueSpinner);
                                                                String from = GlobalLyra.symbolToDomain(tokenFromSpinner.getSelectedItem().toString());
                                                                /********************* Send request for PoolCalculate ***********************/
                                                                NetworkRpc rpc = (NetworkRpc) new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL)
                                                                        .execute("", "PoolCalculate",
                                                                                UiUpdates.getPoolData().getPoolId(), GlobalLyra.symbolToDomain(from), swapFromValueEditText.getText().toString(), String.valueOf(0.01f));
                                                                rpc.setListener(new NetworkRpc.RpcTaskListener() {
                                                                    @Override
                                                                    public void onRpcTaskFinished(String[] output) {
                                                                        activity.runOnUiThread(new Runnable() {
                                                                            public void run() {
                                                                                System.out.println("RPC POOL CALCULATE: " + output[0] + output[1] + output[2]);
                                                                                if (output[2].equals("error")) {
                                                                                    UiUpdates.setPoolCalculateData(null);
                                                                                    ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.swapProgressBar);
                                                                                    progressBar.setVisibility(View.GONE);
                                                                                } else {
                                                                                    try {
                                                                                        UiUpdates.setPoolCalculateData(new UiUpdates.PoolCalculateData(output[2]));
                                                                                        if (Global.getDebugEnabled())
                                                                                            Toast.makeText(activity, "\"PoolCalculate\" fetch done", Toast.LENGTH_SHORT).show();
                                                                                    } catch (JSONException | NullPointerException e) {
                                                                                        Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
                                                                                        e.printStackTrace();
                                                                                    }
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                        } catch (NullPointerException ignored) { }
                                                    } catch (NumberFormatException e) {
                                                        FragmentSwap.setProgressBarVisibility(activity, View.GONE);
                                                        e.printStackTrace();
                                                    }
                                                    if (Global.getDebugEnabled())
                                                        Toast.makeText(activity, "\"Pool\" fetch done", Toast.LENGTH_SHORT).show();
                                                } catch (JSONException e) {
                                                    Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    });
                                }
                            });
                        } catch (NullPointerException ignored) { }
                    }
                });
                NetworkWebHttps webHttpsTask = new NetworkWebHttps();
                webHttpsTask.setListener(new NetworkWebHttps.WebHttpsTaskListener() {
                    @Override
                    public void onWebHttpsTaskFinished(NetworkWebHttps instance) {
                        System.out.println("SHARE IN POOL: " + instance.getContent());
                        //JSONObject obj0 = new JSONObject(instance.getContent());
                        ApiNode.GetPool pool = new ApiNode.GetPool().fromJson(instance.getContent());
                        double accShareValue = 0l;
                        for (Pair<String, Double> share: pool.getShares()) {
                            if(share.first.equals(Global.getSelectedAccountId()))
                                accShareValue = share.second;
                        }
                        double accSharePercent = accShareValue;
                        TextView swapShareValueTextView = (TextView) activity.findViewById(R.id.swapShareValueTextView);
                        swapShareValueTextView.setText(String.format(Locale.US, "%.4f %%", accSharePercent));
                    }
                });
                Spinner tokenFromSpinner = (Spinner) activity.findViewById(R.id.swapFromValueSpinner);
                Spinner tokenToSpinner = (Spinner) activity.findViewById(R.id.swapToValueSpinner);
                if(tokenFromSpinner != null && tokenToSpinner != null && tokenFromSpinner.getSelectedItem() != null && tokenToSpinner.getSelectedItem() != null) {
                    webHttpsTask.execute("https://" + Global.getNodeAddress() + GlobalLyra.LYRA_NODE_API_URL + "/GetPool/?token0=" +
                            GlobalLyra.symbolToDomain(tokenFromSpinner.getSelectedItem().toString()) + "&token1=" +
                            GlobalLyra.symbolToDomain(tokenToSpinner.getSelectedItem().toString()));
                }
            } catch (NullPointerException ignored) { }
        }
    };

    private void refreshAvailableTokens(View view) {
        Activity activity = getActivity();
        if(activity == null)
            return;
        Spinner tokenFromSpinner = (Spinner) view.findViewById(R.id.swapFromValueSpinner);
        List<Pair<String, Double>> list = UtilGetData.getAvailableTokenList();
        if(tokenFromSpinner.getSelectedItem() == null)
            return;
        for (int ii = 0; ii < list.size(); ii++) {
            if (GlobalLyra.domainToSymbol(list.get(ii).first).equals(tokenFromSpinner.getSelectedItem().toString())) {
                TextView fromAmountTextView = (TextView) view.findViewById(R.id.swapFromBalanceTextView);
                fromAmountTextView.setText(String.format(Locale.US, "%s: %.8f", activity.getString(R.string.Balance), list.get(ii).second));
                break;
            }
        }
        int ii = 0;
        Spinner tokenToSpinner = (Spinner) view.findViewById(R.id.swapToValueSpinner);
        TextView toAmountTextView = (TextView) view.findViewById(R.id.swapToBalanceTextView);
        try {
            for (; ii < list.size(); ii++) {
                if (GlobalLyra.domainToSymbol(list.get(ii).first).equals(tokenToSpinner.getSelectedItem().toString())) {
                    toAmountTextView.setText(String.format(Locale.US, "%s: %.8f", activity.getString(R.string.Balance), list.get(ii).second));
                    break;
                }
            }
            if (ii == list.size()) {
                toAmountTextView.setText(String.format(Locale.US, "%s: %.8f", activity.getString(R.string.Balance), 0f));
            }
        } catch (NullPointerException ignored) { }
    }

    private void populateSpinners(View view, boolean forPool) {
        Spinner tokenFromSpinner = (Spinner) view.findViewById(R.id.swapFromValueSpinner);
        if(fromSelect == tokenFromSpinner.getSelectedItemPosition()) {
            return;
        }
        fromSelect = tokenFromSpinner.getSelectedItemPosition();
        Spinner tokenToSpinner = (Spinner) view.findViewById(R.id.swapToValueSpinner);
        if(tokenToSpinner.getSelectedItem() != null) {
            toSelect = tokenToSpinner.getSelectedItem().toString();
        }
        View v = view;
        List<String> tickerList = new ArrayList<>();
        List<Pair<String, Double>> list = UtilGetData.getAvailableTokenList();
        for (int i = 0; i < list.size(); i++) {
            tickerList.add(GlobalLyra.domainToSymbol(list.get(i).first));
        }
        TokensSpinnerAdapter adapter = new TokensSpinnerAdapter(view.getContext(), R.layout.entry_send_token_select_spinner,
                tickerList.toArray(new String[0]), UiHelpers.tickerToImage(tickerList).toArray(new Integer[0]));
        adapter.setDropDownViewResource(R.layout.entry_send_token_select_spinner_first);
        tokenFromSpinner.setAdapter(adapter);
        tokenFromSpinner.setSelection(fromSelect);
        tokenFromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Activity activity = getActivity();
                if(activity == null)
                    return;
                setPoolValuesVisibility(activity, View.GONE);
                populateSpinners(v, forPool);
                startHandler(100);
                refreshAvailableTokens(v);
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        tickerList.clear();
        if(forPool) {
            for (int i = 0; i < list.size(); i++) {
                if (!GlobalLyra.domainToSymbol(list.get(i).first).equals(tokenFromSpinner.getSelectedItem().toString())) {
                    tickerList.add(GlobalLyra.domainToSymbol(list.get(i).first));
                }
            }
        } else {
            for (int i = 0; i < GlobalLyra.TickerIconList.length; i++) {
                if (tokenFromSpinner.getSelectedItem() != null) {
                    if (i != 1 && !GlobalLyra.TickerIconList[i].first.equals(tokenFromSpinner.getSelectedItem().toString())) {
                        tickerList.add(GlobalLyra.TickerIconList[i].first);
                    }
                }
            }
        }
        adapter = new TokensSpinnerAdapter(view.getContext(), R.layout.entry_send_token_select_spinner,
                tickerList.toArray(new String[0]), UiHelpers.tickerToImage(tickerList).toArray(new Integer[0]), 2);
        adapter.setDropDownViewResource(R.layout.entry_send_token_select_spinner_first);
        tokenToSpinner.setAdapter(adapter);
        for (int i = 0; i < tokenToSpinner.getCount(); i++) {
            if(tokenToSpinner.getItemAtPosition(i).toString().equals(toSelect)) {
                tokenToSpinner.setSelection(i);
                break;
            }
        }
        tokenToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Activity activity = getActivity();
                if(activity == null)
                    return;
                setPoolValuesVisibility(activity, View.GONE);
                startHandler(100);
                int ii = 0;
                refreshAvailableTokens(v);
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        for(int i = 0; i < adapter.getCount(); i++) {
            if(adapter.getItem(i).equals(toSelect)) {
                tokenToSpinner.setSelection(i);
            }
        }
    }

    void updatePoolLiquidity(View view) {
        Activity activity = getActivity();
        if(activity == null)
            return;
        try {
            UiUpdates.PoolData poolData = UiUpdates.getPoolData();
            boolean changed = true;
            if(poolData != null) {
                if(PoolData != null) {
                    if(poolData.getLastUpdated() == PoolData.getLastUpdated()) {
                        changed = false;
                    }
                }
            } else {
                setProgressBarVisibility(activity, View.GONE);
                setPoolValuesVisibility(activity, View.GONE);
                TextView swapTotalLiquidityValueTextView = (TextView) activity.findViewById(R.id.swapTotalLiquidityValueTextView);
                swapTotalLiquidityValueTextView.setText("");
            }
            if(changed) {
                PoolData = poolData;
                if (PoolData != null) {
                    TextView swapTotalLiquidityValueTextView = (TextView) activity.findViewById(R.id.swapTotalLiquidityValueTextView);
                    if (swapTotalLiquidityValueTextView != null) {
                        swapTotalLiquidityValueTextView.setText(String.format(Locale.US, "%.8f %s\n%.8f %s", PoolData.getToken0Balance(), GlobalLyra.domainToSymbol(PoolData.getToken0()),
                                PoolData.getToken1Balance(), GlobalLyra.domainToSymbol(PoolData.getToken1())));
                    }
                }
            }
        } catch (NullPointerException ignored) { }
    }

    void restoreTimers(View view) {
        Activity activity = getActivity();
        if(activity == null)
            return;
        timer1 = new Timer();
        timer1.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run () {
                startHandler(100);
                ApiRpcActionsPairPrice.set(activity, "LYR", "tether/USDT");
            }
        },100,60 * 1000);
        timer2 = new Timer();
        timer2.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run () {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        populateSpinners(view, poolSelected);
                        TextView swapExternalPriceValueTextView = (TextView) view.findViewById(R.id.swapExternalPriceValueTextView);
                        swapExternalPriceValueTextView.setText(String.format(Locale.US, "$ %.8f", Global.getTokenPrice(new Pair<>("LYR", "USD"))));
                        double internalLyrPrice = Global.getTokenPrice(new Pair<>("LYR", "tether/USDT"));
                        if(InternalLyrPrice != internalLyrPrice) {
                            InternalLyrPrice = internalLyrPrice;
                            try {
                                TextView swapInternalPriceValueTextView = (TextView) activity.findViewById(R.id.swapInternalPriceValueTextView);
                                swapInternalPriceValueTextView.setText(String.format(Locale.US, "$ %.8f", internalLyrPrice));
                                RecyclerView account_history_recycler = activity.findViewById(R.id.accountHistoryRecycler);
                                if((AccountHistoryGalleryAdapter)account_history_recycler.getAdapter() != null)
                                    ((AccountHistoryGalleryAdapter)account_history_recycler.getAdapter()).updateUnitPrice("LYR");
                            } catch (NullPointerException ignored) { }
                        }
                        updatePoolLiquidity(view);
                        try {
                            UiUpdates.PoolCalculateData poolCalculateData = UiUpdates.getPoolCalculateData();
                            boolean changed = true;
                            if(poolCalculateData != null && PoolCalculateData != null) {
                                if (poolCalculateData.getLastUpdated() == PoolCalculateData.getLastUpdated()) {
                                    changed = false;
                                }
                            }
                            if(changed) {
                                PoolCalculateData = poolCalculateData;
                                if (PoolCalculateData != null) {
                                    Spinner tokenFromSpinner = (Spinner) view.findViewById(R.id.swapFromValueSpinner);
                                    Spinner tokenToSpinner = (Spinner) view.findViewById(R.id.swapToValueSpinner);
                                    if(GlobalLyra.domainToSymbol(poolCalculateData.getSwapInToken()).equals(tokenFromSpinner.getSelectedItem().toString()) &&
                                            GlobalLyra.domainToSymbol(poolCalculateData.getSwapOutToken()).equals(tokenToSpinner.getSelectedItem().toString())) {
                                        if (poolSelected) {
                                            double multiplier = PoolData.getToken1Balance() / PoolData.getToken0Balance();
                                            if(PoolCalculateData.getSwapInToken().equals(PoolData.getToken1()))
                                                multiplier = PoolData.getToken0Balance() / PoolData.getToken1Balance();
                                            setSwapValues(activity, PoolCalculateData.getSwapInToken(), PoolCalculateData.getSwapOutToken(),
                                                    PoolCalculateData.getPrice(), PoolCalculateData.getSwapInAmount(), PoolCalculateData.getSwapInAmount() * (multiplier),
                                                    PoolCalculateData.getPriceImpact(), PoolCalculateData.getPayToProvider(), PoolCalculateData.getPayToAuthorizer());

                                        } else {
                                            setSwapValues(activity, PoolCalculateData.getSwapInToken(), PoolCalculateData.getSwapOutToken(),
                                                    PoolCalculateData.getPrice(), PoolCalculateData.getSwapInAmount(), PoolCalculateData.getSwapOutAmount(),
                                                    PoolCalculateData.getPriceImpact(), PoolCalculateData.getPayToProvider(), PoolCalculateData.getPayToAuthorizer());
                                        }
                                    }
                                }
                            }
                        } catch (NullPointerException ignored) { }
                        refreshAvailableTokens(view);
                    }
                });
            }
        },3000,3000);
    }

    public static void clearAccountFromTo(Activity activity) {
        try {
            EditText fromAmountEditText = (EditText) activity.findViewById(R.id.swapFromValueEditText);
            EditText toAmountEditText = (EditText) activity.findViewById(R.id.swapToValueEditText);
            fromAmountEditText.setText("");
            toAmountEditText.setText("");
        } catch (NullPointerException ignore) { }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_swap, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(getActivity(), "onResumed called", Toast.LENGTH_LONG).show();
        restoreTimers(getView());
    }
    @Override
    public void onPause() {
        super.onPause();
        //Toast.makeText(getActivity(), "onPause called", Toast.LENGTH_LONG).show();
        timer1.cancel();
        timer2.cancel();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        thisView = view;
        poolSelected = false;
        Activity activity = getActivity();
        if(activity == null)
            return;
        CurvedBottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);

        TextView swapAccountNameTextView = view.findViewById(R.id.swapAccountNameTextView);
        swapAccountNameTextView.setText(String.format("%s/%s", Global.getSelectedAccountName(), Global.getCurrentNetworkName()));

        setProgressBarVisibility(activity, View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                ApiRpcActionsHistory.load(Concatenate.getHistoryFileName());
                View v = new View((MainActivity) activity);
                populateSpinners(view, poolSelected);
                //setProgressBarVisibility(activity, View.GONE);
            }
        }, 1000);

        Button swapButton = (Button) view.findViewById(R.id.swapModeSwapButton);
        Button poolButton = (Button) view.findViewById(R.id.swapModePoolButton);
        swapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setPoolValuesVisibility(activity, View.GONE);
                poolSelected = false;
                fromSelect = -1;
                TextView swapHeaderTextView = (TextView) view.findViewById(R.id.swapHeaderTextView);
                swapHeaderTextView.setText(activity.getString(R.string.Swap));
                EditText fromAmountEditText = (EditText) view.findViewById(R.id.swapFromValueEditText);
                EditText toAmountEditText = (EditText) view.findViewById(R.id.swapToValueEditText);
                fromAmountEditText.setText("");
                fromAmountEditText.setHint(activity.getString(R.string.Amount));
                toAmountEditText.setText("");
                toAmountEditText.setHint(activity.getString(R.string.swap_token_amount_to_receive_hint));
                populateSpinners(view, false);
                swapButton.setEnabled(false);
                poolButton.setEnabled(true);
                Button swapButton = (Button) view.findViewById(R.id.swapSwapActionButton);
                swapButton.setText(activity.getText(R.string.Swap));
                Button swapRemoveShareActionButton = (Button) view.findViewById(R.id.swapRemoveShareActionButton);
                swapRemoveShareActionButton.setVisibility(View.GONE);

                TextView swapExternalPriceTextView = (TextView) view.findViewById(R.id.swapExternalPriceTextView);
                TextView swapInternalPriceTextView = (TextView) view.findViewById(R.id.swapInternalPriceTextView);
                swapExternalPriceTextView.setVisibility(View.VISIBLE);
                swapInternalPriceTextView.setVisibility(View.VISIBLE);
                TextView swapExternalPriceValueTextView = (TextView) view.findViewById(R.id.swapExternalPriceValueTextView);
                TextView swapInternalPriceValueTextView = (TextView) view.findViewById(R.id.swapInternalPriceValueTextView);
                swapExternalPriceValueTextView.setVisibility(View.VISIBLE);
                swapInternalPriceValueTextView.setVisibility(View.VISIBLE);
                startHandler(100);
            }
        });
        poolButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                fromSelect = -1;
                TextView swapHeaderTextView = (TextView) view.findViewById(R.id.swapHeaderTextView);
                swapHeaderTextView.setText(activity.getString(R.string.Pool));
                setPoolValuesVisibility(activity, View.GONE);
                poolSelected = true;
                EditText fromAmountEditText = (EditText) view.findViewById(R.id.swapFromValueEditText);
                EditText toAmountEditText = (EditText) view.findViewById(R.id.swapToValueEditText);
                fromAmountEditText.setText("");
                fromAmountEditText.setHint(activity.getString(R.string.swap_amount_of_token));
                toAmountEditText.setText("");
                toAmountEditText.setHint(activity.getString(R.string.swap_amount_of_token));
                populateSpinners(view, true);
                swapButton.setEnabled(true);
                poolButton.setEnabled(false);
                Button swapButton = (Button) view.findViewById(R.id.swapSwapActionButton);
                swapButton.setText(activity.getText(R.string.swap_pool_add_liquidity));
                Button swapRemoveShareActionButton = (Button) view.findViewById(R.id.swapRemoveShareActionButton);
                swapRemoveShareActionButton.setVisibility(View.VISIBLE);

                TextView swapExternalPriceTextView = (TextView) view.findViewById(R.id.swapExternalPriceTextView);
                TextView swapInternalPriceTextView = (TextView) view.findViewById(R.id.swapInternalPriceTextView);
                swapExternalPriceTextView.setVisibility(View.GONE);
                swapInternalPriceTextView.setVisibility(View.GONE);

                TextView swapExternalPriceValueTextView = (TextView) view.findViewById(R.id.swapExternalPriceValueTextView);
                TextView swapInternalPriceValueTextView = (TextView) view.findViewById(R.id.swapInternalPriceValueTextView);
                swapExternalPriceValueTextView.setVisibility(View.GONE);
                swapInternalPriceValueTextView.setVisibility(View.GONE);
                startHandler(100);
            }
        });

        EditText swapFromValueEditText = (EditText) view.findViewById(R.id.swapFromValueEditText);
        if(swapFromValueEditText != null) {
            swapFromValueEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                }
                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    if(s.length() > 0) {
                        try {
                            Double.parseDouble(s.toString());
                            //if(fromLastEditTextChanged) {
                                stopHandler();
                                startHandler(2000);
                            //}
                            setPoolValuesVisibility(activity, View.GONE);
                            fromLastEditTextChanged = true;
                        } catch (NumberFormatException e) {
                            swapFromValueEditText.setText(s.subSequence(0, before - 1));
                        }
                    }
                }
            });
        }
        EditText swapToValueEditText = (EditText) view.findViewById(R.id.swapToValueEditText);
        if(swapToValueEditText != null) {
            swapToValueEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                }
                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    if(s.length() > 0) {
                        try {
                            Double.parseDouble(s.toString());
                            /*if(!fromLastEditTextChanged) {
                                startHandler(2000);
                            }*/
                            fromLastEditTextChanged = false;
                        } catch (NumberFormatException e) {
                            EditText swapToValueEditText = (EditText) view.findViewById(R.id.swapToValueEditText);
                            swapToValueEditText.setText(s.subSequence(0, before - 1));
                        }
                    }
                }
            });
        }
        Button swapSwapActionButton = (Button) view.findViewById(R.id.swapSwapActionButton);
        Button swapRemoveShareActionButton = (Button) view.findViewById(R.id.swapRemoveShareActionButton);
        swapRemoveShareActionButton.setVisibility(View.GONE);
        swapSwapActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    UiDialog.showDialogStatus(R.string.send_sending);
                    Spinner tokenFromSpinner = (Spinner) view.findViewById(R.id.swapFromValueSpinner);
                    Spinner tokenToSpinner = (Spinner) view.findViewById(R.id.swapToValueSpinner);
                    EditText swapFromValueEditText = (EditText) view.findViewById(R.id.swapFromValueEditText);
                    EditText swapToValueEditText = (EditText) view.findViewById(R.id.swapToValueEditText);
                    if(poolSelected) {
                        NetworkRpc rpc = (NetworkRpc) new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL, Global.getWalletPassword())
                                .execute("", "AddLiquidaty", Global.getSelectedAccountId(),
                                        GlobalLyra.symbolToDomain(tokenFromSpinner.getSelectedItem().toString()), swapFromValueEditText.getText().toString(),
                                        GlobalLyra.symbolToDomain(tokenToSpinner.getSelectedItem().toString()), swapToValueEditText.getText().toString()
                                );
                        rpc.setListener(new NetworkRpc.RpcTaskListener() {
                            @Override
                            public void onRpcTaskFinished(String[] output) {
                                activity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        System.out.println("RPC ADD LIQUIDITY: " + output[0] + output[1] + output[2]);
                                        if (output[2].equals("error"))
                                            UiDialog.showDialogStatus(output[1]);
                                        else {
                                            FragmentSwap.clearAccountFromTo(activity);
                                            try {
                                                UiDialog.showDialogStatus(R.string.swap_add_liquidity_complete,
                                                        ApiRpc.class.getDeclaredMethod("runActionHistory"));
                                            } catch (NoSuchMethodException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                            }
                        });
                        /*new ApiRpc().act(new ApiRpc.Action().actionAddLiquidity(Global.getSelectedAccountId(),
                                GlobalLyra.symbolToDomain(tokenFromSpinner.getSelectedItem().toString()), Double.parseDouble(swapFromValueEditText.getText().toString()),
                                GlobalLyra.symbolToDomain(tokenToSpinner.getSelectedItem().toString()), Double.parseDouble(swapToValueEditText.getText().toString())));*/
                        Toast.makeText(view.getContext(), getText(R.string.swap_add_liquidity_action_begin), Toast.LENGTH_LONG).show();
                    } else {
                        UiUpdates.PoolCalculateData poolCalculateData = UiUpdates.getPoolCalculateData();
                        if(poolCalculateData != null) {
                            NetworkRpc rpc = (NetworkRpc) new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL, Global.getWalletPassword())
                                    .execute("", "Swap", Global.getSelectedAccountId(),
                                            GlobalLyra.symbolToDomain(tokenFromSpinner.getSelectedItem().toString()),
                                            GlobalLyra.symbolToDomain(tokenToSpinner.getSelectedItem().toString()),
                                            GlobalLyra.symbolToDomain(tokenFromSpinner.getSelectedItem().toString()),
                                            swapFromValueEditText.getText().toString(),
                                            String.valueOf(poolCalculateData.getMinimumReceived())
                                    );
                            rpc.setListener(new NetworkRpc.RpcTaskListener() {
                                @Override
                                public void onRpcTaskFinished(String[] output) {
                                    activity.runOnUiThread(new Runnable() {
                                        public void run() {
                                            System.out.println("RPC SWAP: " + output[0] + output[1] + output[2]);
                                            if (output[2].equals("error"))
                                                UiDialog.showDialogStatus(R.string.str_an_error_occurred, R.string.str_please_contact_lyra_period_inc);
                                            else {
                                                FragmentSwap.clearAccountFromTo(activity);
                                                try {
                                                    UiDialog.showDialogStatus(R.string.swap_swap_complete,
                                                            ApiRpc.class.getDeclaredMethod("runActionHistory"));
                                                } catch (NoSuchMethodException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    });
                                }
                            });

                            /*new ApiRpc().act(new ApiRpc.Action().actionSwap(Global.getSelectedAccountId(),
                                    GlobalLyra.symbolToDomain(tokenFromSpinner.getSelectedItem().toString()),
                                    GlobalLyra.symbolToDomain(tokenToSpinner.getSelectedItem().toString()),
                                    GlobalLyra.symbolToDomain(tokenFromSpinner.getSelectedItem().toString()),
                                    Double.parseDouble(swapFromValueEditText.getText().toString()),
                                    poolCalculateData.getMinimumReceived()
                            ));*/
                            Toast.makeText(view.getContext(), getText(R.string.swap_swap_action_begin), Toast.LENGTH_LONG).show();
                        } else {

                        }
                    }
                } catch (NumberFormatException | NullPointerException e) {
                    e.printStackTrace();
                    Toast.makeText(view.getContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
           }
        });
        swapRemoveShareActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                UiDialog.showDialogStatus(R.string.send_removing);
                Spinner tokenFromSpinner = (Spinner) view.findViewById(R.id.swapFromValueSpinner);
                Spinner tokenToSpinner = (Spinner) view.findViewById(R.id.swapToValueSpinner);
                NetworkRpc rpc = (NetworkRpc) new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL, Global.getWalletPassword())
                        .execute("", "RemoveLiquidaty", Global.getSelectedAccountId(),
                                GlobalLyra.symbolToDomain(tokenFromSpinner.getSelectedItem().toString()),
                                GlobalLyra.symbolToDomain(tokenToSpinner.getSelectedItem().toString())
                        );
                rpc.setListener(new NetworkRpc.RpcTaskListener() {
                    @Override
                    public void onRpcTaskFinished(String[] output) {
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                System.out.println("RPC REMOVE LIQUIDITY: " + output[0] + output[1] + output[2]);
                                if (output[2].equals("error"))
                                    UiDialog.showDialogStatus(output[1]);
                                else {
                                    FragmentSwap.clearAccountFromTo(activity);
                                    try {
                                        UiDialog.showDialogStatus(R.string.swap_remove_liquidity_complete,
                                                ApiRpc.class.getDeclaredMethod("runActionHistory"));
                                    } catch (NoSuchMethodException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                });
            }
        });
        restoreTimers(view);
        setPoolValuesVisibility(activity, View.GONE);
    }
}
