package tz.go.tpa.ict_assets_management.dto.response;

import java.util.List;

public class PageResponse<T> {
    private List<T> data;
    private int page;
    private int pageSize;
    private long totalItems;
    private int totalPages;

    public PageResponse(List<T> data, int page, int pageSize, long totalItems) {
        this.data = data;
        this.page = page;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
        this.totalPages = pageSize == 0 ? 0 : (int) Math.ceil((double) totalItems / pageSize);
    }

    public List<T> getData() {
        return data;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
