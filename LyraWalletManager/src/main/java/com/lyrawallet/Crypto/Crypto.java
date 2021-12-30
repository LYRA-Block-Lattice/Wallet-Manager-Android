package com.lyrawallet.Crypto;
import java.nio.charset.StandardCharsets;

public class Crypto {
    public static byte[] Sign(byte[] message, byte[] prikey, byte[] pubkey) {
        return Signatures.GetSignature(new String(prikey), new String(message)).getBytes(StandardCharsets.UTF_8);
    }

    public static boolean VerifySignature(byte[] message, byte[] signature, byte[] pubkey) {
        return false;
    }
}
