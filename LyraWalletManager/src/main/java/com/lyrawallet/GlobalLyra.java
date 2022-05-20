package com.lyrawallet;

import static com.lyrawallet.R.mipmap.ic_eth_foreground;
import static com.lyrawallet.R.mipmap.ic_lyra_token_foreground;
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
    public static final String LYRA_RPC_API_URL                = "/api/v1/socket";
    public static final String LYRA_NODE_API_URL               = "/api/Node";
    public static final String LYRA_DEX_API_URL                = "https://dex.lyra.live/api/Dex";
    public static final int LYRA_STAKE_MIN_DAYS                = 3;
    public static final int LYRA_STAKE_MAX_DAYS                = 36500;



    public final static Pair<String, Integer>[] TickerIconList = new Pair[]{
            new Pair<>("", 0),
            new Pair<>("UNKNOWN", ic_unknown_foreground),
            new Pair<>("LYR", ic_lyra_token_foreground),
            new Pair<>("$TRX", ic_tron_foreground),
            new Pair<>("$USDT", ic_usdt_foreground),
            new Pair<>("$ETH", ic_eth_foreground),
            new Pair<>("$USDC", ic_usdc_foreground),
            new Pair<>("$LTT", ic_unknown_foreground),
            new Pair<>("$TLYR", ic_lyra_token_foreground),
    };

    public final static Pair<String, Integer>[] TokenTestnetIconList = new Pair[]{
            new Pair<>("", 0),
            new Pair<>("UNKNOWN", ic_unknown_foreground),
            new Pair<>("Lyra", ic_lyra_token_foreground),
            new Pair<>("Tron", ic_tron_foreground),
            new Pair<>("USDT", ic_usdt_foreground),
            new Pair<>("Ethereum (Rinkeby Testnet)", ic_eth_foreground),
            new Pair<>("USDC", ic_usdc_foreground),
            new Pair<>("Lyra Test Token", ic_unknown_foreground),
            new Pair<>("Lyra Tether on Ethereum", ic_lyra_token_foreground),
    };

    public final static Pair<String, Integer>[] TokenMainnetIconList = new Pair[]{
            new Pair<>("", 0),
            new Pair<>("UNKNOWN", ic_unknown_foreground),
            new Pair<>("Lyra", ic_lyra_token_foreground),
            new Pair<>("Tron", ic_tron_foreground),
            new Pair<>("USDT", ic_usdt_foreground),
            new Pair<>("Ethereum", ic_eth_foreground),
            new Pair<>("USDC", ic_usdc_foreground),
            new Pair<>("Lyra Test Token", ic_unknown_foreground),
            new Pair<>("Lyra Tether on Ethereum", ic_lyra_token_foreground),
    };

    public final static Pair<String, String>[] TickerTokenNameList = new Pair[]{
            new Pair<>("", "UNKNOWN"),
            new Pair<>("LYR", "Lyra"),
            new Pair<>("$LYR", "Lyra Test Token"),
            new Pair<>("TLYR", "Lyra Tether on Ethereum"),
            new Pair<>("$TLYR", "Lyra Tether on Ethereum"),
            new Pair<>("TRX", "Tron"),
            new Pair<>("$TRX", "Tron"),
            new Pair<>("USDT", "USDT"),
            new Pair<>("$USDT", "USDT on Tron"),
            new Pair<>("$ETH", "Ethereum"),
            new Pair<>("$USDC", "USDC on Tron"),
            new Pair<>("$LTT", "Lyra Test Token"),
            new Pair<>("ETH", "Ethereum"),
            new Pair<>("$ETH", "Ethereum"),
    };

    public static int getTokenImage(String token) {
        Pair<String, Integer>[] TokenIconList = TokenTestnetIconList;
        if(Global.getCurrentNetworkName().equals("MAINNET"))
            TokenIconList = TokenMainnetIconList;
        int icon = TokenIconList[1].second;
        for (Pair<String, Integer> k : TokenIconList) {
            if (k.first.equals(GlobalLyra.domainToSymbol(token))) {
                return k.second;
            }
        }
        return icon;
    }

    public static int getTickerImage(String token) {
        int icon = GlobalLyra.TickerIconList[1].second;
        for (Pair<String, Integer> k : GlobalLyra.TickerIconList) {
            if (k.first.equals(GlobalLyra.domainToSymbol(token))) {
                return k.second;
            }
        }
        return icon;
    }

    public static String tickerToTokenName(String ticker) {
        String token = GlobalLyra.TickerTokenNameList[0].second;
        for (int i = 0; i < GlobalLyra.TickerTokenNameList.length; i++) {
            if(GlobalLyra.TickerTokenNameList[i].first.equals(ticker)) {
                return GlobalLyra.TickerTokenNameList[i].second;
            }
        }
         return token;
    }

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

    public final static String[] ProfitingAccountTypes = new String[] {
            "NODE", "ORACLE", "MERCHANT", "YIELD"
    };

    public static String getProfitingAccountTypes(int type) {
        return ProfitingAccountTypes[type];
    }
    public static int getProfitingAccountTypes(String type) {
        switch (type) {
            case "NODE":
                return 0;
            case "ORACLE":
                return 1;
            case "MERCHANT":
                return 2;
            case "YIELD":
                return 3;
            default:
                return -1;
        }
    }
}
