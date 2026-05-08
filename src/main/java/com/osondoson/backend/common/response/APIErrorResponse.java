package com.osondoson.backend.common.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record APIErrorResponse(
        String code,
        String message
) {
    public static ResponseEntity<APIErrorResponse> of(final HttpStatus httpStatus, final String code, final String message) {
        return ResponseEntity.status(httpStatus).body(new APIErrorResponse(code, message));
    }
}
