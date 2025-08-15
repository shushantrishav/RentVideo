package com.rentvideo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // or FORBIDDEN
public class RentalException extends RuntimeException {
    public RentalException(String message) {
        super(message);
    }
}
