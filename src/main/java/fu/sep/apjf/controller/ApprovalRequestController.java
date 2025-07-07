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

/**
 * REST Controller for managing ApprovalRequest operations
 * Provides endpoints for Manager to review and approve/reject requests
 */
@RestController
@RequestMapping("/api/approval-requests")
@RequiredArgsConstructor
@Slf4j
public class ApprovalRequestController {

    private final ApprovalRequestService approvalRequestService;

    /* -------- GET /api/approval-requests - Lấy tất cả approval requests -------- */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ApprovalRequestDto>>> getAll() {
        log.info("Manager requesting all approval requests");
        List<ApprovalRequestDto> requests = approvalRequestService.findAll();
        return ResponseEntity.ok(
                ApiResponse.ok("Danh sách tất cả approval requests", requests));
    }

    /* -------- GET /api/approval-requests/pending - Lấy các approval requests đang chờ duyệt -------- */
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<ApprovalRequestDto>>> getPending() {
        log.info("Quản lý yêu cầu danh sách approval requests đang chờ duyệt");
        List<ApprovalRequestDto> pendingRequests = approvalRequestService.findPending();
        return ResponseEntity.ok(
                ApiResponse.ok("Danh sách approval requests chờ duyệt", pendingRequests));
    }

    /* -------- GET /api/approval-requests/by-target-type/{targetType} - Lấy theo loại đối tượng -------- */
    @GetMapping("/by-target-type/{targetType}")
    public ResponseEntity<ApiResponse<List<ApprovalRequestDto>>> getByTargetType(@PathVariable TargetType targetType) {
        log.info("Manager requesting approval requests for target type: {}", targetType);
        List<ApprovalRequestDto> requests = approvalRequestService.findByTargetType(targetType);
        return ResponseEntity.ok(
                ApiResponse.ok("Danh sách approval requests cho " + targetType, requests));
    }

    /* -------- GET /api/approval-requests/by-staff/{staffId} - Lấy theo staff tạo -------- */
    @GetMapping("/by-staff/{staffId}")
    public ResponseEntity<ApiResponse<List<ApprovalRequestDto>>> getByStaff(@PathVariable String staffId) {
        log.info("Manager requesting approval requests created by staff: {}", staffId);
        List<ApprovalRequestDto> requests = approvalRequestService.findByCreatedBy(staffId);
        return ResponseEntity.ok(
                ApiResponse.ok("Danh sách approval requests của staff " + staffId, requests));
    }

    /* -------- GET /api/approval-requests/by-manager/{managerId} - Lấy theo manager duyệt -------- */
    @GetMapping("/by-manager/{managerId}")
    public ResponseEntity<ApiResponse<List<ApprovalRequestDto>>> getByManager(@PathVariable String managerId) {
        log.info("Requesting approval requests reviewed by manager: {}", managerId);
        List<ApprovalRequestDto> requests = approvalRequestService.findByReviewedBy(managerId);
        return ResponseEntity.ok(
                ApiResponse.ok("Danh sách approval requests đã duyệt bởi manager " + managerId, requests));
    }

    /* -------- GET /api/approval-requests/{id} - Lấy chi tiết approval request -------- */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApprovalRequestDto>> getById(@PathVariable Integer id) {
        log.info("Manager requesting approval request detail for ID: {}", id);
        ApprovalRequestDto request = approvalRequestService.findById(id);
        return ResponseEntity.ok(
                ApiResponse.ok("Chi tiết approval request", request));
    }

    /* -------- PUT /api/approval-requests/{id}/process - Manager approve/reject -------- */
    @PutMapping("/{id}/process")
    public ResponseEntity<ApiResponse<ApprovalRequestDto>> processApproval(
            @PathVariable Integer id,
            @Valid @RequestBody ApprovalDecisionDto decision,
            @RequestHeader("X-User-Id") String managerId) {

        log.info("Manager {} processing approval request ID: {} with decision: {}",
                managerId, id, decision.decision());

        ApprovalRequestDto result = approvalRequestService.processApproval(id, decision, managerId);

        String message = decision.decision().name().equals("APPROVED")
                ? "Phê duyệt thành công"
                : "Từ chối thành công";

        return ResponseEntity.ok(
                ApiResponse.ok(message, result));
    }

    /* -------- PUT /api/approval-requests/{id}/approve - Shortcut for approve -------- */
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
        return ResponseEntity.ok(
                ApiResponse.ok("Phê duyệt thành công", result));
    }

    /* -------- PUT /api/approval-requests/{id}/reject - Shortcut for reject -------- */
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
        return ResponseEntity.ok(
                ApiResponse.ok("Từ chối thành công", result));
    }
}
