//package fu.sep.apjf.controller;
//
//import fu.sep.apjf.dto.request.UpdateUserAuthoritiesDto;
//import fu.sep.apjf.dto.request.UpdateUserStatusDto;
//import fu.sep.apjf.dto.response.ApiResponseDto;
//import fu.sep.apjf.dto.response.UserResponseDto;
//import fu.sep.apjf.entity.Authority;
//import fu.sep.apjf.entity.User;
//import fu.sep.apjf.service.AdminService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/admin")
//@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')")
//public class AdminController {
//
//    private final AdminService adminService;
//
//    /**
//     * Lấy danh sách tất cả users (trừ admin hiện tại)
//     */
//    @GetMapping("/users")
//    public ResponseEntity<ApiResponseDto<List<UserResponseDto>>> getAllUsers(@AuthenticationPrincipal User admin) {
//        List<UserResponseDto> users = adminService.getAllUsers(admin.getEmail());
//        return ResponseEntity.ok(ApiResponseDto.ok("Danh sách tất cả users", users));
//    }
//
//    /**
//     * Lấy thông tin chi tiết user theo ID
//     */
//    @GetMapping("/users/{userId}")
//    public ResponseEntity<ApiResponseDto<UserResponseDto>> getUserById(@PathVariable Long userId) {
//        UserResponseDto user = adminService.getUserById(userId);
//        return ResponseEntity.ok(ApiResponseDto.ok("Thông tin chi tiết user", user));
//    }
//
//    /**
//     * Lấy danh sách tất cả quyền (trừ ROLE_ADMIN)
//     */
//    @GetMapping("/authorities")
//    public ResponseEntity<ApiResponseDto<List<Authority>>> getAllAuthorities() {
//        List<Authority> authorities = adminService.getAllNonAdminAuthorities();
//        return ResponseEntity.ok(ApiResponseDto.ok("Danh sách tất cả quyền", authorities));
//    }
//
//    /**
//     * Cập nhật quyền của user
//     */
//    @PutMapping("/users/authorities")
//    public ResponseEntity<ApiResponseDto<String>> updateUserAuthorities(
//            @Valid @RequestBody UpdateUserAuthoritiesDto dto,
//            @AuthenticationPrincipal User admin) {
//        String message = adminService.updateUserAuthorities(dto, admin.getEmail());
//        return ResponseEntity.ok(ApiResponseDto.ok(message));
//    }
//
//    /**
//     * Ban user (set enabled = false)
//     */
//    @PutMapping("/users/ban")
//    public ResponseEntity<ApiResponseDto<String>> banUser(
//            @Valid @RequestBody UpdateUserStatusDto dto,
//            @AuthenticationPrincipal User admin) {
//        String message = adminService.updateUserStatus(dto, admin.getEmail());
//        return ResponseEntity.ok(ApiResponseDto.ok(message));
//    }
//
//    /**
//     * Unban user (set enabled = true)
//     */
//    @PutMapping("/users/unban")
//    public ResponseEntity<ApiResponseDto<String>> unbanUser(
//            @Valid @RequestBody UpdateUserStatusDto dto,
//            @AuthenticationPrincipal User admin) {// Force unban action
//        String message = adminService.updateUserStatus(dto, admin.getEmail());
//        return ResponseEntity.ok(ApiResponseDto.ok(message));
//    }
//
//    /**
//     * Cập nhật trạng thái user (ban/unban)
//     */
//    @PutMapping("/users/status")
//    public ResponseEntity<ApiResponseDto<String>> updateUserStatus(
//            @Valid @RequestBody UpdateUserStatusDto dto,
//            @AuthenticationPrincipal User admin) {
//        String message = adminService.updateUserStatus(dto, admin.getEmail());
//        return ResponseEntity.ok(ApiResponseDto.ok(message));
//    }
//}
