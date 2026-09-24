package tz.go.tpa.ict_assets_management.dto.response;

import java.util.List;

public class JwtAuthenticationResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private List<String> roles;
    private Long stationId;
    private String stationName;

    public JwtAuthenticationResponse() {
    }

    public JwtAuthenticationResponse(String token, String username, List<String> roles, Long stationId, String stationName) {
        this.token = token;
        this.username = username;
        this.roles = roles;
        this.stationId = stationId;
        this.stationName = stationName;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    public Long getStationId() { return stationId; }
    public void setStationId(Long stationId) { this.stationId = stationId; }

    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }
}
