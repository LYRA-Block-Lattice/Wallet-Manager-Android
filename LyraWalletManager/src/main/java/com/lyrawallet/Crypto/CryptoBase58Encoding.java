package com.lyrawallet.Crypto;

import com.lyrawallet.GlobalLyra;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class CryptoBase58Encoding {
    private static final char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz"
            .toCharArray();
    private static final int BASE_58 = ALPHABET.length;
    private static final int BASE_256 = 256;

    private static final int[] INDEXES = new int[128];
    static {
        Arrays.fill(INDEXES, -1);
        for (int i = 0; i < ALPHABET.length; i++) {
            INDEXES[ALPHABET[i]] = i;
        }
    }



    public static String encode(byte[] input) {
        if (input == null) {
            // paying with the same coin
            return null;
        }

        //
        // Make a copy of the input since we are going to modify it.
        //
        input = copyOfRange(input, 0, input.length);

        //
        // Count leading zeroes
        //
        int zeroCount = 0;
        while (zeroCount < input.length && input[zeroCount] == 0) {
            ++zeroCount;
        }

        //
        // The actual encoding
        //
        byte[] temp = new byte[input.length * 2];
        int j = temp.length;

        int startAt = zeroCount;
        while (startAt < input.length) {
            byte mod = divmod58(input, startAt);
            if (input[startAt] == 0) {
                ++startAt;
            }

            temp[--j] = (byte) ALPHABET[mod];
        }

        //
        // Strip extra '1' if any
        //
        while (j < temp.length && temp[j] == ALPHABET[0]) {
            ++j;
        }

        //
        // Add as many leading '1' as there were leading zeros.
        //
        while (--zeroCount >= 0) {
            temp[--j] = (byte) ALPHABET[0];
        }

        byte[] output = copyOfRange(temp, j, temp.length);
        return new String(output);
    }

    public static byte[] decode(String input) {
        if (input == null) {
            // paying with the same coin
            return null;
        }

        byte[] input58 = new byte[input.length()];
        //
        // Transform the String to a base58 byte sequence
        //
        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);

            int digit58 = -1;
            if (c >= 0 && c < 128) {
                digit58 = INDEXES[c];
            }
            if (digit58 < 0) {
                return null;
            }

            input58[i] = (byte) digit58;
        }

        //
        // Count leading zeroes
        //
        int zeroCount = 0;
        while (zeroCount < input58.length && input58[zeroCount] == 0) {
            ++zeroCount;
        }

        //
        // The encoding
        //
        byte[] temp = new byte[input.length()];
        int j = temp.length;

        int startAt = zeroCount;
        while (startAt < input58.length) {
            byte mod = divmod256(input58, startAt);
            if (input58[startAt] == 0) {
                ++startAt;
            }

            temp[--j] = mod;
        }

        //
        // Do no add extra leading zeroes, move j to first non null byte.
        //
        while (j < temp.length && temp[j] == 0) {
            ++j;
        }

        return copyOfRange(temp, j - zeroCount, temp.length);
    }

    private static byte divmod58(byte[] number, int startAt) {
        int remainder = 0;
        for (int i = startAt; i < number.length; i++) {
            int digit256 = (int) number[i] & 0xFF;
            int temp = remainder * BASE_256 + digit256;

            number[i] = (byte) (temp / BASE_58);

            remainder = temp % BASE_58;
        }

        return (byte) remainder;
    }

    private static byte divmod256(byte[] number58, int startAt) {
        int remainder = 0;
        for (int i = startAt; i < number58.length; i++) {
            int digit58 = (int) number58[i] & 0xFF;
            int temp = remainder * BASE_58 + digit58;

            number58[i] = (byte) (temp / BASE_256);

            remainder = temp % BASE_256;
        }

        return (byte) remainder;
    }

    private static byte[] copyOfRange(byte[] source, int from, int to) {
        byte[] range = new byte[to - from];
        System.arraycopy(source, from, range, 0, range.length);

        return range;
    }


    public static byte[] getCheckSum(byte[] data) {
        if(data == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash1 = md.digest(data);
            md.reset();
            byte[] hash2 = md.digest(hash1);
            return Arrays.copyOf(hash2, GlobalLyra.LYRA_CHECKSUM_SIZE_IN_BYTES);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("ERROR: Checksum" + e.toString());
            return null;
        }
    }

    public static byte[] verifyAndRemoveCheckSum(byte[] data) {
        if(data.length < GlobalLyra.LYRA_CHECKSUM_SIZE_IN_BYTES || data == null) {
            return null;
        }
        byte[] tmpData = Arrays.copyOf(data, data.length - GlobalLyra.LYRA_CHECKSUM_SIZE_IN_BYTES);
        byte[] checkSumCalc = getCheckSum(tmpData);
        byte[] checkSumData = Arrays.copyOfRange(data, data.length - GlobalLyra.LYRA_CHECKSUM_SIZE_IN_BYTES, data.length);
        if(Arrays.equals(checkSumCalc, checkSumData)) {
            return Arrays.copyOf(tmpData, tmpData.length);
        }
        return null;
    }

    public static byte[] addCheckSum(byte[] data) {
        byte[] checkSum = getCheckSum(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write(data, 0, data.length);
        outputStream.write(checkSum, 0, checkSum.length);
        return outputStream.toByteArray();
    }

    public static String encodeWithCheckSum(byte[] data) {
        byte[] tmpData = addCheckSum(data);
        return encode(tmpData);
    }

    public static String encodePrivateKey(byte[] privKey) {
        return encodeWithCheckSum(privKey);
    }

    public static String encodePublicKey(byte[] pubKey) {
        return encodeWithCheckSum(pubKey);
    }

    public static String encodeAccountId(byte[] pubKey) {
        String tmp = encodeWithCheckSum(pubKey);
        if (tmp.length() == 0)
            return null;
        return GlobalLyra.ADDRESSPREFIX + tmp;
    }

    public static byte[] decodeWithCheckSum(String data) {
        byte[] dat = decode(data);
        if(dat == null)
            return null;
        return verifyAndRemoveCheckSum(dat);
    }

    public static byte[] DecodePrivateKey(String privKey) {
        return decodeWithCheckSum(privKey);
    }

    public static byte[] DecodePublicKey(String pubKey) {
        return decodeWithCheckSum(pubKey);
    }

    public static byte[] DecodeAccountId(String accountId) {
        if(accountId.length() < 1 || accountId == null) {
            return null;
        }
        return decodeWithCheckSum(accountId.substring(1));
    }
}
