package com.personal.sagapattern.core.exception;

public class AccountNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -2727614399718495382L;

    public AccountNotFoundException(String message) {
        super(message);
    }
}
