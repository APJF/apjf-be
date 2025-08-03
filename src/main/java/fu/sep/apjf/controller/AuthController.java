package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.*;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.LoginResponseDto;
import fu.sep.apjf.entity.Token.TokenType;
import fu.sep.apjf.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponseDto<Object>> sendOtp(
            @RequestParam String email,
            @RequestParam String type) {
        String message = userService.sendOtp(email, TokenType.valueOf(type.toUpperCase()));
        return ResponseEntity.ok(ApiResponseDto.ok(message, null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponseDto<Object>> resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto) {
        userService.resetPassword(
                resetPasswordDto.email(),
                resetPasswordDto.otp(),
                resetPasswordDto.newPassword()
        );
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
        LoginResponseDto payload = userService.refreshToken(request.refreshToken());
        return ResponseEntity.ok(ApiResponseDto.ok("Làm mới token thành công", payload));
    }
}
