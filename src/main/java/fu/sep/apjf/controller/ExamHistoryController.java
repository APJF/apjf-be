package fu.sep.apjf.controller;

import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.ExamHistoryResponseDto;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.service.ExamHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/student/history")
@RequiredArgsConstructor
public class ExamHistoryController {

    private final ExamHistoryService examHistoryService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<ExamHistoryResponseDto>>> getHistory(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponseDto.ok("Lịch sử bài thi", examHistoryService.getHistoryByUserId(user.getId())));
    }
}

