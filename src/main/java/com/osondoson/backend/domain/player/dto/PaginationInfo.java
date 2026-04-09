package com.osondoson.backend.domain.player.dto;

import org.springframework.data.domain.Page;

public record PaginationInfo(
        int page,
        int size,
        int totalPages,
        long totalElements
) {
    public static PaginationInfo of(int requestPage, int requestSize, Page<?> page) {
        return new PaginationInfo(
                requestPage,
                requestSize,
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}
