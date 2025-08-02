package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.ApprovalDecisionDto;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.ApprovalRequestDto;
import fu.sep.apjf.entity.ApprovalRequest;
import fu.sep.apjf.entity.ApprovalRequest.TargetType;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.service.ApprovalRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approval-requests")
@RequiredArgsConstructor
@Slf4j
public class ApprovalRequestController {

    private final ApprovalRequestService approvalRequestService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<List<ApprovalRequestDto>>> getAll(
            @RequestParam(required = false) ApprovalRequest.Decision decision,
            @RequestParam(required = false) TargetType targetType,
            @RequestParam(required = false) String createdBy,
            @RequestParam(required = false) String reviewedBy) {

        log.info("Fetching approval requests with filters - decision: {}, targetType: {}, createdBy: {}, reviewedBy: {}",
                decision, targetType, createdBy, reviewedBy);

        List<ApprovalRequestDto> requests;
        String message = "Danh sách approval requests";

        if (decision != null) {
            requests = approvalRequestService.findByDecision(decision);
            message = "Danh sách approval requests " + getDecisionMessage(decision);
        } else if (targetType != null) {
            requests = approvalRequestService.findByTargetType(targetType);
            message = "Danh sách approval requests cho " + targetType;
        } else if (createdBy != null) {
            requests = approvalRequestService.findByCreatedBy(createdBy);
            message = "Danh sách approval requests của staff " + createdBy;
        } else if (reviewedBy != null) {
            requests = approvalRequestService.findByReviewedBy(reviewedBy);
            message = "Danh sách approval requests đã duyệt bởi manager " + reviewedBy;
        } else {
            requests = approvalRequestService.findAll();
        }

        return ResponseEntity.ok(ApiResponseDto.ok(message, requests));
    }

    private String getDecisionMessage(ApprovalRequest.Decision decision) {
        return switch (decision) {
            case PENDING -> "chờ duyệt";
            case APPROVED -> "đã duyệt";
            case REJECTED -> "đã từ chối";
        };
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDto<ApprovalRequestDto>> getById(@PathVariable Integer id) {
        log.info("Requesting approval request detail for ID: {}", id);
        ApprovalRequestDto request = approvalRequestService.findById(id);
        return ResponseEntity.ok(ApiResponseDto.ok("Chi tiết approval request", request));
    }

    @PutMapping("/{id}/decision")
    public ResponseEntity<ApiResponseDto<ApprovalRequestDto>> makeDecision(
            @PathVariable Integer id,
            @Valid @RequestBody ApprovalDecisionDto decision,
            @AuthenticationPrincipal User user) {
        log.info("Manager {} processing approval request ID: {} with decision: {}",
                user.getUsername(), id, decision.decision());

        ApprovalRequestDto result = approvalRequestService.processApproval(id, decision, user.getId());

        String message = decision.decision().name().equals("APPROVED")
                ? "Phê duyệt thành công"
                : "Từ chối thành công";

        return ResponseEntity.ok(ApiResponseDto.ok(message, result));
    }
}
