package com.lyrawallet.Util;

import com.lyrawallet.Crypto.CryptoBase58Encoding;
import com.lyrawallet.Global;
import com.lyrawallet.GlobalLyra;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.web3j.crypto.Hash;
import org.web3j.utils.Numeric;

public class ExternTokenAddressValidator {
    public static boolean tron(String accountId) {
        if(accountId.length() < 1)
            return false;
        if (accountId.charAt(0) != Global.TRON_ADDRESSPREFIX)
            return false;
        byte[] res = CryptoBase58Encoding.decodeWithCheckSum(accountId);
        if(res == null) {
            return false;
        }
        return res.length != 0;
    }

    static String checkedAddress(final String address) {
        final String cleanAddress = Numeric.cleanHexPrefix(address).toLowerCase();
        //
        StringBuilder o = new StringBuilder();
        String keccak = Hash.sha3String(cleanAddress);
        char[] checkChars = keccak.substring(2).toCharArray();

        char[] cs = cleanAddress.toLowerCase().toCharArray();
        for (int i = 0; i < cs.length; i++) {
            char c = cs[i];
            c = (Character.digit(checkChars[i], 16) & 0xFF) > 7 ? Character.toUpperCase(c) : Character.toLowerCase(c);
            o.append(c);
        }
        return Numeric.prependHexPrefix(o.toString());
    }

    public static boolean ethereum(final String address) {
        return Numeric.prependHexPrefix(address).equals(checkedAddress(address));
    }
}
