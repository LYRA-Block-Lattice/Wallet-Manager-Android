package com.lyrawallet.Ui;

import android.util.Pair;

import com.lyrawallet.Api.ApiRpcActions.ApiRpcActionsHistory;
import com.lyrawallet.Global;
import com.lyrawallet.GlobalLyra;
import com.lyrawallet.Ui.FragmentAccountHistory.FragmentAccountHistory;
import com.lyrawallet.Util.Concatenate;

import java.util.ArrayList;
import java.util.List;

public class UtilGetData {
    public static List<Pair<String, Double>> getAvailableTokenList() {
        List<Pair<String, Double>> balances;
        List<Pair<String, Double>> balancesRet = new ArrayList<>();;
        Pair<Integer, List<ApiRpcActionsHistory.HistoryEntry>> history = Global.getWalletHistory(Concatenate.getHistoryFileName());
        try {
            if(history == null)
                return balancesRet;
            if(history.second.size() != 0) {
                balances = history.second.get(history.second.size() - 1).getBalances();
                for (int i = 0; i < balances.size(); i++) {
                    Pair<String, Double> p = new Pair<>(GlobalLyra.domainToSymbol(balances.get(i).first), balances.get(i).second);
                    balancesRet.add(p);
                }
                if(balancesRet.size() > 1) {
                    for (int i = 1; i < balancesRet.size(); i++) {
                        Pair<String, Double> p = new Pair<>(GlobalLyra.domainToSymbol(balances.get(i).first), balances.get(i).second);
                        if(p.first.equals("LYR")) {
                            // Make sure LYR is on position 0
                            balancesRet.remove(p);
                            balancesRet.add(0, p);
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return balancesRet;
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

    public static List<FragmentAccountHistory.AccountHistoryEntry> historyToAccountAdapter(List<ApiRpcActionsHistory.HistoryEntry> historyList) {
        if(historyList == null) {
            return null;
        }
        List<FragmentAccountHistory.AccountHistoryEntry> List = new ArrayList<>();
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
            List.add(0, new FragmentAccountHistory.AccountHistoryEntry(historyList.get(i).getHeight(), icon,
                    GlobalLyra.domainToSymbol(tokenAmount.first), tokenAmount.second));
        }
        return List;
    }

    public static void sortKnownFirst(List<FragmentAccountHistory.AccountHistoryEntry> list) {

    }
}
