package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.UserRequestDto;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.UserResponseDto;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.service.MinioService;
import fu.sep.apjf.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final MinioService minioService;
    private final UserService userService;

    @PostMapping("/avatar")
    public ResponseEntity<ApiResponseDto<String>> uploadAvatar(@RequestParam("file") MultipartFile file,
                                                               @AuthenticationPrincipal User user) throws Exception {
        String objectName = minioService.uploadAvatar(file, user.getEmail());
        return ResponseEntity.ok(ApiResponseDto.ok("Upload avatar thành công", objectName));
    }

    @PostMapping("/profile")
    public ResponseEntity<ApiResponseDto<Object>> updateProfile(@RequestBody UserRequestDto dto,
                                                                @AuthenticationPrincipal User user) {
        String message = userService.updateProfile(user.getEmail(), dto);
        return ResponseEntity.ok(ApiResponseDto.ok(message));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> getProfile(@AuthenticationPrincipal User user) {
        UserResponseDto profile = userService.getProfile(user.getEmail());
        return ResponseEntity.ok(ApiResponseDto.ok("Thông tin người dùng", profile));
    }
}
