package com.chriniko.rxjavaexample.connector.exception;

public class NewsApiConnectionException extends RuntimeException {

    public NewsApiConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
