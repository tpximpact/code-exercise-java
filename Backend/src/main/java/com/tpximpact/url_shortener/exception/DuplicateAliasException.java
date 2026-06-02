package com.tpximpact.url_shortener.exception;

public class DuplicateAliasException extends RuntimeException{
    public DuplicateAliasException(String errorMessage){
        super(errorMessage);
    }
}
