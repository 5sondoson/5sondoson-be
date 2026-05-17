package com.osondoson.backend.admin.service.batch;

public record BatchResult(
        int processed,
        int failed
) {}
