package com.lyrawallet.Ui;

import android.util.Pair;

import com.lyrawallet.Api.ApiRpcActions.ApiRpcActionsHistory;
import com.lyrawallet.Global;
import com.lyrawallet.GlobalLyra;
import com.lyrawallet.Ui.FragmentAccount.FragmentAccount;
import com.lyrawallet.Util.Concatenate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UtilGetData {
    public static List<Pair<String, Double>> getAvailableTokenList() {
        List<Pair<String, Double>> balances = new ArrayList<>();
        Pair<Integer, String> history = Global.getWalletHistory(Concatenate.getHistoryFileName());
        try {
            JSONArray arrayStored = new JSONArray(history.second);
            if(arrayStored.length() != 0) {
                JSONObject o = arrayStored.getJSONObject(arrayStored.length() - 1).getJSONObject("Balances");
                for (Iterator<String> it = o.keys(); it.hasNext(); ) {
                    String key = it.next();
                    balances.add(new Pair<String, Double>(GlobalLyra.domainToSymbol(key), o.getDouble(key)));
                }
                if(balances.size() > 1) {
                    for (int i = 1; i < balances.size(); i++) {
                        Pair<String, Double> p = balances.get(i);
                        if(p.first.equals("LYR")) {
                            // Make sure LYR is on position 0
                            balances.remove(p);
                            balances.add(0, p);
                        }
                    }
                }
            }
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }
        return balances;
    }

    public static boolean checkTokenExist(String token) {
        List<Pair<String, Double>> balances = UtilGetData.getAvailableTokenList();
        for (int i = 0; i < balances.size(); i++) {
            if( token.equals(balances.get(i).first)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkEnoughTokens(String token, double value, double fee) {
        if(value == 0f) {
            return false;
        }
        List<Pair<String, Double>> balances = UtilGetData.getAvailableTokenList();
        for (int i = 0; i < balances.size(); i++) {
            if( token.equals(balances.get(i).first)) {
                double val = value;
                if (token.equals("LYR")) {
                    val += fee;
                }
                return !(val > balances.get(i).second);
            }
        }
        return false;
    }

    // Sample data for RecyclerView
    private List<FragmentAccount.AccountHistoryEntry> historyToAccountAdapter() {
        Pair<Integer, String> historyAndCnt = Global.getWalletHistory(Concatenate.getHistoryFileName());
        return historyToAccountAdapter(historyAndCnt);
    }
    public static List<FragmentAccount.AccountHistoryEntry> historyToAccountAdapter(Pair<Integer, String> historyAndCnt) {
        //List<ApiRpcActionsHistory.HistoryEntry> historyList = ApiRpcActionsHistory.loadHistory(ApiRpcActionsHistory.load(ApiRpcActionsHistory.getHistoryFileName()));

        if(historyAndCnt == null || historyAndCnt.second == null) {
            return null;
        }
        List<ApiRpcActionsHistory.HistoryEntry> historyList = ApiRpcActionsHistory.loadHistory(historyAndCnt.second);
        if(historyList == null) {
            return null;
        }
        List<FragmentAccount.AccountHistoryEntry> List = new ArrayList<>();
        for (int i = 0; i < historyList.size(); i++) {
            int size = historyList.get(i).getChanges().size();
            Pair<String, Double> tokenAmount;
            if(size > 1) {
                tokenAmount = historyList.get(i).getChanges().get(1);
            } else  {
                tokenAmount = historyList.get(i).getChanges().get(0);
            }
            int icon = GlobalLyra.TokenIconList[1].second;
            for (Pair<String, Integer> k: GlobalLyra.TokenIconList) {
                if(k.first.equals(GlobalLyra.domainToSymbol(tokenAmount.first))) {
                    icon = k.second;
                    break;
                }
            }
            List.add(0, new FragmentAccount.AccountHistoryEntry(historyList.get(i).getHeight(), icon,
                    GlobalLyra.domainToSymbol(tokenAmount.first), tokenAmount.second, 0.00021f));
        }
        return List;
    }
}
