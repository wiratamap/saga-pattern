package com.personal.sagapattern.core.exception;

public class ExceededBalanceException extends RuntimeException {
    public ExceededBalanceException(String message) {
        super(message);
    }
}
