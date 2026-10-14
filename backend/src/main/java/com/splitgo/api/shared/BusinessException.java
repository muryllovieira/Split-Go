package com.splitgo.api.shared;

public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}