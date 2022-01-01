package com.lyrawallet.Storage;

import com.lyrawallet.MainActivity;

public class ImportWallet extends MainActivity {
    public ImportWallet(String walletName) {
        importWallet(StorageCommon.IMPORT_WALLET, walletName);
    }
}
