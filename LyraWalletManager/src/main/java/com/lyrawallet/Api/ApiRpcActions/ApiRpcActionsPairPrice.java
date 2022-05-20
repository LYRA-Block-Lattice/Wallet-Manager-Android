package com.lyrawallet.Api.ApiRpcActions;

import android.app.Activity;
import android.util.Pair;
import android.widget.Toast;

import com.lyrawallet.Api.ApiRpc;
import com.lyrawallet.Api.Network.NetworkRpc;
import com.lyrawallet.Global;
import com.lyrawallet.GlobalLyra;
import com.lyrawallet.Ui.UiUpdates;

import org.json.JSONException;
import org.json.JSONObject;

public class ApiRpcActionsPairPrice {
    static public void set(Activity activity, String token0, String Token1) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                NetworkRpc rpc = (NetworkRpc) new NetworkRpc(GlobalLyra.LYRA_RPC_API_URL)
                        .execute("", "Pool", token0, Token1);
                rpc.setListener(new NetworkRpc.RpcTaskListener() {
                    @Override
                    public void onRpcTaskFinished(String[] output) {
                        System.out.println("RPC POOL " + token0 + "/" + Token1 + ": " + output[0] + output[1] + output[2]);
                        try {
                            UiUpdates.PoolData poolData = new UiUpdates.PoolData(output[2]);
                            JSONObject obj = new JSONObject(output[2]);
                            JSONObject objBalance = obj.getJSONObject("balance");
                            if( poolData.token1Is(Token1) && poolData.token0Is(token0))
                                Global.setTokenPrice(new Pair<>(token0, Token1), objBalance.getDouble(Token1) / objBalance.getDouble(token0));
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                            Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

    }
}
