package com.lyrawallet.Crypto;

import android.util.Pair;

import com.lyrawallet.GlobalLyra;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

public class CryptoSignatures {
    static {
        Security.removeProvider("BC");
        // Confirm that positioning this provider at the end works for your needs!
        Security.addProvider(new BouncyCastleProvider());
    }


    public static boolean validateAccountId(String AccountId) {
        if(AccountId.length() < 1)
            return false;
        if (AccountId.charAt(0) != GlobalLyra.ADDRESSPREFIX)
            return false;
        byte[] res = CryptoBase58Encoding.DecodeAccountId(AccountId);
        if(res == null) {
            return false;
        }
        return res.length != 0;
    }

    public static boolean validatePublicKey(String PublicKey) {
        byte[] res = CryptoBase58Encoding.DecodePublicKey(PublicKey);
        if(res == null) {
            return false;
        }
        return res.length != 0;
    }

    public static boolean validatePrivateKey(String PrivateKey) {
        byte[] res = CryptoBase58Encoding.DecodePrivateKey(PrivateKey);
        if(res == null) {
            return false;
        }
        return res.length != 0;
    }

    public static boolean verifyAccountSignature(String message, String accountId, String signature) {
        if (message.length() == 0 || !validateAccountId(accountId) || signature.length() == 0)
            return false;
        byte[] publicKeyBytes = CryptoBase58Encoding.DecodeAccountId(accountId);
        return verifySignature(message, publicKeyBytes, signature);
    }

    public static boolean verifyAuthorizerSignature(String message, String publicKey, String signature) {
        if (message.length() == 0 || !validateAccountId(publicKey) || signature.length() == 0)
            return false;
        byte[] publicKeyBytes = CryptoBase58Encoding.DecodePublicKey(publicKey);
        //try {
            return verifySignature(message, publicKeyBytes, signature);
        //} catch (NoSuchAlgorithmException e) {
        //    e.printStackTrace();
        //}
        //return false;
    }

    private static boolean verifySignature(String message, byte[] publicKeyBytes, String signature) {
        try {
            PublicKey pubKey = CryptoJavaCryptoUtil.publicKeyBytesToPublicKey(publicKeyBytes);
            Signature sign = Signature.getInstance("SHA256withECDSA");
            sign.initVerify(pubKey);
            sign.update(message.getBytes(StandardCharsets.UTF_8));
            byte[] signnatureBytes = CryptoBase58Encoding.decode(signature);
            return sign.verify(CryptoSignatureHelper.derSign(signnatureBytes));
        } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getSignature(String privateKey, String message, String accountId) {
        return getSignature(privateKey, message);
    }

    public static String getSignature(String privateKey, String message)  {
        try {
            byte[] keyBytes = CryptoBase58Encoding.DecodePrivateKey(privateKey);
            PrivateKey privKey = CryptoJavaCryptoUtil.privateKeyBytesToPrivateKey(keyBytes);
            Signature signer = Signature.getInstance("SHA256withECDSA");
            //System.out.println(signer.getProvider());

            signer.initSign(privKey/*, new SecureRandom()*/);
            byte[] signature;
            //do {
                signer.update(message.getBytes(StandardCharsets.UTF_8));
                byte[] signedBytes = signer.sign();
                signature = CryptoSignatureHelper.convertDerToP1393(signedBytes);
            //}while(signature[0] == 0);
            return CryptoBase58Encoding.encode(signature);
        } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException | InvalidKeySpecException | IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static byte[] derivePublicKeyBytes(String privateKey) {
        if(!validatePrivateKey(privateKey))
            return new byte[0];
        byte[] keyBytes = CryptoBase58Encoding.DecodePrivateKey(privateKey);
        return CryptoJavaCryptoUtil.derivePublicKeyBytes(keyBytes);
    }

    public static String getAccountIdFromPrivateKey(String privateKey) {
        return CryptoBase58Encoding.encodeAccountId(derivePublicKeyBytes(privateKey));
    }

    public static String getPublicKeyFromPrivateKey(String privateKey) {
        return CryptoBase58Encoding.encodePublicKey(derivePublicKeyBytes(privateKey));
    }

    public static String generatePrivateKey() {
        return null;
    }

    public static Pair<String, String> generateWallet(byte[] keyData) {
        String pvtKeyStr = CryptoBase58Encoding.encodePrivateKey(keyData);
        String pubKey = getAccountIdFromPrivateKey(CryptoBase58Encoding.encodePrivateKey(keyData));
        return new Pair<>(pvtKeyStr, pubKey);
    }

    public static Pair<String, String> generateWallet() {
        Random rd = new Random();
        byte[] arr = new byte[32];
        rd.nextBytes(arr);
        return generateWallet(arr);
    }
}
