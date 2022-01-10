package com.lyrawallet.Api.ApiRpcActions;

import static com.lyrawallet.Api.ApiRpc.getHistoryFileName;

import com.lyrawallet.Api.ApiRpc;
import com.lyrawallet.Global;
import com.lyrawallet.Storage.StorageHistory;

import org.json.JSONArray;
import org.json.JSONException;

public class ApiRpcActionsHistory {
    public static void store(ApiRpc.Action ac, String networkData) {
        int countArrayStored = 0;
        if(ac.getActionPurpose().equals(Global.str_api_rpc_purpose_history_disk_storage)) {
            String storedData = StorageHistory.read( getHistoryFileName(ac), "one_password");
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
                    if(countArrayStored != arrayNetwork.length()) {
                        StorageHistory.save( getHistoryFileName(ac), networkData, "one_password");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
