package com.osondoson.backend.admin.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AdminTokenInterceptor implements HandlerInterceptor {

    public static final String ADMIN_TOKEN_HEADER = "X-ADMIN-TOKEN";

    private final AdminTokenValidator adminTokenValidator;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }
        adminTokenValidator.validate(request.getHeader(ADMIN_TOKEN_HEADER));
        return true;
    }
}
