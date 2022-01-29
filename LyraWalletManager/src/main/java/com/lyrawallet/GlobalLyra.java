package com.lyrawallet;

import static com.lyrawallet.R.mipmap.ic_eth_foreground;
import static com.lyrawallet.R.mipmap.ic_lyra_foreground;
import static com.lyrawallet.R.mipmap.ic_tron_foreground;
import static com.lyrawallet.R.mipmap.ic_unknown_foreground;
import static com.lyrawallet.R.mipmap.ic_usdc_foreground;
import static com.lyrawallet.R.mipmap.ic_usdt_foreground;

import android.util.Pair;

public class GlobalLyra {
    public static final int LYRA_CHECKSUM_SIZE_IN_BYTES        = 4;
    public static final char ADDRESSPREFIX                     = 'L';
    public static final long  LYRA_MAX_SUPPLY                  = 10_000_000_000L;
    public static final double LYRA_TX_FEE                     = 1.0;
    public static final String BREAKING_STAKE_CONTRACT_FEE     = "0.8";
    public static final String SYMBOL_FOR_TETHERED_TOKEN       = "$";
    public static final String LYRA_RPC_API_URL                =  "/api/v1/socket";
    public static final String LYRA_NODE_API_URL                = "/api/Node";



    public final static Pair<String, Integer>[] TokenIconList = new Pair[]{
            new Pair<>("", 0),
            new Pair<>("UNKNOWN", ic_unknown_foreground),
            new Pair<>("LYR", ic_lyra_foreground),
            new Pair<>("$TRX", ic_tron_foreground),
            new Pair<>("$USDT", ic_usdt_foreground),
            new Pair<>("$ETH", ic_eth_foreground),
            new Pair<>("$USDC", ic_usdc_foreground),
    };

    public static String symbolToDomain(String token) {
        if(token == null)
            return null;
        return token.replace("$", "tether/");
    }
    public static String domainToSymbol(String domain) {
        if(domain == null)
            return null;
        return domain.replace("tether/", "$");
    }
}
