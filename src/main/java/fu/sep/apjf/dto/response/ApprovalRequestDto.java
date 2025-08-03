package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.ApprovalRequest.Decision;
import fu.sep.apjf.entity.ApprovalRequest.RequestType;
import fu.sep.apjf.entity.ApprovalRequest.TargetType;

import java.time.Instant;

public record ApprovalRequestDto(
        Integer id,
        TargetType targetType,
        String targetId,
        RequestType requestType,
        Decision decision,
        String feedback,
        String createdBy,
        Instant createdAt,
        String reviewedBy,
        Instant reviewedAt
) {
}
