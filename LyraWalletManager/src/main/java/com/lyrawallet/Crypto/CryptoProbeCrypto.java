package com.lyrawallet.Crypto;

import android.util.Pair;

public class CryptoProbeCrypto {
    public static void derivePublicFromPrivate() {
        byte[] keyBytes = CryptoBase58Encoding.DecodePrivateKey("dkrwRdqNjEEshpLuEPPqc6zM1HM3nzGjsYts39zzA1iUypcpj");
        System.out.println("pk, expected id: " +
                new Pair<>("dkrwRdqNjEEshpLuEPPqc6zM1HM3nzGjsYts39zzA1iUypcpj",
                "LUTG2E1mdpGk5Qtq9BUgwZDWhUeZc14Xfw2pAvAdKoacvgRBU3atwtrQeoY3evm5C7TXRz3Q5nwPEUHj9p7CBDE6kQTQMy"));
        System.out.println("pk, derived  id: " + CryptoSignatures.generateWallet(keyBytes));
    }
    public static void newWallet() {
        System.out.println("New :pk, id: " + CryptoSignatures.generateWallet());
    }

    public static void getSignature() {
        System.out.println("Signature: " + CryptoSignatures.getSignature("dkrwRdqNjEEshpLuEPPqc6zM1HM3nzGjsYts39zzA1iUypcpj",
                "H9m8Pe4Fejvb4ERNg4A2D1AqacpdhT1w62QBPTFAnFuW"));
    }

    public static void verifySignature() {
        System.out.println("Signature: " + CryptoSignatures.getSignature("dkrwRdqNjEEshpLuEPPqc6zM1HM3nzGjsYts39zzA1iUypcpj",
                "H9m8Pe4Fejvb4ERNg4A2D1AqacpdhT1w62QBPTFAnFuW"));
        System.out.println("Signature match? " + CryptoSignatures.verifyAccountSignature("H9m8Pe4Fejvb4ERNg4A2D1AqacpdhT1w62QBPTFAnFuW",
                "LUTG2E1mdpGk5Qtq9BUgwZDWhUeZc14Xfw2pAvAdKoacvgRBU3atwtrQeoY3evm5C7TXRz3Q5nwPEUHj9p7CBDE6kQTQMy",
                CryptoSignatures.getSignature("dkrwRdqNjEEshpLuEPPqc6zM1HM3nzGjsYts39zzA1iUypcpj", "H9m8Pe4Fejvb4ERNg4A2D1AqacpdhT1w62QBPTFAnFuW")));
    }

    public static void aesEncryptDecrypt() {
        String encrypted = CryptoAes256.encrypt("Pigs never die, they always get killed.", "one_password");
        System.out.println("Expected message:  " + "Pigs never die, they always get killed.");
        System.out.println("Decrypted message: " + CryptoAes256.decrypt(encrypted, "one_password"));
        System.out.println("Encoded message:   " + encrypted);
    }

}
