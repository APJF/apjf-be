package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.ProfileRequestDto;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.ProfileResponseDto;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.mapper.UserMapper;
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
    private final UserMapper userMapper;

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
        return ResponseEntity.ok(ApiResponseDto.ok(message));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponseDto<ProfileResponseDto>> getCurrentUser(@AuthenticationPrincipal User user) {
        // Lấy thông tin mới nhất từ database thay vì từ JWT
        User freshUser = userService.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        return ResponseEntity.ok(ApiResponseDto.ok("Thông tin người dùng", userMapper.toProfileDto(freshUser)));
    }
}
