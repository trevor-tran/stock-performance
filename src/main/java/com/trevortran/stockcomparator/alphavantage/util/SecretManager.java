package com.trevortran.stockcomparator.alphavantage.util;

public class SecretManager {
        private static final String ALPHA_VANTAGE_KEY = "<api key here>";

    public static String getApiKey() {
        return ALPHA_VANTAGE_KEY;
    }
}
