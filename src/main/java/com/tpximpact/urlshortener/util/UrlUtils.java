package com.tpximpact.urlshortener.util;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class UrlUtils {

    public static String generateAlias() {
        return UUID.randomUUID().toString().substring(0, 6);
    }
}
