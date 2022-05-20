package com.lyrawallet.Storage;

import android.util.Pair;

import com.lyrawallet.Crypto.CryptoAes256;
import com.lyrawallet.Crypto.CryptoBase58Encoding;
import com.lyrawallet.Global;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StorageHistory {
    static List<Pair<String, String>> history = new ArrayList<>();

    public static String read(String containerName, String password) {
        for (Pair<String, String> h: history) {
            if(h.first.equals(containerName))
                return h.second;
        }
        return null;
    }

    public static boolean save(String containerName, String data, String password) {
        Pair<String, String> h = new Pair<>(containerName, data);
        for (int i = 0; i < history.size(); i++) {
            if(history.get(i).first.equals(containerName)) {
                history.set(i, h);
                return true;
            }
        }
        history.add(h);
        return true;
    }
}
