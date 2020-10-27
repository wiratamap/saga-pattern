package com.personal.servicedlqplatform.core.deadletter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DeadLetterNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -4803859770120076490L;

    public DeadLetterNotFoundException(String message) {
        super(message);
    }
}
