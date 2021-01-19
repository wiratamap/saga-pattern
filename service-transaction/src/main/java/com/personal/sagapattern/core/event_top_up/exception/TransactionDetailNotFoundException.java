package com.personal.sagapattern.core.event_top_up.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TransactionDetailNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -7019962009670351555L;
}
