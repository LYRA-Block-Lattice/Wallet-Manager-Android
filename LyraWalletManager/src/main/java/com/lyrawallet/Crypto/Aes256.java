package com.lyrawallet.Crypto;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Aes256 {
    /* Encryption Method */
    public static byte[] encryptEcb(byte[] data, String password) {
        if(data == null || password == null) {
            return null;
        }
        try {
            /* Declare a byte array. */
            // Random rd = new Random();
            // byte[] iv = new byte[32];
            // rd.nextBytes(iv);
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            SecretKeySpec secretKey = new SecretKeySpec(md.digest(password.getBytes(StandardCharsets.UTF_8)), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            return cipher.doFinal(data);
        } catch (InvalidAlgorithmParameterException |
                InvalidKeyException |
                NoSuchAlgorithmException |
                BadPaddingException |
                IllegalBlockSizeException |
                NoSuchPaddingException e) {
            System.out.println("ERROR: During encryption: " + e.toString());
        }
        return null;
    }

    /* Decryption Method */
    public static byte[] decryptEcb(byte[] data, String password) {
        if(data == null || password == null) {
            return null;
        }
        try {
            /* Declare a byte array. */
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            IvParameterSpec ivspec = new IvParameterSpec(iv);
            SecretKeySpec secretKey = new SecretKeySpec(md.digest(password.getBytes(StandardCharsets.UTF_8)), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            return cipher.doFinal(data);
        } catch (InvalidAlgorithmParameterException |
                InvalidKeyException |
                NoSuchAlgorithmException |
                BadPaddingException |
                IllegalBlockSizeException |
                NoSuchPaddingException e) {
            System.out.println("ERROR: During decryption: " + e.toString());
        }
        return null;
    }

    public static String encrypt(String str, String password) {
        if(str == null || password == null) {
            return null;
        }
        int destSize = ((str.length() % 16) != 0) ? ((str.length() / 16) * 16) + 16 : str.length();
        if(destSize == 0) {
            return null;
        }
        byte[] dataByte = new byte[destSize + 1];
        int i = 0;
        for(; i < str.length();i++) {
            dataByte[i] = str.getBytes()[i];
        }
        for(; i < destSize; i++) {
            dataByte[i] = 0;
        }
        byte[] tmp = encryptEcb(dataByte, password);
        if(tmp == null) {
            return null;
        }
        return Base58Encoding.Encode(tmp);
    }

    public static String decrypt(String str, String password) {
        if(str == null || password == null) {
            return null;
        }
        byte[] st = decryptEcb(Base58Encoding.Decode(str), password);
        if(st == null) {
            return null;
        }
        int i = 0;
        for(; i < st.length; i++) {
            if(st[i] == 0) {
                break;
            }
        };
        return new String(Arrays.copyOf(st, i));
    }
}
