package fu.sep.cms.dto;

import fu.sep.cms.entity.ApprovalRequest.Decision;
import fu.sep.cms.entity.ApprovalRequest.RequestType;
import fu.sep.cms.entity.ApprovalRequest.TargetType;

import java.time.Instant;

/**
 * DTO for displaying ApprovalRequest information to clients
 */
public record ApprovalRequestDto(
        Integer id,
        TargetType targetType,
        String targetId,
        String targetTitle,
        RequestType requestType,
        Decision decision,
        String feedback,
        String createdBy,
        Instant createdAt,
        String reviewedBy,
        Instant reviewedAt
) {
}
