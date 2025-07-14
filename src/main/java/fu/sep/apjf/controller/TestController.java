package fu.sep.apjf.controller;

import fu.sep.apjf.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Test controller để kiểm tra OAuth2 flow
 */
@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    /**x`
     * Endpoint để test OAuth2 success callback
     */
    @GetMapping("/oauth2-success")
    public ResponseEntity<ApiResponse<Map<String, String>>> handleOAuth2Success(
            @RequestParam(required = false) String token,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String username) {

        log.info("OAuth2 success callback received - token: {}, email: {}, username: {}",
                token != null ? "***" : null, email, username);

        Map<String, String> response = Map.of(
                "message", "OAuth2 login successful",
                "token", token != null ? token : "No token",
                "email", email != null ? email : "No email",
                "username", username != null ? username : "No username"
        );

        return ResponseEntity.ok(ApiResponse.ok("OAuth2 đăng nhập thành công", response));
    }

    /**
     * Endpoint để test authentication status
     */
    @GetMapping("/auth-status")
    public ResponseEntity<ApiResponse<String>> checkAuthStatus() {
        return ResponseEntity.ok(ApiResponse.ok("Authentication check endpoint", "Server is running"));
    }
}
