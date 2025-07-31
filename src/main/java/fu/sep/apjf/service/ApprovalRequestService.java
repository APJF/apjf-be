package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.ApprovalDecisionDto;
import fu.sep.apjf.dto.response.ApprovalRequestDto;
import fu.sep.apjf.entity.*;
import fu.sep.apjf.entity.ApprovalRequest.Decision;
import fu.sep.apjf.entity.ApprovalRequest.TargetType;
import fu.sep.apjf.mapper.ApprovalRequestMapper;
import fu.sep.apjf.repository.*;
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
    private final UserRepository userRepository;

    /* ---------- READ OPERATIONS ---------- */

    @Transactional(readOnly = true)
    public List<ApprovalRequestDto> findPending() {
        log.info("Lấy danh sách tất cả yêu cầu phê duyệt đang chờ");
        return approvalRequestRepository.findByDecision(Decision.PENDING)
                .stream()
                .map(ApprovalRequestMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ApprovalRequestDto> findAll() {
        log.info("Lấy danh sách tất cả yêu cầu phê duyệt");
        return ApprovalRequestMapper.toDtoList(approvalRequestRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<ApprovalRequestDto> findByTargetType(TargetType targetType) {
        log.info("Lấy danh sách yêu cầu phê duyệt theo loại đối tượng: {}", targetType);
        return ApprovalRequestMapper.toDtoList(approvalRequestRepository.findByTargetType(targetType));
    }

    @Transactional(readOnly = true)
    public ApprovalRequestDto findById(Integer id) {
        log.info("Lấy chi tiết yêu cầu phê duyệt với ID: {}", id);
        ApprovalRequest approvalRequest = approvalRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy yêu cầu phê duyệt"));
        return ApprovalRequestMapper.toDto(approvalRequest);
    }

    @Transactional(readOnly = true)
    public List<ApprovalRequestDto> findByCreatedBy(String staffId) {
        log.info("Lấy danh sách yêu cầu phê duyệt được tạo bởi: {}", staffId);
        User creator = userRepository.findById(Long.parseLong(staffId))
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy user: " + staffId));
        return approvalRequestRepository.findByCreator(creator)
                .stream()
                .map(ApprovalRequestMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ApprovalRequestDto> findByReviewedBy(String managerId) {
        log.info("Lấy danh sách yêu cầu phê duyệt được duyệt bởi: {}", managerId);
        User reviewer = userRepository.findById(Long.parseLong(managerId))
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy manager: " + managerId));
        return approvalRequestRepository.findByReviewer(reviewer)
                .stream()
                .map(ApprovalRequestMapper::toDto)
                .toList();
    }

    public void autoCreateApprovalRequest(TargetType targetType, String targetId,
                                          ApprovalRequest.RequestType requestType, Long staffId) {
        log.info("Tự động tạo yêu cầu phê duyệt cho {} {} bởi nhân viên {}", requestType, targetType, staffId);

        // Tìm user creator
        User creator = userRepository.findById(staffId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy user: " + staffId));

        Optional<ApprovalRequest> existingPending = approvalRequestRepository
                .findPendingRequestByTargetId(targetId);

        if (existingPending.isPresent()) {
            log.warn("Yêu cầu phê duyệt đã tồn tại cho đối tượng {}, bỏ qua việc tạo tự động", targetId);
            return;
        }

        ApprovalRequest.ApprovalRequestBuilder builder = ApprovalRequest.builder()
                .targetType(targetType)
                .requestType(requestType)
                .decision(Decision.PENDING)
                .creator(creator)
                .createdAt(Instant.now());

        switch (targetType) {
            case COURSE -> {
                Course course = courseRepository.findById(targetId)
                        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khóa học: " + targetId));
                builder.course(course);
            }
            case CHAPTER -> {
                Chapter chapter = chapterRepository.findById(targetId)
                        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chương học: " + targetId));
                builder.chapter(chapter);
            }
            case UNIT -> {
                Unit unit = unitRepository.findById(targetId)
                        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn vị học tập: " + targetId));
                builder.unit(unit);
            }
            case MATERIAL -> {
                Material material = materialRepository.findById(targetId)
                        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài liệu: " + targetId));
                builder.material(material);
            }
            default -> throw new IllegalArgumentException("Unknown targetType: " + targetType);
        }

        ApprovalRequest approvalRequest = builder.build();
        approvalRequestRepository.save(approvalRequest);

        log.info("Tự động tạo yêu cầu phê duyệt thành công cho {} {}", targetType, targetId);
    }

    public ApprovalRequestDto processApproval(Integer id, @Valid ApprovalDecisionDto decision, Long managerId) {
        log.info("Xử lý yêu cầu phê duyệt ID: {} bởi quản lý: {} với quyết định: {}",
                id, managerId, decision.decision());

        ApprovalRequest approvalRequest = approvalRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy yêu cầu phê duyệt với ID: " + id));

        // Tìm user reviewer
        User reviewer = userRepository.findById(managerId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy manager: " + managerId));

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
        approvalRequest.setReviewer(reviewer);
        approvalRequest.setReviewedAt(Instant.now());

        ApprovalRequest saved = approvalRequestRepository.save(approvalRequest);

        log.info("Xử lý yêu cầu phê duyệt thành công ID: {} với quyết định: {}", id, decision.decision());
        return ApprovalRequestMapper.toDto(saved);
    }
}
