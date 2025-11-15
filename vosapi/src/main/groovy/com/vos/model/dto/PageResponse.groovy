package com.vos.model.dto

class PageResponse<T> {
    List<T> content
    int page
    int size
    long totalElements
    int totalPages
    boolean last
    
    PageResponse() {}
    
    PageResponse(List<T> content, int page, int size, long totalElements) {
        this.content = content
        this.page = page
        this.size = size
        this.totalElements = totalElements
        this.totalPages = (int) Math.ceil((double) totalElements / size)
        this.last = page >= totalPages - 1
    }
}

