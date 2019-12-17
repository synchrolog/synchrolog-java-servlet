package com.synchrolog.client;

public class ErrorContext {

    String remoteAddr;
    String status;
    String userAgent;

    public ErrorContext(String remoteAddr, String status, String userAgent) {
        this.remoteAddr = remoteAddr;
        this.status = status;
        this.userAgent = userAgent;
    }
}
