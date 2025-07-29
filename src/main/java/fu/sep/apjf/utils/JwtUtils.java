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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

/**
 * Tiện ích xử lý JWT token
 * Hỗ trợ nghiệp vụ xác thực người dùng và phân quyền trong hệ thống
 */
@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${spring.app.jwtRefreshExpirationMs:604800000}")
    private int jwtRefreshExpirationMs;

    /**
     * Trích xuất JWT token từ header của request
     */
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        return extractTokenFromBearerString(bearerToken);
    }

    /**
     * Trích xuất token từ chuỗi Authorization Bearer
     */
    public String extractTokenFromBearerString(String bearerToken) {
        logger.debug("Authorization header: {}", bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Tạo access token từ thông tin người dùng
     */
    public String generateTokenFromUsername(UserDetails userDetails) {
        String username = userDetails.getUsername();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // Lấy thêm thông tin từ User object
        Long userId = null;
        String email = null;

        if (userDetails instanceof User user) {
            userId = user.getId();
            email = user.getEmail();
        }

        return Jwts.builder()
                .subject(email)  // Sử dụng email làm subject
                .claim("roles", roles)     // nhúng roles vào claim
                .claim("userId", userId)   // nhúng userId vào claim
                .claim("username", username)   // nhúng username vào claim
                .claim("tokenType", "access") // đánh dấu loại token
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    /**
     * Tạo refresh token từ thông tin người dùng
     */
    public String generateRefreshToken(UserDetails userDetails) {
        String username = userDetails.getUsername();

        Long userId = null;
        String email = null;

        if (userDetails instanceof User user) {
            userId = user.getId();
            email = user.getEmail();
        }

        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("username", username)
                .claim("tokenType", "refresh") // đánh dấu loại token
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtRefreshExpirationMs))
                .signWith(key())
                .compact();
    }

    /**
     * Lấy username từ JWT token
     */
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser().verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("username", String.class);
    }

    /**
     * Lấy email từ JWT token (stored in subject)
     */
    public String getEmailFromJwtToken(String token) {
        return Jwts.parser().verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Lấy userId từ JWT token
     */
    public Long getUserIdFromJwtToken(String token) {
        return Jwts.parser().verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("userId", Long.class);
    }

    /**
     * Lấy danh sách roles từ JWT token
     */
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromJwtToken(String token) {
        return Jwts.parser().verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("roles", List.class);
    }

    /**
     * Kiểm tra token có phải là token hợp lệ
     */
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

    /**
     * Kiểm tra refresh token có hợp lệ không và có đúng loại không
     */
    public boolean validateRefreshToken(String token) {
        try {
            var claims = Jwts.parser().verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();

            // Kiểm tra tokenType claim phải là "refresh"
            String tokenType = claims.get("tokenType", String.class);
            return tokenType != null && tokenType.equals("refresh");
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

    /**
     * Lấy secret key để ký JWT
     */
    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * Trích xuất userId từ Authorization header
     * Phương thức tiện ích cho các controller
     */
    public Long getUserIdFromAuthHeader(String authHeader) {
        String token = extractTokenFromBearerString(authHeader);
        if (token != null) {
            return getUserIdFromJwtToken(token);
        }
        return null;
    }

    /**
     * Trích xuất username từ Authorization header
     * Phương thức tiện ích cho các controller phục vụ nghiệp vụ ApprovalRequest
     */
    public String getUsernameFromAuthHeader(String authHeader) {
        String token = extractTokenFromBearerString(authHeader);
        if (token != null) {
            return getUsernameFromJwtToken(token);
        }
        return null;
    }

    /**
     * Kiểm tra xem token có chứa một vai trò cụ thể không
     * Phương thức này chỉ kiểm tra thông tin, không tham gia vào quyết định phân quyền
     *
     * @param token JWT token
     * @param role Vai trò cần kiểm tra
     * @return true nếu token chứa vai trò đó, ngược lại false
     */
    public boolean hasRole(String token, String role) {
        if (token == null || !validateJwtToken(token)) {
            return false;
        }

        List<String> roles = getRolesFromJwtToken(token);
        return roles != null && roles.contains(role);
    }

    /**
     * Lấy ID của người dùng hiện tại từ SecurityContext
     * Phương thức tiện ích cho toàn bộ dự án để lấy ID của người dùng đã đăng nhập
     *
     * @return ID người dùng hiện tại dưới dạng chuỗi
     * @throws IllegalStateException nếu không thể xác định người dùng
     */
    public String getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return user.getId().toString();
        }
        throw new IllegalStateException("Không thể xác định người dùng");
    }
}
