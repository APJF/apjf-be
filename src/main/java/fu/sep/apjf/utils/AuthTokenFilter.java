package fu.sep.apjf.utils;

import fu.sep.apjf.entity.User;
import fu.sep.apjf.entity.Authority;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    private static final Logger authTokenlogger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        authTokenlogger.debug("AuthTokenFilter called for URI: {}", request.getRequestURL());
        try {
            String jwt = extractJwtFromRequest(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                // Hiển thị thông tin về JWT token
                authTokenlogger.info("JWT token is valid");

                // Lấy thông tin từ JWT
                String email = jwtUtils.getEmailFromJwtToken(jwt); // Email từ subject
                String username = jwtUtils.getUsernameFromJwtToken(jwt); // Username từ claim
                Long userId = jwtUtils.getUserIdFromJwtToken(jwt);
                List<String> roles = jwtUtils.getRolesFromJwtToken(jwt);

                authTokenlogger.info("JWT payload - Subject/Email: {}, Username: {}, UserId: {}",
                    email, username, userId);
                authTokenlogger.info("JWT roles: {}", roles);

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

                authTokenlogger.info("Created Authentication with User - Email: {}, Username: {}, UserId: {}",
                    user.getEmail(), user.getUsername(), user.getId());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else if (jwt != null) {
                authTokenlogger.warn("JWT token is present but invalid");
            }
        } catch (Exception e) {
            authTokenlogger.error("Cannot set user authentication: {}", e.getMessage(), e);
        }
        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String jwt = jwtUtils.getJwtFromHeader(request);
        if (jwt == null) {
            authTokenlogger.debug("No JWT found in request header");
        }
        return jwt;
    }
}
