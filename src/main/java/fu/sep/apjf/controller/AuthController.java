package fu.sep.apjf.controller;

import fu.sep.apjf.dto.*;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginResponse payload = userService.login(loginDTO);
        return ResponseEntity.ok(ApiResponse.ok("Đăng nhập thành công", payload));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> register(@Valid @RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return new ResponseEntity<>(
                ApiResponse.ok("Đăng ký thành công. Vui lòng kiểm tra email để xác thực tài khoản.", null),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Object>> verifyAccount(
            @RequestParam String email,
            @RequestParam String otp) {
        userService.verifyAccount(email, otp);
        return ResponseEntity.ok(ApiResponse.ok("Xác thực tài khoản thành công.", null));
    }

    @PostMapping("/otp")
    public ResponseEntity<ApiResponse<Object>> regenerateOtp(@RequestParam String email) {
        userService.regenerateOtp(email);
        return ResponseEntity.ok(ApiResponse.ok("OTP đã được gửi lại thành công.", null));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Object>> forgotPassword(@RequestParam String email) {
        userService.forgotPassword(email);
        return ResponseEntity.ok(ApiResponse.ok("Đã gửi email xác thực đặt lại mật khẩu.", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Object>> resetPassword(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam String newPassword) {
        userService.resetPassword(email, otp, newPassword);
        return ResponseEntity.ok(ApiResponse.ok("Đặt lại mật khẩu thành công.", null));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Object>> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        userService.changePassword(
                changePasswordDTO.email(),
                changePasswordDTO.oldPassword(),
                changePasswordDTO.newPassword()
        );
        return ResponseEntity.ok(ApiResponse.ok("Thay đổi mật khẩu thành công.", null));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileDto>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Chưa xác thực", null));
        }

        String email = authentication.getName();
        Optional<User> userOptional = userService.findByEmail(email);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Không tìm thấy người dùng", null));
        }

        User user = userOptional.get();
        UserProfileDto profile = UserProfileDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .avatar(user.getAvatar())
                .enabled(user.isEnabled())
                .authorities(user.getAuthorities().stream()
                        .map(authority -> authority.getAuthority())
                        .toList())
                .build();

        return ResponseEntity.ok(ApiResponse.ok("Lấy thông tin người dùng thành công", profile));
    }

    @GetMapping("/oauth2/success")
    public ResponseEntity<ApiResponse<Map<String, String>>> oauth2Success(
            @RequestParam String token,
            @RequestParam String email,
            @RequestParam String username) {

        Map<String, String> response = Map.of(
                "token", token,
                "email", email,
                "username", username,
                "loginType", "oauth2",
                "provider", "google"
        );

        return ResponseEntity.ok(ApiResponse.ok("Đăng nhập Google thành công", response));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkAuthStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAuthenticated = authentication != null &&
                                 authentication.isAuthenticated() &&
                                 !"anonymousUser".equals(authentication.getName());

        Map<String, Object> status = Map.of(
                "authenticated", isAuthenticated,
                "principal", authentication != null ? authentication.getName() : "anonymous",
                "authorities", authentication != null ? authentication.getAuthorities() : "none"
        );

        return ResponseEntity.ok(ApiResponse.ok("Authentication status retrieved", status));
    }
}