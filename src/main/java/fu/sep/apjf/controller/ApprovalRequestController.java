package fu.sep.apjf.controller;

import fu.sep.apjf.dto.ApiResponse;
import fu.sep.apjf.dto.ApprovalDecisionDto;
import fu.sep.apjf.dto.ApprovalRequestDto;
import fu.sep.apjf.entity.ApprovalRequest.TargetType;
import fu.sep.apjf.service.ApprovalRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/approval-requests")
@RequiredArgsConstructor
@Slf4j
public class ApprovalRequestController {

    private final ApprovalRequestService approvalRequestService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ApprovalRequestDto>>> getAll() {
        log.info("Manager requesting all approval requests");
        List<ApprovalRequestDto> requests = approvalRequestService.findAll();
        return ResponseEntity.ok(ApiResponse.ok("Danh sách tất cả approval requests", requests));
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<ApprovalRequestDto>>> getPending() {
        log.info("Quản lý yêu cầu danh sách approval requests đang chờ duyệt");
        List<ApprovalRequestDto> pendingRequests = approvalRequestService.findPending();
        return ResponseEntity.ok(ApiResponse.ok("Danh sách approval requests chờ duyệt", pendingRequests));
    }

    @GetMapping("/by-target-type/{targetType}")
    public ResponseEntity<ApiResponse<List<ApprovalRequestDto>>> getByTargetType(@PathVariable TargetType targetType) {
        log.info("Manager requesting approval requests for target type: {}", targetType);
        List<ApprovalRequestDto> requests = approvalRequestService.findByTargetType(targetType);
        return ResponseEntity.ok(ApiResponse.ok("Danh sách approval requests cho " + targetType, requests));
    }

    @GetMapping("/by-staff/{staffId}")
    public ResponseEntity<ApiResponse<List<ApprovalRequestDto>>> getByStaff(@PathVariable String staffId) {
        log.info("Manager requesting approval requests created by staff: {}", staffId);
        List<ApprovalRequestDto> requests = approvalRequestService.findByCreatedBy(staffId);
        return ResponseEntity.ok(ApiResponse.ok("Danh sách approval requests của staff " + staffId, requests));
    }

    @GetMapping("/by-manager/{managerId}")
    public ResponseEntity<ApiResponse<List<ApprovalRequestDto>>> getByManager(@PathVariable String managerId) {
        log.info("Requesting approval requests reviewed by manager: {}", managerId);
        List<ApprovalRequestDto> requests = approvalRequestService.findByReviewedBy(managerId);
        return ResponseEntity.ok(ApiResponse.ok("Danh sách approval requests đã duyệt bởi manager " + managerId, requests));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApprovalRequestDto>> getById(@PathVariable Integer id) {
        log.info("Manager requesting approval request detail for ID: {}", id);
        ApprovalRequestDto request = approvalRequestService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok("Chi tiết approval request", request));
    }

    @PutMapping("/{id}/process")
    public ResponseEntity<ApiResponse<ApprovalRequestDto>> processApproval(
            @PathVariable Integer id,
            @Valid @RequestBody ApprovalDecisionDto decision,
            @RequestHeader("X-User-Id") String managerId) {
        log.info("Manager {} processing approval request ID: {} with decision: {}", managerId, id, decision.decision());

        ApprovalRequestDto result = approvalRequestService.processApproval(id, decision, managerId);

        String message = decision.decision().name().equals("APPROVED")
                ? "Phê duyệt thành công"
                : "Từ chối thành công";

        return ResponseEntity.ok(ApiResponse.ok(message, result));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<ApprovalRequestDto>> approve(
            @PathVariable Integer id,
            @RequestBody(required = false) String feedback,
            @RequestHeader("X-User-Id") String managerId) {
        log.info("Manager {} approving approval request ID: {}", managerId, id);

        ApprovalDecisionDto decision = new ApprovalDecisionDto(
                fu.sep.apjf.entity.ApprovalRequest.Decision.APPROVED,
                feedback
        );

        ApprovalRequestDto result = approvalRequestService.processApproval(id, decision, managerId);
        return ResponseEntity.ok(ApiResponse.ok("Phê duyệt thành công", result));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<ApprovalRequestDto>> reject(
            @PathVariable Integer id,
            @RequestBody(required = false) String feedback,
            @RequestHeader("X-User-Id") String managerId) {
        log.info("Manager {} rejecting approval request ID: {}", managerId, id);

        ApprovalDecisionDto decision = new ApprovalDecisionDto(
                fu.sep.apjf.entity.ApprovalRequest.Decision.REJECTED,
                feedback
        );

        ApprovalRequestDto result = approvalRequestService.processApproval(id, decision, managerId);
        return ResponseEntity.ok(ApiResponse.ok("Từ chối thành công", result));
    }
}
