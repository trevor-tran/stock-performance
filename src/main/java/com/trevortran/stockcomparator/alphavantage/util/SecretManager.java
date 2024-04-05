package com.trevortran.stockcomparator.alphavantage.util;

public class SecretManager {
    private static final String ALPHA_VANTAGE_KEY = "9550BIKHH601BM7H";
//        private static final String ALPHA_VANTAGE_KEY = "PDOGDFRY2VU8A943";

    public static String getSecretKey() {
        return ALPHA_VANTAGE_KEY;
    }
}
