package com.lyrawallet.Util;

import com.lyrawallet.Api.ApiRpc;
import com.lyrawallet.Global;

public class Concatenate {
    static public String getHistoryFileName(ApiRpc.Action ac) {
        return Global.getWalletName() + "_" + ac.getAccName() + "_" + ac.getNetwork();
    }

    static public String getHistoryFileName() {
        return Global.getWalletName() + "_" + Global.getSelectedAccountName() + "_" + Global.getCurrentNetworkName();
    }

}
