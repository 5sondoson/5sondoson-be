package com.osondoson.backend.admin.auth;

import com.osondoson.backend.common.exception.OsondosonException;
import com.osondoson.backend.enums.message.FailMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AdminTokenValidator {

    private final String adminToken;

    public AdminTokenValidator(@Value("${admin.token}") final String adminToken) {
        this.adminToken = adminToken;
    }

    public void validate(final String token) {
        if (token == null || token.isBlank() || !adminToken.equals(token)) {
            throw new OsondosonException(FailMessage.FORBIDDEN_ADMIN_TOKEN);
        }
    }
}
