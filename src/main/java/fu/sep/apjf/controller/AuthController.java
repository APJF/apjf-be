package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.ChangePasswordDto;
import fu.sep.apjf.dto.request.LoginRequestDto;
import fu.sep.apjf.dto.request.RegisterDto;
import fu.sep.apjf.dto.request.RefreshTokenRequest;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.LoginResponseDto;
import fu.sep.apjf.dto.response.ProfileResponseDto;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.mapper.UserMapper;
import fu.sep.apjf.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        LoginResponseDto payload = userService.login(loginRequest);
        return ResponseEntity.ok(ApiResponseDto.ok("Đăng nhập thành công", payload));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto<Object>> register(@Valid @RequestBody RegisterDto registerRequest) {
        userService.register(registerRequest);
        return new ResponseEntity<>(
                ApiResponseDto.ok("Đăng ký thành công. Vui lòng kiểm tra email để xác thực tài khoản.", null),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponseDto<Object>> verifyAccount(
            @RequestParam String email,
            @RequestParam String otp) {
        userService.verifyAccount(email, otp);
        return ResponseEntity.ok(ApiResponseDto.ok("Xác thực tài khoản thành công.", null));
    }

    @PostMapping("/otp")
    public ResponseEntity<ApiResponseDto<Object>> regenerateOtp(@RequestParam String email) {
        userService.regenerateOtp(email);
        return ResponseEntity.ok(ApiResponseDto.ok("OTP đã được gửi lại thành công.", null));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponseDto<Object>> forgotPassword(@RequestParam String email) {
        userService.forgotPassword(email);
        return ResponseEntity.ok(ApiResponseDto.ok("Đã gửi email xác thực đặt lại mật khẩu.", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponseDto<Object>> resetPassword(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam String newPassword) {
        userService.resetPassword(email, otp, newPassword);
        return ResponseEntity.ok(ApiResponseDto.ok("Đặt lại mật khẩu thành công.", null));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponseDto<Object>> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto) {
        userService.changePassword(
                changePasswordDto.email(),
                changePasswordDto.oldPassword(),
                changePasswordDto.newPassword()
        );
        return ResponseEntity.ok(ApiResponseDto.ok("Thay đổi mật khẩu thành công.", null));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> refreshToken(@RequestBody RefreshTokenRequest request) {
        LoginResponseDto payload = userService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponseDto.ok("Làm mới token thành công", payload));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponseDto<ProfileResponseDto>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            log.error("Authentication is null, not authenticated, or anonymous user");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponseDto.error("Chưa xác thực", null));
        }

        String email = authentication.getName(); // Email từ JWT token (vì subject là email)
        log.info("Attempting to find user with email: {}", email);

        // Kiểm tra authentication principal
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            User userFromToken = (User) principal;
            log.info("User from token - ID: {}, Username: {}, Email: {}",
                    userFromToken.getId(), userFromToken.getUsername(), userFromToken.getEmail());

            // Sử dụng email từ User object trong token
            email = userFromToken.getEmail();
            log.info("Using email from user object: {}", email);
        } else {
            log.warn("Principal is not a User object, it is: {}", principal.getClass().getName());
        }

        Optional<User> userOptional = userService.findByEmail(email);

        if (userOptional.isEmpty()) {
            log.error("No user found with email: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDto.error("Không tìm thấy người dùng", null));
        }

        User user = userOptional.get();
        log.info("User found - ID: {}, Username: {}, Email: {}", user.getId(), user.getUsername(), user.getEmail());
        ProfileResponseDto userProfileDto = UserMapper.toProfileDto(user);

        return ResponseEntity.ok(ApiResponseDto.ok("Thông tin người dùng", userProfileDto));
    }
}
