package fu.sep.apjf.utils;

import fu.sep.apjf.entity.User;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("AuthTokenFilter called for URI: {}", request.getRequestURL());
        try {
            String jwt = extractJwtFromRequest(request);
            if (jwt != null) {
                // Validate token và lấy claims
                Claims claims = jwtUtils.validateJwtToken(jwt);
                log.info("JWT token is valid");

                // Lấy thông tin từ Claims
                String email = claims.getSubject(); // Email từ subject
                String username = claims.get("username", String.class); // Username từ claim
                Long userId = claims.get("userId", Long.class);
                @SuppressWarnings("unchecked")
                List<String> roles = claims.get("roles", List.class);

                log.info("JWT payload - Subject/Email: {}, Username: {}, UserId: {}",
                        email, username, userId);
                log.info("JWT roles: {}", roles);

                // Chuyển đổi thành GrantedAuthority
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                // Tạo User object với thông tin từ token
                User user = new User();
                user.setId(userId);
                user.setUsername(username);
                user.setEmail(email);

                // Tạo Authentication với User object
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(user, null, authorities);

                log.info("Created Authentication with User - Email: {}, Username: {}, UserId: {}",
                        user.getEmail(), user.getUsername(), user.getId());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                log.warn("JWT token is present but invalid");
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage(), e);
        }
        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String jwt = jwtUtils.getTokenFromHeader(request);
        if (jwt == null) {
            log.debug("No JWT found in request header");
        }
        return jwt;
    }
}
