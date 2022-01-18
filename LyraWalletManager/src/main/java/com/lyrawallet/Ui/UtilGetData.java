package com.lyrawallet.Ui;

import android.util.Pair;

import com.lyrawallet.Api.ApiRpcActions.ApiRpcActionsHistory;
import com.lyrawallet.Global;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UtilGetData {
    public static List<Pair<String, Double>> getAvailableTokenList() {
        List<Pair<String, Double>> balances = new ArrayList<>();
        Pair<Integer, String> history = Global.getWalletHistory(ApiRpcActionsHistory.getHistoryFileName());
        try {
            JSONArray arrayStored = new JSONArray(history.second);
            if(arrayStored.length() != 0) {
                JSONObject o = arrayStored.getJSONObject(arrayStored.length() - 1).getJSONObject("Balances");
                for (Iterator<String> it = o.keys(); it.hasNext(); ) {
                    String key = it.next();
                    balances.add(new Pair<String, Double>(key, o.getDouble(key)));
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
        } catch (JSONException e) {
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
}
