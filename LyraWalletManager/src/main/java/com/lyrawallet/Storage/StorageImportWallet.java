package com.lyrawallet.Storage;

import com.lyrawallet.MainActivity;

public class StorageImportWallet extends MainActivity {
    public StorageImportWallet(String walletName) {
        importWallet(StorageCommon.IMPORT_WALLET, walletName);
    }
}
