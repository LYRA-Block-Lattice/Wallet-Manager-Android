package com.lyrawallet.Crypto;
import java.nio.charset.StandardCharsets;

public class Crypto {
    public static byte[] sign(byte[] message, byte[] prikey, byte[] pubkey) {
        return CryptoSignatures.getSignature(new String(prikey), new String(message)).getBytes(StandardCharsets.UTF_8);
    }

    public static boolean verifySignature(byte[] message, byte[] signature, byte[] pubkey) {
        return false;
    }
}
