package com.synchrolog.servlet;

import com.synchrolog.client.SynchrologHttpClient;
import com.synchrolog.logger.LoggerContext;
import com.synchrolog.logger.LoggerContextManager;
import com.synchrolog.logger.LoggerContextParams;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

public class MiddlewareFilter implements Filter {

    private static final String SYNCHROLOG_DEFAULT_HOST = "https://input.synchrolog.com";
    private static final String CONFIG_PROP_PREFIX = "synchrolog.";
    private SynchrologHttpClient synchrologClient;
    private boolean disableDefaultRequestLogging;
    private String requestAlreadyFilteredAttrKey;

    @Override
    public void init(FilterConfig config) throws ServletException {

        String apiKey = getConfigProperty(config, "apiKey");
        if (apiKey == null || apiKey.trim().length() == 0) {
            throw new ServletException("Missing Synchrolog API key. Please add init parameter 'apiKey' to MiddlewareFilter.");
        }

        String host = getConfigProperty(config, "host");
        if (host == null || host.trim().length() == 0) {
            host = SYNCHROLOG_DEFAULT_HOST;
        }

        String disableDefaultRequestLogging = getConfigProperty(config, "disableDefaultRequestLogging");
        if (disableDefaultRequestLogging != null && disableDefaultRequestLogging.trim().length() > 0) {
            this.disableDefaultRequestLogging = Boolean.parseBoolean(disableDefaultRequestLogging);
        }

        this.synchrologClient = new SynchrologHttpClient(apiKey, host);
        this.requestAlreadyFilteredAttrKey = config.getFilterName() + ".FILTERED";
    }

    private String getConfigProperty(FilterConfig config, String s) {
        String propVal = System.getProperty(CONFIG_PROP_PREFIX + s);
        if (propVal == null) {
            propVal = config.getInitParameter(s);
        }
        return propVal;
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        final Object isRequestAlreadyFiltered = request.getAttribute(requestAlreadyFilteredAttrKey);
        if(isRequestAlreadyFiltered != null) {
            filterChain.doFilter(request, response);
            return;
        }

        if ("/synchrolog-time".equals(request.getRequestURI())) {
            response.getWriter().printf("{ \"time\": \"%s\" }", DateTimeFormatter.ISO_INSTANT.format(Instant.now()));
            return;
        }

        final Optional<String> optionalAnonId = getCookieValue(request, "synchrolog_anonymous_id");
        String anonId = optionalAnonId.orElse(createSecureRandomHex());
        if (!optionalAnonId.isPresent()) {
            response.addCookie(new Cookie("synchrolog_anonymous_id", anonId));
        }

        final String userId = getCookieValue(request, "synchrolog_user_id").orElse(null);

        final LoggerContextParams params = new LoggerContextParams(anonId, userId, request.getRequestURI());
        final LoggerContext loggerContext = new LoggerContext(params, this.synchrologClient);
        LoggerContextManager.set(loggerContext);

        try {
            request.setAttribute(requestAlreadyFilteredAttrKey, Boolean.TRUE);
            filterChain.doFilter(request, response);
            if(!disableDefaultRequestLogging) {
                synchrologClient.sendRequest(params, request, response);
            }
        } catch (Exception ex) {
            if(!disableDefaultRequestLogging) {
                synchrologClient.sendError(params, request, response, ex);
            }
            throw ex;
        } finally {
            request.removeAttribute(requestAlreadyFilteredAttrKey);
            LoggerContextManager.set(null);
        }
    }

    @Override
    public void destroy() {

    }

    private static Optional<String> getCookieValue(HttpServletRequest request, String key) {
        return Arrays.stream(request.getCookies())
                .filter(c -> key.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    private static String createSecureRandomHex() {
        byte[] resBuf = new byte[16];
        new SecureRandom().nextBytes(resBuf);
        return bytesToHex(resBuf);
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
