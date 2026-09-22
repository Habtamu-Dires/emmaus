package com.hab.emmaus.shared.common_utils;


import lombok.Builder;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Builder
public record PageResponse<T>(
        List<T> content,
        int number,
        int size,
        long totalElements,
        int numberOfElements,
        int totalPages,
        boolean last,
        boolean first,
        boolean empty
) {

    public static <T> PageResponse<T> toPage(
            List<T> content,
            long total,
            Pageable pageable
    ) {
        int size = pageable.getPageSize();
        int page = pageable.getPageNumber();

        return PageResponse.<T>builder()
                .content(content)
                .totalElements(total)
                .totalPages((int) Math.ceil((double) total / size))
                .size(size)
                .number(page)
                .numberOfElements(content.size())
                .first(page == 0)
                .last((long) (page + 1) * size >= total)
                .empty(content.isEmpty())
                .build();
    }
}
