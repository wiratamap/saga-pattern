package com.personal.sagapattern.core.exception;

public class ExceededBalanceException extends RuntimeException {
    private static final long serialVersionUID = -8098313986792159623L;

    public ExceededBalanceException(String message) {
        super(message);
    }
}
