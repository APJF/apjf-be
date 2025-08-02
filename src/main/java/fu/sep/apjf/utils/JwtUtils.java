package fu.sep.apjf.utils;

import fu.sep.apjf.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${app.jwt.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwt.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${app.jwt.jwtRefreshExpirationMs:604800000}")
    private int jwtRefreshExpirationMs;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Claims validateJwtToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
            throw new IllegalArgumentException("Token không hợp lệ");
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token: {}", e.getMessage());
            throw new IllegalArgumentException("Token đã hết hạn");
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token: {}", e.getMessage());
            throw new IllegalArgumentException("Token không được hỗ trợ");
        } catch (IllegalArgumentException e) {
            logger.error("JWT validation error: {}", e.getMessage());
            throw e;
        }
    }

    public String generateJwtToken(UserDetails userDetails, boolean isRefreshToken) {
        Long userId = null;
        String email = null;
        String username = null;

        if (userDetails instanceof User user) {
            userId = user.getId();
            email = user.getEmail();
            username = user.getUsername();
        }

        var builder = Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("username", username)
                .issuedAt(new Date());

        if (isRefreshToken) {
            builder.claim("tokenType", "refresh")
                   .expiration(new Date(System.currentTimeMillis() + jwtRefreshExpirationMs));
        } else {
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
            builder.claim("roles", roles)
                   .claim("tokenType", "access")
                   .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs));
        }

        return builder.signWith(key()).compact();
    }
}
