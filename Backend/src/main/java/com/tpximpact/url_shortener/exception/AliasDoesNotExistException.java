package com.tpximpact.url_shortener.exception;

public class AliasDoesNotExistException extends RuntimeException{
    public AliasDoesNotExistException(String errorMessage){
        super(errorMessage);
    }
}