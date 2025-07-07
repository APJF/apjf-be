package fu.sep.apjf.dto;

import fu.sep.apjf.entity.ApprovalRequest.Decision;
import fu.sep.apjf.entity.ApprovalRequest.RequestType;
import fu.sep.apjf.entity.ApprovalRequest.TargetType;

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
