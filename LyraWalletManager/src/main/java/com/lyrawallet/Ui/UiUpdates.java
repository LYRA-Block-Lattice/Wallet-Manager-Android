package com.lyrawallet.Ui;

import org.json.JSONException;
import org.json.JSONObject;

public class UiUpdates {
    public static class PoolData {
        private long LastUpdated = 0;
        private String PoolId = null;
        private long Height = 0;
        private String Token0 = null;
        private double Token0Balance = 0f;
        private String Token1 = null;
        private double Token1Balance = 0f;

        public PoolData(String jsonData) throws JSONException {
            JSONObject obj = new JSONObject(jsonData);
            LastUpdated = System.currentTimeMillis();
            PoolId = obj.getString("poolId");
            Token0 = obj.getString("token0");
            Token1 = obj.getString("token1");
            Height = obj.getLong("height");
            JSONObject objBalance = obj.getJSONObject("balance");
            Token0Balance = objBalance.getDouble(Token0);
            Token1Balance = objBalance.getDouble(Token1);
        }
        public long getLastUpdated() { return LastUpdated; }
        public String getPoolId() { return PoolId; }
        public long getHeight() { return Height; }
        public String getToken0() { return Token0; }
        public double getToken0Balance() { return Token0Balance; }
        public String getToken1() { return Token1; }
        public double getToken1Balance() { return Token1Balance; }
        public boolean token0Is( String ticker) {
            try {
                return ticker.equals(Token0);
            } catch (NullPointerException ignored) {}
            return false;
        }
        public boolean token1Is( String ticker) {
            try {
                return ticker.equals(Token1);
            } catch (NullPointerException ignored) {}
            return false;
        }
    }
    public static class PoolCalculateData {
        private long LastUpdated = 0;
        String SwapInToken = null;
        double SwapInAmount = 0f;
        String SwapOutToken = null;
        double SwapOutAmount = 0f;
        double Price = 0f;
        double PriceImpact = 0f;
        double MinimumReceived = 0f;
        double PayToProvider = 0f;
        double PayToAuthorizer = 0f;

        public PoolCalculateData(String jsonData) throws JSONException {
            JSONObject obj = new JSONObject(jsonData);
            LastUpdated = System.currentTimeMillis();
            SwapInToken = obj.getString("SwapInToken");
            SwapInAmount = obj.getDouble("SwapInAmount");
            SwapOutToken = obj.getString("SwapOutToken");
            SwapOutAmount = obj.getDouble("SwapOutAmount");
            Price = obj.getDouble("Price");
            PriceImpact = obj.getDouble("PriceImpact");
            MinimumReceived = obj.getDouble("MinimumReceived");
            PayToProvider = obj.getDouble("PayToProvider");
            PayToAuthorizer = obj.getDouble("PayToAuthorizer");
        }

        public long getLastUpdated() { return LastUpdated; }
        public String getSwapInToken() { return SwapInToken; }
        public double getSwapInAmount() { return SwapInAmount; }
        public String getSwapOutToken() { return SwapOutToken; }
        public double getSwapOutAmount() { return SwapOutAmount; }
        public double getPrice() { return Price; }
        public double getPriceImpact() { return PriceImpact; }
        public double getMinimumReceived() { return MinimumReceived; }
        public double getPayToProvider() { return PayToProvider; }
        public double getPayToAuthorizer() { return PayToAuthorizer; }
    }

    static PoolData poolData = null;
    static PoolCalculateData poolCalculateData = null;

    public static void setPoolData(PoolData data) { poolData = data ; }
    public static PoolData getPoolData() { return poolData; }

    public static void setPoolCalculateData(PoolCalculateData data) { poolCalculateData = data ; }
    public static PoolCalculateData getPoolCalculateData() { return poolCalculateData; }
}
