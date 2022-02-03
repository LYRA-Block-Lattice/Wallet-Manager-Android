package com.lyrawallet.Api.ApiRpcActions;

import android.util.Pair;

import androidx.appcompat.app.AppCompatActivity;

import com.lyrawallet.Api.ApiRpc;
import com.lyrawallet.Api.Network.NetworkRpc;
import com.lyrawallet.Global;
import com.lyrawallet.GlobalLyra;
import com.lyrawallet.MainActivity;
import com.lyrawallet.Util.Concatenate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ApiRpcActionsBalance extends AppCompatActivity {
    public ApiRpcActionsBalance() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (Global.getSelectedAccountName().length() != 0) {
                    //new ApiRpc().act(new ApiRpc.Action().actionBalance(Global.getSelectedAccountId()));
                    NetworkRpc rpc = (NetworkRpc) new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL)
                            .execute("", "Balance", Global.getSelectedAccountId()
                            );
                    rpc.setListener(new NetworkRpc.RpcTaskListener() {
                        @Override
                        public void onRpcTaskFinished(String[] output) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    System.out.println("RPC BALANCE: " + output[0] + output[1] + output[2]);
                                    try {
                                        JSONObject objCmd = new JSONObject(output[2]);
                                        if (!objCmd.isNull("unreceived")) {
                                            if (objCmd.getBoolean("unreceived")) {
                                                //new ApiRpc().act(new ApiRpc.Action().actionReceive("Receive", Global.getSelectedAccountId()));
                                                NetworkRpc rpc = (NetworkRpc) new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL, Global.getWalletPassword())
                                                        .execute("", "Receive", Global.getSelectedAccountId()
                                                        );
                                                rpc.setListener(new NetworkRpc.RpcTaskListener() {
                                                    @Override
                                                    public void onRpcTaskFinished(String[] output) {
                                                        runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                System.out.println("RPC RECEIVE: " + output[0] + output[1] + output[2]);
                                                                ApiRpc.getBalanceAfterAction();
                                                            }
                                                        });
                                                    }
                                                });

                                            } else {
                                                Pair<Integer, List<ApiRpcActionsHistory.HistoryEntry>> h = Global.getWalletHistory(Concatenate.getHistoryFileName());
                                                if (h != null && h.second != null) {
                                                    if (objCmd.getLong("height") != h.second.get(h.second.size() - 1).getHeight()) {
                                                        new ApiRpc().act(new ApiRpc.Action().actionHistory(Global.str_api_rpc_purpose_history_disk_storage,
                                                                Global.getCurrentNetworkName(), Global.getSelectedAccountName(), Global.getSelectedAccountId()));
                                                    }
                                                } else {
                                                    new ApiRpc().act(new ApiRpc.Action().actionHistory(Global.str_api_rpc_purpose_history_disk_storage,
                                                            Global.getCurrentNetworkName(), Global.getSelectedAccountName(), Global.getSelectedAccountId()));
                                                }
                                            }
                                        }
                                    } catch (JSONException | NullPointerException | ArrayIndexOutOfBoundsException e) {
                                        //Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }
}
