package fu.sep.apjf.controller;

import fu.sep.apjf.dto.ApiResponse;
import fu.sep.apjf.dto.LoginDTO;
import fu.sep.apjf.dto.LoginResponse;
import fu.sep.apjf.dto.RegisterDTO;
import fu.sep.apjf.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
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
        return new ResponseEntity<>(ApiResponse.ok("Đăng ký thành công. Vui lòng kiểm tra email để xác thực tài khoản.", null), HttpStatus.CREATED);
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Object>> verifyAccount(@RequestParam String email, @RequestParam String otp) {
        userService.verifyAccount(email, otp);
        return new ResponseEntity<>(ApiResponse.ok("Xác thực tài khoản thành công.", null), HttpStatus.OK);
    }

    @PostMapping("/otp")
    public ResponseEntity<ApiResponse<Object>> regenerateOtp(@RequestParam String email) {
        userService.regenerateOtp(email);
        return new ResponseEntity<>(ApiResponse.ok("OTP đã được gửi lại thành công.", null), HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Object>> forgotPassword(@RequestParam String email) {
        userService.forgotPassword(email);
        return new ResponseEntity<>(ApiResponse.ok("Đã gửi email xác thực đặt lại mật khẩu.", null), HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Object>> resetPassword(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam String newPassword) {
        userService.resetPassword(email, otp, newPassword);
        return new ResponseEntity<>(ApiResponse.ok("Đặt lại mật khẩu thành công.", null), HttpStatus.OK);
    }

}