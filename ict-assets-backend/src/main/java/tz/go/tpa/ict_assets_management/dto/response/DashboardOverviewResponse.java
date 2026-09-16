package tz.go.tpa.ict_assets_management.dto.response;

public class DashboardOverviewResponse {
    private long totalUsers;
    private long activeUsers;
    private long inactiveUsers;
    private long totalAssets;
    private String systemStatus;

    public DashboardOverviewResponse() {
    }

    public DashboardOverviewResponse(long totalUsers, long activeUsers, long inactiveUsers,
                                     long totalAssets, String systemStatus) {
        this.totalUsers = totalUsers;
        this.activeUsers = activeUsers;
        this.inactiveUsers = inactiveUsers;
        this.totalAssets = totalAssets;
        this.systemStatus = systemStatus;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public long getInactiveUsers() {
        return inactiveUsers;
    }

    public void setInactiveUsers(long inactiveUsers) {
        this.inactiveUsers = inactiveUsers;
    }

    public long getTotalAssets() {
        return totalAssets;
    }

    public void setTotalAssets(long totalAssets) {
        this.totalAssets = totalAssets;
    }

    public String getSystemStatus() {
        return systemStatus;
    }

    public void setSystemStatus(String systemStatus) {
        this.systemStatus = systemStatus;
    }
}
