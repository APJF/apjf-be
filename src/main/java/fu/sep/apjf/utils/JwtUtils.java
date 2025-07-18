package fu.sep.apjf.utils;

import fu.sep.apjf.entity.User;
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
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        logger.debug("Authorization header: {}", bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String generateTokenFromUsername(UserDetails userDetails) {
        String username = userDetails.getUsername();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Lấy thêm thông tin từ User object
        Long userId = null;
        String email = null;

        if (userDetails instanceof User) {
            User user = (User) userDetails;
            userId = user.getId();
            email = user.getEmail();
        }

        return Jwts.builder()
                .subject(email)  // Sử dụng email làm subject thay vì username
                .claim("roles", roles)     // embed roles claim
                .claim("userId", userId)   // embed userId claim
                .claim("username", username)   // Chuyển username thành một claim
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser().verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("username", String.class);  // Lấy username từ claim
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromJwtToken(String token) {
        return Jwts.parser().verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("roles", List.class);
    }

    // Thêm phương thức để lấy userId từ JWT token
    public Long getUserIdFromJwtToken(String token) {
        return Jwts.parser().verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("userId", Long.class);
    }

    // Thêm phương thức để lấy email từ JWT token
    public String getEmailFromJwtToken(String token) {
        return Jwts.parser().verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();  // Email bây giờ là subject
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().verifyWith(key()).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT signature: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT signature: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Empty JWT claims string: {}", e.getMessage());
        }
        return false;
    }
}
