package com.tpximpact.url_shortener.util;

import java.net.URI;

public class ValidatorHelper {

    public static boolean isValidUrl(String url){
        try {
            URI uri = URI.create(url);

            String scheme = uri.getScheme();

            return ("http".equalsIgnoreCase(scheme)
                    || "https".equalsIgnoreCase(scheme))
                    && uri.getHost() != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getUrlProtocol(String url){
        try {
            URI uri = URI.create(url);

            return uri.getScheme();
        } catch (Exception e) {
            return "";
        }
    }
}
