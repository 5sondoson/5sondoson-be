package com.osondoson.backend.admin.controller;

import com.osondoson.backend.admin.auth.AdminTokenValidator;
import com.osondoson.backend.admin.dto.request.AdminTokenVerifyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/api/auth")
@RequiredArgsConstructor
public class AdminAuthController implements AdminAuthControllerSwagger {

    private final AdminTokenValidator adminTokenValidator;

    @PostMapping("/verify")
    public ResponseEntity<Void> verifyToken(@RequestBody AdminTokenVerifyRequest adminTokenVerifyRequest) {
        adminTokenValidator.validate(adminTokenVerifyRequest.adminToken());
        return ResponseEntity.ok().build();
    }
}
