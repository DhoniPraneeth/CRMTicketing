package com.example.CRMTicketing.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
    private final LocalDateTime timestamp;

    private final String message;

    public ErrorResponse(String message) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }
}
