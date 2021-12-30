package com.lyrawallet.Crypto;

import android.util.Pair;

import com.lyrawallet.LyraGlobal;

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

public class Signatures {
    static {
        Security.removeProvider("BC");
        // Confirm that positioning this provider at the end works for your needs!
        Security.addProvider(new BouncyCastleProvider());
    }


    public static boolean ValidateAccountId(String AccountId) {
        if (AccountId.charAt(0) != LyraGlobal.ADDRESSPREFIX)
            return false;
        byte[] res = Base58Encoding.DecodeAccountId(AccountId);
        if(res == null) {
            return false;
        }
        return res.length != 0;
    }

    public static boolean ValidatePublicKey(String PublicKey) {
        byte[] res = Base58Encoding.DecodePublicKey(PublicKey);
        if(res == null) {
            return false;
        }
        return res.length != 0;
    }

    public static boolean ValidatePrivateKey(String PrivateKey) {
        byte[] res = Base58Encoding.DecodePrivateKey(PrivateKey);
        if(res == null) {
            return false;
        }
        return res.length != 0;
    }

    public static boolean VerifyAccountSignature(String message, String accountId, String signature) {
        if (message.length() == 0 || !ValidateAccountId(accountId) || signature.length() == 0)
            return false;
        byte[] publicKeyBytes = Base58Encoding.DecodeAccountId(accountId);
        return VerifySignature(message, publicKeyBytes, signature);
    }

    public static boolean VerifyAuthorizerSignature(String message, String publicKey, String signature) {
        if (message.length() == 0 || !ValidateAccountId(publicKey) || signature.length() == 0)
            return false;
        byte[] publicKeyBytes = Base58Encoding.DecodePublicKey(publicKey);
        //try {
            return VerifySignature(message, publicKeyBytes, signature);
        //} catch (NoSuchAlgorithmException e) {
        //    e.printStackTrace();
        //}
        //return false;
    }

    private static boolean VerifySignature(String message, byte[] publicKeyBytes, String signature) {
        try {
            PublicKey pubKey = JavaCryptoUtil.PublicKeyBytesToPublicKey(publicKeyBytes);
            Signature sign = Signature.getInstance("SHA256withECDSA");
            sign.initVerify(pubKey);
            sign.update(message.getBytes(StandardCharsets.UTF_8));
            byte[] signnatureBytes = Base58Encoding.Decode(signature);
            return sign.verify(SignatureHelper.derSign(signnatureBytes));
        } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String GetSignature(String privateKey, String message, String accountId) {
        return GetSignature(privateKey, message);
    }

    public static String GetSignature(String privateKey, String message)  {
        try {
            byte[] keyBytes = Base58Encoding.DecodePrivateKey(privateKey);
            PrivateKey privKey = JavaCryptoUtil.PrivateKeyBytesToPrivateKey(keyBytes);
            Signature signer = Signature.getInstance("SHA256withECDSA");
            //System.out.println(signer.getProvider());

            signer.initSign(privKey/*, new SecureRandom()*/);
            byte[] signature;
            //do {
                signer.update(message.getBytes(StandardCharsets.UTF_8));
                byte[] signedBytes = signer.sign();
                signature = SignatureHelper.ConvertDerToP1393(signedBytes);
            //}while(signature[0] == 0);
            return Base58Encoding.Encode(signature);
        } catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException | InvalidKeySpecException | IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static byte[] DerivePublicKeyBytes(String privateKey) {
        if(!ValidatePrivateKey(privateKey))
            return new byte[0];
        byte[] keyBytes = Base58Encoding.DecodePrivateKey(privateKey);
        return JavaCryptoUtil.DerivePublicKeyBytes(keyBytes);
    }

    public static String GetAccountIdFromPrivateKey(String privateKey) {
        return Base58Encoding.EncodeAccountId(DerivePublicKeyBytes(privateKey));
    }

    public static String GetPublicKeyFromPrivateKey(String privateKey) {
        return Base58Encoding.EncodePublicKey(DerivePublicKeyBytes(privateKey));
    }

    public static String GeneratePrivateKey() {
        return null;
    }

    public static Pair<String, String> GenerateWallet(byte[] keyData) {
        String pvtKeyStr = Base58Encoding.EncodePrivateKey(keyData);
        String pubKey = GetAccountIdFromPrivateKey(Base58Encoding.EncodePrivateKey(keyData));
        return new Pair<>(pvtKeyStr, pubKey);
    }

    public static Pair<String, String> GenerateWallet() {
        Random rd = new Random();
        byte[] arr = new byte[32];
        rd.nextBytes(arr);
        return GenerateWallet(arr);
    }
}
