package tz.go.tpa.ict_assets_management.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final long expirationMs;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration-ms:3600000}") long expirationMs) {
        this.algorithm = Algorithm.HMAC256(secret.getBytes());
        this.verifier = JWT.require(algorithm).build();
        this.expirationMs = expirationMs;
    }

    public String generateToken(String username, List<String> roles, Long stationId) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        List<String> authorityValues = roles.stream()
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .toList();
        return JWT.create()
                .withSubject(username)
                .withClaim("roles", authorityValues)
                .withClaim("stationId", stationId)
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .sign(algorithm);
    }

    public boolean validateToken(String token) {
        try {
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException ex) {
            return false;
        }
    }

    public String getUsername(String token) {
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getSubject();
    }

    public List<String> getRoles(String token) {
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("roles").asList(String.class);
    }

    public Long getStationId(String token) {
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("stationId").asLong();
    }
}