package com.lyrawallet.Storage;

import android.util.Pair;

import com.lyrawallet.Crypto.CryptoAes256;
import com.lyrawallet.Crypto.CryptoBase58Encoding;
import com.lyrawallet.Global;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StorageKeys {
    public enum status {
        OK,
        FILE_EXIST,
        FILE_NOT_EXIST,
        FILE_READ_ERROR,
        FILE_NOT_DECODED,
        FILE_ERROR_SAVE,
        FILE_ERROR_CREATE_BACKUP,
        JSON_ERROR_LOAD_KEYS,
        JSON_ERROR_PARSE_KEYS,
        JSON_ERROR_CREATE_KEYS,
        JSON_ERROR_SAVE_KEYS,
        ALIAS_EXISTS,
        ERROR_PASSWORD,
        ENCRYPTION_NULL,
        DECRYPTION_NULL,
        ACCOUNT_EXISTS,
        ACCOUNT_NOT_EXISTS
    }

    private static status Status = status.OK;

    public static status getStatus() {
        return Status;
    }

    public static boolean aliasAdd(String containerName, String alias, String pKey, String password) {
        if(aliasExists(containerName, alias, password)) {
            Status = status.ALIAS_EXISTS;
            return false;
        }
        List<Pair<String, String>> keyList = containerRead(containerName, password);
        if(keyList == null) {
            return false;
        }
        keyList.add(new Pair<String, String>(alias, pKey));
        return containerSave(containerName, password, keyList);
    }

    public static boolean aliasExists(String containerName, String alias, String password) {
        Status = status.OK;
        List<Pair<String, String>> accList = containerRead(containerName, password);
        if(accList == null) {
            return false;
        }
        for (Pair<String, String> acc : accList) {
            if(acc.first.equals(alias)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasAliases(String containerName, String password) {
        Status = status.OK;
        List<Pair<String, String>> accList = containerRead(containerName, password);
        if(accList == null) {
            Status = status.FILE_READ_ERROR;
            return false;
        }
        return accList.size() != 0;
    }

    public static boolean fileExists(String containerName) {
        Status = status.OK;
        File file = new File(Global.getWalletPath(containerName));
        if(file.exists())  {
            Status = status.FILE_EXIST;
            return true;
        }
        return false;
    }

    public static boolean containerExists(String containerName, String password) {
        Status = status.OK;
        return containerRead(containerName, password) != null;
    }

    public  static boolean containerDelete(String containerName, String password) {
        Status = status.OK;
        if(!containerExists(containerName, password)) {
            Status = status.FILE_NOT_EXIST;
            return false;
        }
        File file = new File(Global.getWalletPath(containerName));
        return file.delete();
    }

    public static List<Pair<String, String>> containerRead(String containerName, String password) {
        Status = status.OK;
        byte[] data = read(containerName, password);
        if(data == null) {
            Status = status.FILE_NOT_EXIST;
            return null;
        }
        return decrypt(new String(data), password);
    }

    public static byte[] read(String containerName, String password) {
        //--------------------- Read encrypted container -----------------------//
        Status = status.OK;
        try (FileInputStream fis = new FileInputStream(Global.getWalletPath(containerName))) {
            int size = fis.available();
            if(size == 0) {
                Status = status.FILE_NOT_EXIST;
                return null;
            }
            byte[] buffer = new byte[size];
            int bRead = fis.read(buffer);
            fis.close();
            if(buffer.length != bRead || bRead == -1) {
                Status = status.FILE_READ_ERROR;
                return null;
            }
            byte[] decoded = CryptoBase58Encoding.decode(new String(buffer));
            if(decoded == null) {
                Status = status.FILE_NOT_DECODED;
                return null;
            }
            return CryptoBase58Encoding.verifyAndRemoveCheckSum(decoded);
        } catch (IOException e) {
            Status = status.FILE_NOT_EXIST;
            e.printStackTrace();
            return null;
        }
    }

    public static List<Pair<String, String>> decrypt(String data, String password) {
        Status = status.OK;
        if (data == null) {
            Status = status.DECRYPTION_NULL;
            return null;
        }
        String decrypted = CryptoAes256.decrypt(data, password);
        if (decrypted == null) {
            Status = status.DECRYPTION_NULL;
            return null;
        }
        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(decrypted);
        } catch (JSONException e) {
            Status = status.ERROR_PASSWORD;
            e.printStackTrace();
            return null;
        }
        JSONObject jsonKeyList;
        try {
            jsonKeyList = jsonObj.getJSONObject("Keys");
        } catch (JSONException e) {
            Status = status.JSON_ERROR_LOAD_KEYS;
            e.printStackTrace();
            return null;
        }
        List<Pair<String, String>> keyList = new ArrayList<Pair<String, String>>();
        Iterator<String> keys = jsonKeyList.keys();
        try {
            while (keys.hasNext()) {
                String key = keys.next();
                keyList.add(new Pair<String, String>(key, jsonKeyList.getString(key)));
            }
        } catch (JSONException e) {
            Status = status.JSON_ERROR_PARSE_KEYS;
            e.printStackTrace();
            return null;
        }
        return keyList;
    }

    public static boolean containerSave(String containerName, String password, List<Pair<String, String>> keyList) {
        //------  Store encrypted container -------------------------//
        Status = status.OK;
         String encrypted = encrypt(keyList, password);
        if(encrypted == null) {
            return false;
        }
        if(!StorageUtil.createBeakup(containerName)) {
            Status = StorageKeys.status.FILE_ERROR_CREATE_BACKUP;
            return false;
        }
        return save(containerName, encrypted);
    }

    public static String encrypt(List<Pair<String, String>> keyList, String password) {
        Status = status.OK;
        JSONObject jsonObject = new JSONObject ();
        JSONObject jsonKeyObject = new JSONObject ();
        if(keyList != null) {
            for (Pair<String, String> acc : keyList) {
                try {
                    jsonKeyObject.put(acc.first, acc.second);
                } catch (JSONException e) {
                    Status = status.JSON_ERROR_CREATE_KEYS;
                    e.printStackTrace();
                    return null;
                }
            }
        }
        try {
            jsonObject.put("Keys", jsonKeyObject);
        } catch (JSONException e) {
            Status = status.JSON_ERROR_SAVE_KEYS;
            e.printStackTrace();
            return null;
        }
        String decrypted = jsonObject.toString();
        return CryptoAes256.encrypt(decrypted, password);
    }

    private static boolean save(String containerName, String data) {
        //------  Save the new wallet file to disk ------------------//
        String encoded = CryptoBase58Encoding.encodeWithCheckSum(data.getBytes(StandardCharsets.UTF_8));
        File dstFile = new File(Global.getWalletPath(containerName));
        try {
            FileWriter fiw = new FileWriter(dstFile);
            fiw.write(encoded);
            fiw.flush();
            fiw.close();
        } catch (IOException e) {
            Status = status.FILE_ERROR_SAVE;
            return false;
        }
        return true;
    }
}
