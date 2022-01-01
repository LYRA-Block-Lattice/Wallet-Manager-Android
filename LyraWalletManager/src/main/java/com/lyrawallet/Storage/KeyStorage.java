package com.lyrawallet.Storage;

import android.util.Pair;

import com.lyrawallet.Crypto.Aes256;
import com.lyrawallet.Crypto.Base58Encoding;
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

public class KeyStorage {
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
        ACC_EXISTS,
        ACC_NOT_EXISTS
    }

    public static status Status = status.OK;

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
            byte[] decoded = Base58Encoding.Decode(new String(buffer));
            if(decoded == null) {
                Status = status.FILE_NOT_DECODED;
                return null;
            }
            return Base58Encoding.VerifyAndRemoveCheckSum(decoded);
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
        String decrypted = Aes256.decrypt(data, password);
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
        if(!createBeakups(containerName)) {
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
        return Aes256.encrypt(decrypted, password);
    }

    private static boolean save(String containerName, String data) {
        //------  Save the new wallet file to disk ------------------//
        String encoded = Base58Encoding.EncodeWithCheckSum(data.getBytes(StandardCharsets.UTF_8));
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

    private static boolean createBeakups(String containerName) {
        //------  Increase the count of each backup wallet ----------//
        int fileCount = Global.maxWalletsBackupAllowed;
        for (; fileCount > 0; fileCount-- ) {
            File srcFile = new File(Global.getWalletPath(containerName + "_" + fileCount));
            File dstFile = new File(Global.getWalletPath(containerName + "_" + (fileCount + 1)));
            if(dstFile.exists() && fileCount == Global.maxWalletsBackupAllowed) {
                dstFile.delete();
            } else if(srcFile.exists()) {
                boolean success = srcFile.renameTo(dstFile);
                if(!success) {
                    Status = status.FILE_ERROR_CREATE_BACKUP;
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
