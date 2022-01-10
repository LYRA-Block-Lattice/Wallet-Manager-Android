package com.lyrawallet.Storage;

import com.lyrawallet.Crypto.CryptoBase58Encoding;
import com.lyrawallet.Global;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class StorageHistory {
    public static String read(String containerName, String password) {
        //--------------------- Read encrypted container -----------------------//
        try (FileInputStream fis = new FileInputStream(Global.getWalletPath(containerName))) {
            int size = fis.available();
            if(size == 0) {
                return null;
            }
            byte[] buffer = new byte[size];
            int bRead = fis.read(buffer);
            fis.close();
            if(buffer.length != bRead || bRead == -1) {
                return null;
            }
            return new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean save(String containerName, String data, String password) {
        //------  Save the new wallet file to disk ------------------//
        File dstFile = new File(Global.getWalletPath(containerName));
        try {
            FileWriter fiw = new FileWriter(dstFile);
            fiw.write(data);
            fiw.flush();
            fiw.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
