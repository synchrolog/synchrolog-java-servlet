package com.synchrolog.client;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.synchrolog.logger.LoggerContextParams;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;

public class SynchrologHttpClient {

    private final String apiKey;
    private final String host;
    private JsonFactory jsonFac = new JsonFactory();

    public SynchrologHttpClient(String apiKey, String host) {
        this.apiKey = apiKey;
        this.host = host;
    }

    public void sendError(LoggerContextParams contextParams, HttpServletRequest request, HttpServletResponse response, Throwable throwable) {
        final ErrorContext errCtx = new ErrorContext(
                request.getRemoteAddr(),
                String.valueOf(response.getStatus()),
                request.getHeader("User-Agent"));
        doRequestAsync("/v1/track-backend-error", outputStream -> {
            createErrorMessage(contextParams, errCtx, throwable, outputStream);
        });
    }

    public void sendRequest(LoggerContextParams contextParams, HttpServletRequest request, HttpServletResponse response) {
        final String message = requestToString(request, response);
        doRequestAsync("/v1/track-backend", outputStream -> {
            createLogMessage(contextParams, message, outputStream);
        });
    }

    public void sendLog(LoggerContextParams contextParams, String message) {
        doRequestAsync("/v1/track-backend", outputStream -> {
            createLogMessage(contextParams, message, outputStream);
        });
    }

    private String requestToString(HttpServletRequest request, HttpServletResponse response) {
        // ":remote-addr - :remote-user [:date[clf]] \":method :url HTTP/:http-version\" :status :res[content-length] \":referrer\" \":user-agent\""
        final String referer = request.getHeader("Referer");
        final String userAgent = request.getHeader("User-Agent");
        final String remoteUser = request.getRemoteUser();
        return String.format("%s - %s \"%s %s %s\" %s \"%s\" \"%s\"",
                request.getRemoteAddr(),
                remoteUser != null ? remoteUser : "",
                request.getMethod(),
                request.getRequestURI(),
                request.getProtocol(),
                response.getStatus(),
                referer != null ? referer : "",
                userAgent != null ? userAgent : ""
        );
    }

    private void doRequestAsync(String urlPath, Consumer<OutputStream> requestBodyCallback) {
        ForkJoinPool.commonPool().execute(() -> {
            try {
                URL url = new URL(host + urlPath);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Authorization", String.format("Basic %s", apiKey));
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                try (final OutputStream out = conn.getOutputStream()) {
                    requestBodyCallback.accept(out);
                }
                int httpStatus = conn.getResponseCode();
                if (httpStatus != HttpURLConnection.HTTP_OK
                        && httpStatus != HttpURLConnection.HTTP_CREATED) {
                    String body = "";
                    try (InputStream in = conn.getInputStream()) {
                        byte[] buf = new byte[1024];
                        in.read(buf);
                        body = new String(buf, StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.printf("Synchrolog send failed. Server responded: %s %s", httpStatus, body);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void createErrorMessage(LoggerContextParams context,
                                    ErrorContext errorEvent,
                                    Throwable throwable,
                                    OutputStream out) {
        try {
            final JsonGenerator jsonGen = jsonFac.createGenerator(out);
            jsonGen.writeStartObject();
            addCommonParams(context, "error", Instant.now(), jsonGen);
            final StringWriter stringWriter = new StringWriter();
            final PrintWriter stackTraceWriter = new PrintWriter(stringWriter);
            throwable.printStackTrace(stackTraceWriter);

            jsonGen.writeObjectFieldStart("error");
            jsonGen.writeStringField("status", errorEvent.status);
            jsonGen.writeStringField("description", throwable.getMessage());
            jsonGen.writeStringField("backtrace", stringWriter.toString());
            jsonGen.writeStringField("ip_address", errorEvent.remoteAddr);
            jsonGen.writeStringField("user_agent", errorEvent.userAgent);
            jsonGen.writeStringField("file_name", throwable.getStackTrace()[0].getFileName());
            jsonGen.writeNumberField("line_number", throwable.getStackTrace()[0].getLineNumber());
            jsonGen.writeEndObject();

            jsonGen.writeEndObject();
            jsonGen.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createLogMessage(LoggerContextParams context, String message, OutputStream out) {
        try {
            final Instant now = Instant.now();
            final String timestamp = DateTimeFormatter.ISO_INSTANT.format(now);
            final JsonGenerator jsonGen = jsonFac.createGenerator(out);
            jsonGen.writeStartObject();
            addCommonParams(context, "log", now, jsonGen);
            jsonGen.writeObjectFieldStart("log");
            jsonGen.writeStringField("timestamp", timestamp);
            jsonGen.writeStringField("message", message);
            jsonGen.writeStringField("request_path", context.requestUrl);
            jsonGen.writeEndObject();

            jsonGen.writeEndObject();
            jsonGen.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addCommonParams(LoggerContextParams context,
                                 String eventType,
                                 Instant timestamp,
                                 JsonGenerator jsonGen) throws IOException {

        jsonGen.writeStringField("event_type", eventType);
        jsonGen.writeStringField("timestamp", DateTimeFormatter.ISO_INSTANT.format(timestamp));
        jsonGen.writeStringField("anonymous_id", context.anonymousId);
        jsonGen.writeStringField("user_id", context.userId);
        jsonGen.writeStringField("source", "backend");
        jsonGen.writeStringField("api_key", apiKey);
    }
}
