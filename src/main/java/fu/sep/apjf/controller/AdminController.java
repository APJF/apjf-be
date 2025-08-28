package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.UpdateUserAuthoritiesDto;
import fu.sep.apjf.dto.request.UpdateUserStatusDto;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.DashboardManagerResponseDto;
import fu.sep.apjf.dto.response.UserResponseDto;
import fu.sep.apjf.dto.response.UserStatsResponseDto;
import fu.sep.apjf.entity.Authority;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.service.AdminService;
import fu.sep.apjf.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponseDto<List<UserResponseDto>>> getAllUsers(@AuthenticationPrincipal User admin) {
        List<UserResponseDto> users = adminService.getAllUsers(admin.getEmail());
        return ResponseEntity.ok(ApiResponseDto.ok("Danh sách tất cả users", users));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponseDto<UserResponseDto>> getUserById(@PathVariable Long userId) {
        UserResponseDto user = adminService.getUserById(userId);
        return ResponseEntity.ok(ApiResponseDto.ok("Thông tin chi tiết user", user));
    }

    @GetMapping("/authorities")
    public ResponseEntity<ApiResponseDto<List<Authority>>> getAllAuthorities() {
        List<Authority> authorities = adminService.getAllNonAdminAuthorities();
        return ResponseEntity.ok(ApiResponseDto.ok("Danh sách tất cả quyền", authorities));
    }

    @PatchMapping("/authorities")
    public ResponseEntity<ApiResponseDto<String>> updateUserAuthorities(
            @Valid @RequestBody UpdateUserAuthoritiesDto dto,
            @AuthenticationPrincipal User admin) {
        String message = adminService.updateUserAuthorities(dto, admin.getEmail());
        return ResponseEntity.ok(ApiResponseDto.ok(message));
    }


    @PatchMapping("/status")
    public ResponseEntity<ApiResponseDto<String>> updateUserStatus(
            @Valid @RequestBody UpdateUserStatusDto dto,
            @AuthenticationPrincipal User admin) {
        String message = adminService.updateUserStatus(dto, admin.getEmail());
        return ResponseEntity.ok(ApiResponseDto.ok(message));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponseDto<UserStatsResponseDto>> getUserStats() {
        UserStatsResponseDto stats = userService.getUserStats();
        return ResponseEntity.ok(ApiResponseDto.ok("Thống kê người dùng", stats));
    }

    @PreAuthorize("hasRole('STAFF')")
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponseDto<DashboardManagerResponseDto>> getDashboardStats() {
        DashboardManagerResponseDto stats = adminService.getDashboardData();
        return ResponseEntity.ok(ApiResponseDto.ok("Thống kê tổng quan dashboard", stats));
    }
}
