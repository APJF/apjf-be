//package fu.sep.apjf.controller;
//
//import fu.sep.apjf.dto.request.ProfileRequestDto;
//import fu.sep.apjf.dto.response.ApiResponseDto;
//import fu.sep.apjf.dto.response.ExamResultSummaryDto;
//import fu.sep.apjf.dto.response.ProfileResponseDto;
//import fu.sep.apjf.entity.EnumClass;
//import fu.sep.apjf.entity.User;
//import fu.sep.apjf.mapper.UserMapper;
//import fu.sep.apjf.service.ExamResultService;
//import fu.sep.apjf.service.MinioService;
//import fu.sep.apjf.service.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//@RestController
//@RequestMapping("/api/users")
//@RequiredArgsConstructor
//public class UserController {
//
//    private final ExamResultService examResultService;
//    private final MinioService minioService;
//    private final UserService userService;
//
//    @GetMapping("/{userId}/exam-history")
//    public ResponseEntity<ApiResponseDto<Page<ExamResultSummaryDto>>> getUserExamHistory(
//            @PathVariable Long userId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(required = false) EnumClass.ExamStatus status
//    ) {
//        Page<ExamResultSummaryDto> history;
//
//        if (status != null) {
//            history = examResultService.getExamHistoryByStatus(userId, status, page, size);
//        } else {
//            history = examResultService.getExamHistory(userId, page, size);
//        }
//
//        return ResponseEntity.ok(ApiResponseDto.ok("Lịch sử làm bài kiểm tra", history));
//    }
//
//    @PostMapping("/avatar")
//    public ResponseEntity<ApiResponseDto<String>> uploadAvatar(@RequestParam("file") MultipartFile file,
//                                                               @AuthenticationPrincipal User user) throws Exception {
//        String objectName = minioService.uploadAvatar(file, user.getEmail());
//        return ResponseEntity.ok(ApiResponseDto.ok("Upload avatar thành công", objectName));
//    }
//
//    @PostMapping("/profile")
//    public ResponseEntity<ApiResponseDto<Object>> updateProfile(@RequestBody ProfileRequestDto dto,
//                                                                @AuthenticationPrincipal User user) {
//        String message = userService.updateProfile(user.getEmail(), dto);
//        return ResponseEntity.ok(ApiResponseDto.ok(message, null));
//    }
//
//    @GetMapping("/profile")
//    public ResponseEntity<ApiResponseDto<ProfileResponseDto>> getCurrentUser(@AuthenticationPrincipal User user) {
//        ProfileResponseDto userProfileDto = UserMapper.toProfileDto(user);
//        return ResponseEntity.ok(ApiResponseDto.ok("Thông tin người dùng", userProfileDto));
//    }
//
//}
//
