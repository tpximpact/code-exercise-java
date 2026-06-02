package com.tpximpact.url_shortener.exception;

public class UnacceptableUrlException extends RuntimeException{
    public UnacceptableUrlException(String errorMessage){
        super(errorMessage);
    }
}