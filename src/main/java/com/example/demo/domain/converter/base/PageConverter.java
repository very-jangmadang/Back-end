package com.example.demo.domain.converter.base;

import com.example.demo.domain.dto.base.PageInfo;
import org.springframework.data.domain.Page;

public class PageConverter {
    public static PageInfo toPageInfo(Page<?> page) {
        return PageInfo.builder()
                .currentPage(page.getNumber() + 1)
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .hasNext(page.hasNext())
                .build();
    }
}

