package com.lyrawallet.Crypto;

import android.util.Pair;

public class ProbeCrypto {
    public static void DerivePublicFromPrivate() {
        byte[] keyBytes = Base58Encoding.DecodePrivateKey("dkrwRdqNjEEshpLuEPPqc6zM1HM3nzGjsYts39zzA1iUypcpj");
        System.out.println("pk, expected id: " +
                new Pair<>("dkrwRdqNjEEshpLuEPPqc6zM1HM3nzGjsYts39zzA1iUypcpj",
                "LUTG2E1mdpGk5Qtq9BUgwZDWhUeZc14Xfw2pAvAdKoacvgRBU3atwtrQeoY3evm5C7TXRz3Q5nwPEUHj9p7CBDE6kQTQMy"));
        System.out.println("pk, derived  id: " + Signatures.GenerateWallet(keyBytes));
    }
    public static void NewWallet() {
        System.out.println("New :pk, id: " + Signatures.GenerateWallet());
    }

    public static void GetSignature() {
        System.out.println("Signature: " + Signatures.GetSignature("dkrwRdqNjEEshpLuEPPqc6zM1HM3nzGjsYts39zzA1iUypcpj",
                "H9m8Pe4Fejvb4ERNg4A2D1AqacpdhT1w62QBPTFAnFuW"));
    }

    public static void VerifySignature() {
        System.out.println("Signature: " + Signatures.GetSignature("dkrwRdqNjEEshpLuEPPqc6zM1HM3nzGjsYts39zzA1iUypcpj",
                "H9m8Pe4Fejvb4ERNg4A2D1AqacpdhT1w62QBPTFAnFuW"));
        System.out.println("Signature match? " + Signatures.VerifyAccountSignature("H9m8Pe4Fejvb4ERNg4A2D1AqacpdhT1w62QBPTFAnFuW",
                "LUTG2E1mdpGk5Qtq9BUgwZDWhUeZc14Xfw2pAvAdKoacvgRBU3atwtrQeoY3evm5C7TXRz3Q5nwPEUHj9p7CBDE6kQTQMy",
                Signatures.GetSignature("dkrwRdqNjEEshpLuEPPqc6zM1HM3nzGjsYts39zzA1iUypcpj", "H9m8Pe4Fejvb4ERNg4A2D1AqacpdhT1w62QBPTFAnFuW")));
    }

    public static void AesEncryptDecrypt() {
        String encrypted = Aes256.encrypt("Pigs never die, they always get killed.", "one_password");
        System.out.println("Expected message:  " + "Pigs never die, they always get killed.");
        System.out.println("Decrypted message: " + Aes256.decrypt(encrypted, "one_password"));
        System.out.println("Encoded message:   " + encrypted);
    }

}
