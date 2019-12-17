package com.synchrolog.logger;

public class LoggerContextParams {

    public String anonymousId;
    public String userId;
    public String requestUrl;

    public LoggerContextParams(String anonymousId, String userId, String requestUrl) {
        this.anonymousId = anonymousId;
        this.userId = userId;
        this.requestUrl = requestUrl;
    }
}
