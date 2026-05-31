package com.tpximpact.url_shortener.controller;

import com.tpximpact.url_shortener.exception.AliasDoesNotExistException;
import com.tpximpact.url_shortener.exception.DuplicateAliasException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(AliasDoesNotExistException.class)
    public ResponseEntity<Object> handleAliasNotFound(AliasDoesNotExistException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(DuplicateAliasException.class)
    public ResponseEntity<Object> handleDuplicateAliasException(DuplicateAliasException ex){
        return ResponseEntity.status(400).body(ex.getMessage());
    }
}
