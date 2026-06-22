package com.jpximpact.url_shortener_service.exception;

public final class UrlShortenerExceptions {

    private UrlShortenerExceptions() {}

    public static class AliasNotFoundException extends RuntimeException {
        public AliasNotFoundException(String message) { super(message); }
    }

    public static class AliasAlreadyTakenException extends RuntimeException {
        public AliasAlreadyTakenException(String message) { super(message); }
    }

    public static class InvalidUrlException extends RuntimeException {
        public InvalidUrlException(String message) { super(message); }
    }

    public static class InvalidAliasException extends RuntimeException {
        public InvalidAliasException(String message) { super(message); }
    }
}
