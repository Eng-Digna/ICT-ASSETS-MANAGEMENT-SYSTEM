package tz.go.tpa.ict_assets_management.dto.request;

public class AssignmentSearchFilter {
    private Long assetId;
    private Long userId;
    private String status;
    private int page = 0;
    private int pageSize = 20;
    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = Math.min(pageSize, 100); }
}