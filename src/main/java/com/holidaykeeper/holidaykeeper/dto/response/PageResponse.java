package com.holidaykeeper.holidaykeeper.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.function.Function;

@Getter
@Builder
public class PageResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    /**
     * PageResponse의 content 타입을 변환하면서 페이징 정보는 유지
     */
    public <R> PageResponse<R> map(Function<T, R> converter) {
        List<R> convertedContent = this.content.stream()
                .map(converter)
                .toList();

        return PageResponse.<R>builder()
                .content(convertedContent)
                .page(this.page)
                .size(this.size)
                .totalElements(this.totalElements)
                .totalPages(this.totalPages)
                .build();
    }
}
