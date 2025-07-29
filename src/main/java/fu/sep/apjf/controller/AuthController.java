package fu.sep.apjf.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import fu.sep.apjf.dto.request.ChangePasswordDto;
import fu.sep.apjf.dto.request.LoginRequestDto;
import fu.sep.apjf.dto.request.ProfileRequestDto;
import fu.sep.apjf.dto.request.RefreshTokenRequest;
import fu.sep.apjf.dto.request.RegisterDto;
import fu.sep.apjf.dto.request.ResetPasswordDto;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.LoginResponseDto;
import fu.sep.apjf.dto.response.ProfileResponseDto;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.mapper.UserMapper;
import fu.sep.apjf.service.MinioService;
import fu.sep.apjf.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final MinioService minioService;

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

    @PostMapping("/send-verification-otp")
    public ResponseEntity<ApiResponseDto<Object>> sendVerificationOtp(@RequestParam String email) {
        userService.sendVerificationOtp(email);
        return ResponseEntity.ok(ApiResponseDto.ok("Đã gửi mã OTP xác thực tài khoản vào email của bạn.", null));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponseDto<Object>> forgotPassword(@RequestParam String email) {
        userService.forgotPassword(email);
        return ResponseEntity.ok(ApiResponseDto.ok("Đã gửi email xác thực đặt lại mật khẩu.", null));
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

    @PostMapping("/avatar")
    public ResponseEntity<ApiResponseDto<String>> uploadAvatar(@RequestParam("file") MultipartFile file,
                                                              @AuthenticationPrincipal User user) throws Exception {
        String objectName = minioService.uploadAvatar(file, user.getEmail());
        return ResponseEntity.ok(ApiResponseDto.ok("Upload avatar thành công", objectName));
    }

    @PostMapping("/profile")
    public ResponseEntity<ApiResponseDto<Object>> updateProfile(@RequestBody ProfileRequestDto dto,
                                                               @AuthenticationPrincipal User user) {
        String message = userService.updateProfile(user.getEmail(), dto);
        return ResponseEntity.ok(ApiResponseDto.ok(message, null));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponseDto<ProfileResponseDto>> getCurrentUser(@AuthenticationPrincipal User user) {
        ProfileResponseDto userProfileDto = UserMapper.toProfileDto(user);
        return ResponseEntity.ok(ApiResponseDto.ok("Thông tin người dùng", userProfileDto));
    }


}
