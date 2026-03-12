package com.osondoson.backend.common.exception;

import com.osondoson.backend.enums.message.FailMessage;
import lombok.Getter;

@Getter
public class OsondosonException extends RuntimeException {

    private final FailMessage failMessage;

    public OsondosonException(final FailMessage failMessage) {
        super(failMessage.getMessage());
        this.failMessage = failMessage;
    }
}
