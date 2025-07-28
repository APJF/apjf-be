package fu.sep.apjf.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import fu.sep.apjf.dto.response.ApiResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.util.Set;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final Set<String> ADMIN_PATHS = Set.of("/api/admin/", "/api/users/");
    private static final Set<String> MANAGER_PATHS = Set.of("/api/manager/");
    private static final Set<String> STAFF_PATHS = Set.of("/api/staff/");
    private static final Set<String> USER_PATHS = Set.of("/api/exams/", "/api/exam-results/");

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                      AccessDeniedException accessDeniedException) throws IOException, ServletException {

        String requiredRole = determineRequiredRole(request.getRequestURI());
        String message = "Bạn cần quyền " + requiredRole + " để thực hiện thao tác này.";

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            message = "Bạn cần đăng nhập để thực hiện thao tác này.";
        }

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        ApiResponseDto<Object> responseBody = new ApiResponseDto<>(
                false,
                message,
                null,
                System.currentTimeMillis()
        );

        new ObjectMapper().writeValue(response.getOutputStream(), responseBody);
    }

    private String determineRequiredRole(String requestUri) {
        if (requestUri == null) return "ROLE_USER";

        if (USER_PATHS.stream().anyMatch(requestUri::contains)) {
            return "ROLE_USER";
        } else if (STAFF_PATHS.stream().anyMatch(requestUri::contains)) {
            return "ROLE_STAFF";
        } else if (MANAGER_PATHS.stream().anyMatch(requestUri::contains)) {
            return "ROLE_MANAGER";
        } else if (ADMIN_PATHS.stream().anyMatch(requestUri::contains)) {
            return "ROLE_ADMIN";
        }

        // Mặc định yêu cầu ROLE_STAFF cho các endpoint khác
        return "ROLE_STAFF";
    }
}
