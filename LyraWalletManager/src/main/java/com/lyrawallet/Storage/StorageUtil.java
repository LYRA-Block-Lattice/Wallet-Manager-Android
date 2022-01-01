package com.lyrawallet.Storage;

import com.lyrawallet.Global;

import java.io.File;

public class StorageUtil {
    public static boolean createBeakup(String containerName) {
        //------  Increase the count of each backup wallet ----------//
        int fileCount = Global.getMaxWalletsBackupAllowed();
        for (; fileCount > 0; fileCount-- ) {
            File srcFile = new File(Global.getWalletPath(containerName + "_" + fileCount));
            File dstFile = new File(Global.getWalletPath(containerName + "_" + (fileCount + 1)));
            if(dstFile.exists() && fileCount == Global.getMaxWalletsBackupAllowed()) {
                dstFile.delete();
            } else if(srcFile.exists()) {
                boolean success = srcFile.renameTo(dstFile);
                if(!success) {
                    return false;
                }
            }
        }
        //------  Current stored wallet has no count ----------------//
        File srcFile = new File(Global.getWalletPath(containerName));
        if(srcFile.exists()) {
            File dstFile = new File(Global.getWalletPath(containerName + "_" + 1));
            return srcFile.renameTo(dstFile);
        }
        return true;
    }
}
