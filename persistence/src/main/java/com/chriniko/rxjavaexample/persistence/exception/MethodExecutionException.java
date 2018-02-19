package com.chriniko.rxjavaexample.persistence.exception;

public class MethodExecutionException extends RuntimeException {

    public MethodExecutionException(String message, Throwable error) {
        super(message, error);
    }

    public MethodExecutionException(String message) {
        super(message);
    }
}
