package com.synchrolog.client;

public class SynchrologHttpClientSingleton {

    private static final String DEFAULT_PROD_HOST = "https://input.synchrolog.com";
    private static final Object lock = new Object();

    private static SynchrologHttpClient instance;

    public static SynchrologHttpClient getInstance() {
        if (instance != null) {
            return instance;
        }

        synchronized (lock) {
            instance = createSynchrologHttpClient();
        }
        return instance;
    }

    private static SynchrologHttpClient createSynchrologHttpClient() {

        String apiKey = System.getProperty("synchrolog.apiKey");
        if(apiKey == null || apiKey.trim().length() == 0) {
            throw new RuntimeException("Missing Synchrolog API key. Please add system property 'synchrolog.apiKey'.");
        }

        String host = System.getProperty("synchrolog.host");
        if(host == null || host.trim().length() == 0) {
            host = DEFAULT_PROD_HOST;
        }

        return new SynchrologHttpClient(apiKey, host);
    }

}
