package fu.sep.apjf.controller;

import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.NotificationResponseDto;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.service.NotificationService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<NotificationResponseDto>>> getAllByUser(
            @AuthenticationPrincipal User user) {
        List<NotificationResponseDto> notifications = notificationService.getByUserId(user.getId());
        return ResponseEntity.ok(ApiResponseDto.ok("Danh sách thông báo", notifications));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponseDto<Void>> markAsRead(
            @PathVariable @NotNull Long id,
            @AuthenticationPrincipal User user) {

        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponseDto.ok("Đã đánh dấu thông báo là đã đọc", null));
    }
}

