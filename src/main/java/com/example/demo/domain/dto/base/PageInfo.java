package com.example.demo.domain.dto.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageInfo {
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private boolean hasNext;
}