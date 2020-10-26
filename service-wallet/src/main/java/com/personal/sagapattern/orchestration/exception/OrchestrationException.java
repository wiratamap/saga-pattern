package com.personal.sagapattern.orchestration.exception;

public class OrchestrationException extends RuntimeException {
    private static final long serialVersionUID = -3155840539459623950L;

    public OrchestrationException(String message) {
        super(message);
    }
}
