package com.lyrawallet.Api.ApiRpcActions;

import android.content.SharedPreferences;
import android.util.Pair;

import androidx.preference.PreferenceManager;

import com.lyrawallet.Api.ApiRpc;
import com.lyrawallet.Global;
import com.lyrawallet.GlobalLyra;
import com.lyrawallet.MainActivity;
import com.lyrawallet.R;
import com.lyrawallet.Storage.StorageHistory;
import com.lyrawallet.Ui.FragmentAccount.FragmentAccount;
import com.lyrawallet.Util.Concatenate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class ApiRpcActionsHistory extends MainActivity {
    public static class HistoryEntry {
        private int Height = 0;
        private boolean IsReceive = false;
        private long TimeStamp = 0;
        private String SendAccountId = null;
        private String SendHash = null;
        private String RecvAccountId = null;
        private String RecvHash = null;
        private final List<Pair<String, Double>> Changes = new ArrayList<>();
        private final List<Pair<String, Double>> Balances = new ArrayList<>();

        HistoryEntry() {

        }

        public static String toJson(String data, String sendAccountId, String recvAccountId, Pair<String, Double> changes) {
            try {
                JSONArray objRet = new JSONArray(data);
                JSONObject objEntry;
                if(objRet.length() == 0)
                    objEntry = new JSONObject();
                else
                    objEntry = objRet.getJSONObject(objRet.length() - 1);

                objEntry.put("Height", 0);
                objEntry.put("IsReceive", false);
                objEntry.put("TimeStamp", 0L);
                objEntry.put("SendAccountId", sendAccountId);
                objEntry.put("SendHash", "");
                objEntry.put("RecvAccountId", recvAccountId);
                objEntry.put("RecvHash", "");
                JSONObject objChanges = new JSONObject();
                objChanges.put(changes.first, String.format(Locale.US, "%f", changes.second));
                objEntry.put("Changes", objChanges);
                if(objRet.length() == 0) {
                    JSONObject objBalances = new JSONObject();
                    objBalances.put("LYR", "0");
                    objEntry.put("Balances", objBalances);
                } else {
                    JSONObject en = objRet.getJSONObject(objRet.length() - 1).getJSONObject("Balances");
                    objEntry.put("Balances", en);
                }
                objRet.put(objEntry);
                return objRet.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return data;
        }

        public HistoryEntry fromJson(String data) {
            try {
                JSONObject obj = new JSONObject(data);
                Height = obj.getInt("Height");
                IsReceive = obj.getBoolean("IsReceive");
                TimeStamp = obj.getLong("TimeStamp");
                SendAccountId = obj.getString("SendAccountId");
                SendHash = obj.getString("SendHash");
                RecvAccountId = obj.getString("RecvAccountId");
                if(!obj.isNull("RecvHash"))
                    RecvHash = obj.getString("RecvHash");

                JSONObject o = obj.getJSONObject("Changes");
                for (Iterator<String> it = o.keys(); it.hasNext(); ) {
                    String key = it.next();
                    Changes.add(new Pair<String, Double>(key, o.getDouble(key)));
                }
                if(Changes.size() > 1) {
                    for (int i = 1; i < Changes.size(); i++) {
                        Pair<String, Double> p = Changes.get(i);
                        if(p.first.equals("LYR")) {
                            // Make sure LYR is on position 0
                            Changes.remove(p);
                            Changes.add(0, p);
                        }
                    }
                }
                o = obj.getJSONObject("Balances");
                for (Iterator<String> it = o.keys(); it.hasNext(); ) {
                    String key = it.next();
                    Balances.add(new Pair<String, Double>(key, o.getDouble(key)));
                }
                if(Balances.size() > 1) {
                    for (int i = 1; i < Balances.size(); i++) {
                        Pair<String, Double> p = Balances.get(i);
                        if(p.first.equals("LYR")) {
                            // Make sure LYR is on position 0
                            Balances.remove(p);
                            Balances.add(0, p);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }

        public int getHeight() {
            return Height;
        }
        public boolean getIsReceive() {
            return IsReceive;
        }
        public long getTimeStamp() {
            return TimeStamp;
        }
        public String getSendAccountId() {
            return SendAccountId;
        }
        public String getSendHash() {
            return SendHash;
        }
        public String getRecvAccountId() {
            return RecvAccountId;
        }
        public String getRecvHash() {
            return RecvHash;
        }
        public List<Pair<String, Double>> getChanges() {
            return Changes;
        }
        public List<Pair<String, Double>> getBalances() {
            return Balances;
        }

    }

    public static void store(ApiRpc.Action ac, String networkData, String insertedData) {

    }
    public static void store(ApiRpc.Action ac, String networkData) {
        boolean isStore = false;
        if(ac.getActionPurpose() != null) {
            isStore = ac.getActionPurpose().equals(Global.str_api_rpc_purpose_history_disk_storage);
        }
        if(ac.getApi().equals("Send") || isStore) {
            Pair<Integer, String> h = Global.getWalletHistory(Concatenate.getHistoryFileName(ac));
            if(networkData != null) {
                if (h == null || h.second == null || h.second.length() != networkData.length()) {
                    // Run set history on another thread.
                    Timer timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        public void run() {
                            timer.cancel();
                            Global.setWalletHistory(Concatenate.getHistoryFileName(ac), networkData);
                        }
                    }, 100, 100);
                }
            }
            String storedData = StorageHistory.read( Concatenate.getHistoryFileName(ac), Global.getWalletPassword());
            int countArrayStored = 0;
            if(storedData != null) {
                try {
                    JSONArray arrayStored = new JSONArray(storedData);
                    countArrayStored = arrayStored.length();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            try {
                if(networkData != null) {
                    JSONArray arrayNetwork = new JSONArray(networkData);
                    if(countArrayStored != arrayNetwork.length() || !networkData.equals(storedData)) {
                        StorageHistory.save( Concatenate.getHistoryFileName(ac), networkData, Global.getWalletPassword());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static String load(ApiRpc.Action ac) {
        String h = StorageHistory.read( Concatenate.getHistoryFileName(ac), Global.getWalletPassword());
        Global.setWalletHistory( Concatenate.getHistoryFileName(ac), h);
        return h;
    }

    public static String load(String fileName) {
        String h = StorageHistory.read( fileName, Global.getWalletPassword());
        Global.setWalletHistory( fileName, h);
        return h;
    }

    public List<HistoryEntry> loadHistory(ApiRpc.Action ac) {
        return loadHistory(StorageHistory.read( Concatenate.getHistoryFileName(ac), Global.getWalletPassword()));
    }

    public static List<HistoryEntry> loadHistory(String data) {
        if(data == null) {
            return null;
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getInstance().getBaseContext());
        /*double lyraPriceInUsd = Double.parseDouble(preferences.getString(Global.keyCustomLyraPriceInUsd,
                String.valueOf(Global.getLyraPriceInUsd(new Pair<>("LYR", "USD")))));*/
        List<HistoryEntry> historyList = new ArrayList<HistoryEntry>();
        try {
            JSONArray arrObj = new JSONArray(data);
            for (int i = 0; i < arrObj.length(); i++) {
                historyList.add(new HistoryEntry().fromJson(arrObj.getJSONObject(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return historyList;
    }

    public static int getHistoryCount(String data) {
        if(data != null) {
            try {
                JSONArray arrObj = new JSONArray(data);
                return arrObj.length();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    // Sample data for RecyclerView
    private List<FragmentAccount.AccountHistoryEntry> getData() {
        Pair<Integer, String> historyAndCnt = Global.getWalletHistory(Concatenate.getHistoryFileName());
        return getData(historyAndCnt);
    }
    public static List<FragmentAccount.AccountHistoryEntry> getData(Pair<Integer, String> historyAndCnt) {
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
            int icon = GlobalLyra.TokenIconList[0].second;
            for (Pair<String, Integer> k: GlobalLyra.TokenIconList) {
                if(k.first.equals(tokenAmount.first)) {
                    icon = k.second;
                    break;
                }
            }
            List.add(0, new FragmentAccount.AccountHistoryEntry(historyList.get(i).getHeight(), icon,
                    tokenAmount.first, tokenAmount.second, 0.00021f));
        }
        return List;
    }

}
