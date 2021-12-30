package com.lyrawallet.Crypto;

import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidKeySpecException;

public class JavaCryptoUtil {
    //private static byte[] P256_HEAD = Base58Encoding.Decode("7Y17FQ2tFjERXfrhWiK15SeP5AVSNdwzroacK"); \\Priv
    //private static byte[] P256_HEAD = Base58Encoding.Decode("4FKQkfAuH4FCiTX4c2C5Gthhaq4gXasUQpW2Pscggb2w"); \\ Pub

    public static PrivateKey PrivateKeyBytesToPrivateKey(byte[] pKeyBytes) throws InvalidKeySpecException {
        ECNamedCurveParameterSpec curve = ECNamedCurveTable.getParameterSpec("secp256r1");
        ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec(new BigInteger(1, pKeyBytes), curve);
        KeyFactory eckf;
        try {
            eckf = KeyFactory.getInstance("EC");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("EC key factory not present in runtime");
        }
        return eckf.generatePrivate(privateKeySpec);
    }

    public static byte[] PrivateBytesToPrivateKeyBytes(PrivateKey pKey) {
        return null;
    }

    public static ECPublicKey PublicKeyBytesToPublicKey(byte[] rawPublicKey) throws NoSuchAlgorithmException {
        byte[] byte0 = new byte[rawPublicKey.length+1];
        byte0[0] = 4;
        System.arraycopy(rawPublicKey, 0, byte0, 1, rawPublicKey.length);
        ECPublicKey ecPublicKey = null;
        ECNamedCurveParameterSpec ecNamedCurveParameterSpec = ECNamedCurveTable.getParameterSpec("secp256r1");
        ECCurve curve = ecNamedCurveParameterSpec.getCurve();
        EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, null);
        java.security.spec.ECPoint ecPoint = ECPointUtil.decodePoint(ellipticCurve, byte0);
        ECParameterSpec ecParameterSpec = EC5Util.convertSpec(ellipticCurve, ecNamedCurveParameterSpec);
        java.security.spec.ECPublicKeySpec publicKeySpec = new java.security.spec.ECPublicKeySpec(ecPoint, ecParameterSpec);
        KeyFactory kf = java.security.KeyFactory.getInstance("EC");
        try {
            ecPublicKey = (ECPublicKey) kf.generatePublic(publicKeySpec);
        } catch (Exception e) {
            System.out.println("Caught Exception public key: " + e.toString());
        }
        //JavaUtil.PublicKeyToPublicKey(id).getW().getAffineX().toByteArray();
        //JavaUtil.PublicKeyToPublicKey(id).getW().getAffineY().toByteArray();
        return ecPublicKey;
    }

    public static byte[] DerivePublicKeyBytes(byte[] privateKey) {
        if(privateKey.length != 32)
            return new byte[0];
        ECNamedCurveParameterSpec curve = ECNamedCurveTable.getParameterSpec("secp256r1");
        ECDomainParameters domain = new ECDomainParameters(curve.getCurve(), curve.getG(), curve.getN(), curve.getH());
        BigInteger d = new BigInteger(1, privateKey);
        ECPoint q = domain.getG().multiply(d);
        ECPublicKeyParameters publicKey = new ECPublicKeyParameters(q, domain);
        byte[] pubKeyBytes = new byte[64];
        System.arraycopy(publicKey.getQ().getRawXCoord().getEncoded(), 0, pubKeyBytes, 0, 32);
        System.arraycopy(publicKey.getQ().getRawYCoord().getEncoded(), 0, pubKeyBytes, 32, 32);
        return pubKeyBytes;
    }

    public static ECPublicKeyParameters PrivateKeyToPublicKey(byte[] privateKey) {
        ECNamedCurveParameterSpec curve = ECNamedCurveTable.getParameterSpec("secp256r1");
        ECDomainParameters domain = new ECDomainParameters(curve.getCurve(), curve.getG(), curve.getN(), curve.getH());
        BigInteger d = new BigInteger(1, privateKey);
        ECPoint q = domain.getG().multiply(d);
        return new ECPublicKeyParameters(q, domain);
        //System.arraycopy(publicKey.getQ().getRawXCoord().getEncoded(), 0, id, 1, 32);
        //System.arraycopy(publicKey.getQ().getRawYCoord().getEncoded(), 0, id, 33, 32);

    }
}
