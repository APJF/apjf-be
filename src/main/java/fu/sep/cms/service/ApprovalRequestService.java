package fu.sep.cms.service;

import fu.sep.cms.dto.ApprovalDecisionDto;
import fu.sep.cms.dto.ApprovalRequestDto;
import fu.sep.cms.dto.CreateApprovalRequestDto;
import fu.sep.cms.entity.ApprovalRequest;
import fu.sep.cms.entity.ApprovalRequest.Decision;
import fu.sep.cms.entity.ApprovalRequest.TargetType;
import fu.sep.cms.repository.ApprovalRequestRepository;
import fu.sep.cms.repository.CourseRepository;
import fu.sep.cms.repository.ChapterRepository;
import fu.sep.cms.repository.UnitRepository;
import fu.sep.cms.repository.MaterialRepository;
import fu.sep.cms.entity.Course;
import fu.sep.cms.entity.Chapter;
import fu.sep.cms.entity.Unit;
import fu.sep.cms.entity.Material;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for managing ApprovalRequest operations
 * Handles Manager approval/rejection workflow
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ApprovalRequestService {

    private final ApprovalRequestRepository approvalRequestRepository;
    private final CourseRepository courseRepository;
    private final ChapterRepository chapterRepository;
    private final UnitRepository unitRepository;
    private final MaterialRepository materialRepository;

    /* ---------- READ OPERATIONS ---------- */

    /**
     * Get all pending approval requests waiting for manager review
     */
    @Transactional(readOnly = true)
    public List<ApprovalRequestDto> findAllPending() {
        log.info("Fetching all pending approval requests");
        return approvalRequestRepository.findByDecision(Decision.PENDING)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Get all approval requests regardless of status
     */
    @Transactional(readOnly = true)
    public List<ApprovalRequestDto> findAll() {
        log.info("Fetching all approval requests");
        return approvalRequestRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Get approval requests by target type
     */
    @Transactional(readOnly = true)
    public List<ApprovalRequestDto> findByTargetType(TargetType targetType) {
        log.info("Fetching approval requests for target type: {}", targetType);
        return approvalRequestRepository.findByTargetType(targetType)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Get specific approval request by ID
     */
    @Transactional(readOnly = true)
    public ApprovalRequestDto findById(Integer id) {
        log.info("Fetching approval request with ID: {}", id);
        ApprovalRequest approvalRequest = approvalRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy approval request với ID: " + id));
        return toDto(approvalRequest);
    }

    /**
     * Get approval requests created by specific staff member
     */
    @Transactional(readOnly = true)
    public List<ApprovalRequestDto> findByCreatedBy(String staffId) {
        log.info("Fetching approval requests created by: {}", staffId);
        return approvalRequestRepository.findByCreatedBy(staffId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Get approval requests reviewed by specific manager
     */
    @Transactional(readOnly = true)
    public List<ApprovalRequestDto> findByReviewedBy(String managerId) {
        log.info("Fetching approval requests reviewed by: {}", managerId);
        return approvalRequestRepository.findByReviewedBy(managerId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /* ---------- STAFF OPERATIONS ---------- */

    /**
     * Staff creates a new approval request (kept for compatibility but not used in auto-flow)
     */
    public ApprovalRequestDto createApprovalRequest(CreateApprovalRequestDto dto, String staffId) {
        log.info("Staff {} creating approval request for {} with ID: {}",
                staffId, dto.targetType(), dto.targetId());

        // Check if there's already a pending request for this target
        Optional<ApprovalRequest> existingPending = approvalRequestRepository
                .findPendingRequestByTargetId(dto.targetId());

        if (existingPending.isPresent()) {
            throw new IllegalArgumentException("Đã có approval request đang chờ duyệt cho đối tượng này");
        }

        ApprovalRequest approvalRequest = ApprovalRequest.builder()
                .targetType(dto.targetType())
                .requestType(dto.requestType())
                .decision(Decision.PENDING)
                .createdBy(staffId)
                .createdAt(Instant.now())
                .build();

        // For now, we'll rely on the targetType field - entity relationships will be set later
        ApprovalRequest saved = approvalRequestRepository.save(approvalRequest);

        log.info("Successfully created approval request ID: {} for target: {}",
                saved.getId(), dto.targetId());
        return toDto(saved);
    }

    /* ---------- AUTO-CREATE APPROVAL REQUEST OPERATIONS ---------- */

    /**
     * Automatically create approval request when staff creates/updates content
     * This method is called internally by other services
     */
    public void autoCreateApprovalRequest(TargetType targetType, String targetId,
                                        ApprovalRequest.RequestType requestType, String staffId) {
        log.info("Auto-creating approval request for {} {} by staff {}", requestType, targetType, staffId);

        Optional<ApprovalRequest> existingPending = approvalRequestRepository
                .findPendingRequestByTargetId(targetId);

        if (existingPending.isPresent()) {
            log.warn("Approval request already exists for target {}, skipping auto-creation", targetId);
            return;
        }

        ApprovalRequest.ApprovalRequestBuilder builder = ApprovalRequest.builder()
                .targetType(targetType)
                .requestType(requestType)
                .decision(Decision.PENDING)
                .createdBy(staffId)
                .createdAt(Instant.now());

        switch (targetType) {
            case COURSE -> {
                Course course = courseRepository.findById(targetId)
                        .orElseThrow(() -> new EntityNotFoundException("Course not found: " + targetId));
                builder.course(course);
            }
            case CHAPTER -> {
                Chapter chapter = chapterRepository.findById(targetId)
                        .orElseThrow(() -> new EntityNotFoundException("Chapter not found: " + targetId));
                builder.chapter(chapter);
            }
            case UNIT -> {
                Unit unit = unitRepository.findById(targetId)
                        .orElseThrow(() -> new EntityNotFoundException("Unit not found: " + targetId));
                builder.unit(unit);
            }
            case MATERIAL -> {
                Material material = materialRepository.findById(targetId)
                        .orElseThrow(() -> new EntityNotFoundException("Material not found: " + targetId));
                builder.material(material);
            }
            default -> throw new IllegalArgumentException("Unknown targetType: " + targetType);
        }

        ApprovalRequest approvalRequest = builder.build();
        approvalRequestRepository.save(approvalRequest);

        log.info("Successfully auto-created approval request for {} {}", targetType, targetId);
    }

    /**
     * Manager approves or rejects an approval request with feedback
     */
    public ApprovalRequestDto processApproval(Integer id, @Valid ApprovalDecisionDto decision, String managerId) {
        log.info("Processing approval request ID: {} by manager: {} with decision: {}",
                id, managerId, decision.decision());

        ApprovalRequest approvalRequest = approvalRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy approval request với ID: " + id));

        // Validate current status
        if (approvalRequest.getDecision() != Decision.PENDING) {
            throw new IllegalArgumentException("Approval request này đã được xử lý với trạng thái: " + approvalRequest.getDecision());
        }

        // Validate decision input
        if (decision.decision() != Decision.APPROVED && decision.decision() != Decision.REJECTED) {
            throw new IllegalArgumentException("Quyết định phải là APPROVED hoặc REJECTED");
        }

        // Update approval request
        approvalRequest.setDecision(decision.decision());
        approvalRequest.setFeedback(decision.feedback());
        approvalRequest.setReviewedBy(managerId);
        approvalRequest.setReviewedAt(Instant.now());

        ApprovalRequest saved = approvalRequestRepository.save(approvalRequest);

        log.info("Successfully processed approval request ID: {} with decision: {}", id, decision.decision());
        return toDto(saved);
    }

    /* ---------- MAPPING HELPERS ---------- */

    /**
     * Convert ApprovalRequest entity to DTO with target information
     */
    private ApprovalRequestDto toDto(ApprovalRequest ar) {
        String targetId = null;
        String targetTitle = null;

        // Determine target ID and title based on target type
        switch (ar.getTargetType()) {
            case COURSE -> {
                if (ar.getCourse() != null) {
                    targetId = ar.getCourse().getId();
                    targetTitle = ar.getCourse().getTitle();
                }
            }
            case CHAPTER -> {
                if (ar.getChapter() != null) {
                    targetId = ar.getChapter().getId();
                    targetTitle = ar.getChapter().getTitle();
                }
            }
            case UNIT -> {
                if (ar.getUnit() != null) {
                    targetId = ar.getUnit().getId();
                    targetTitle = ar.getUnit().getTitle();
                }
            }
            case MATERIAL -> {
                if (ar.getMaterial() != null) {
                    targetId = ar.getMaterial().getId();
                    targetTitle = ar.getMaterial().getDescription(); // Material uses description as title
                }
            }
        }

        return new ApprovalRequestDto(
                ar.getId(),
                ar.getTargetType(),
                targetId,
                targetTitle,
                ar.getRequestType(),
                ar.getDecision(),
                ar.getFeedback(),
                ar.getCreatedBy(),
                ar.getCreatedAt(),
                ar.getReviewedBy(),
                ar.getReviewedAt()
        );
    }
}
