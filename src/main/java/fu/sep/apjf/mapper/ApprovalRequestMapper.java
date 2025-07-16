package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.ApprovalRequestDto;
import fu.sep.apjf.entity.ApprovalRequest;

import java.util.List;

public final class ApprovalRequestMapper {

    private ApprovalRequestMapper() {
        // Private constructor to prevent instantiation
    }

    public static ApprovalRequestDto toDto(ApprovalRequest request) {
        if (request == null) {
            return null;
        }

        String targetId = null;
        String targetTitle = null;

        // Determine target ID and title based on the target type
        switch (request.getTargetType()) {
            case COURSE:
                if (request.getCourse() != null) {
                    targetId = request.getCourse().getId();
                    targetTitle = request.getCourse().getTitle();
                }
                break;
            case CHAPTER:
                if (request.getChapter() != null) {
                    targetId = request.getChapter().getId();
                    targetTitle = request.getChapter().getTitle();
                }
                break;
            case UNIT:
                if (request.getUnit() != null) {
                    targetId = request.getUnit().getId();
                    targetTitle = request.getUnit().getTitle();
                }
                break;
            case MATERIAL:
                if (request.getMaterial() != null) {
                    targetId = request.getMaterial().getId();
                    targetTitle = request.getMaterial().getDescription();
                }
                break;
        }

        String createdBy = null;
        if (request.getCreator() != null) {
            createdBy = request.getCreator().getUsername();
        }

        String reviewedBy = null;
        if (request.getReviewer() != null) {
            reviewedBy = request.getReviewer().getUsername();
        }

        return new ApprovalRequestDto(
                request.getId(),
                request.getTargetType(),
                targetId,
                targetTitle,
                request.getRequestType(),
                request.getDecision(),
                request.getFeedback(),
                createdBy,
                request.getCreatedAt(),
                reviewedBy,
                request.getReviewedAt()
        );
    }

    public static ApprovalRequest toEntity(ApprovalRequestDto dto) {
        if (dto == null) {
            return null;
        }

        ApprovalRequest request = new ApprovalRequest();
        request.setId(dto.id());
        request.setTargetType(dto.targetType());
        request.setRequestType(dto.requestType());
        request.setDecision(dto.decision());
        request.setFeedback(dto.feedback());
        request.setCreatedAt(dto.createdAt());
        request.setReviewedAt(dto.reviewedAt());

        // Note: The entity relationships (course, chapter, unit, material)
        // and users (creator, reviewer) cannot be set from the DTO alone.
        // These would typically be set in the service layer.

        return request;
    }

    /**
     * Convert a list of ApprovalRequest entities to a list of ApprovalRequestDto records
     */
    public static List<ApprovalRequestDto> toDtoList(List<ApprovalRequest> requests) {
        if (requests == null) {
            return List.of();
        }

        return requests.stream()
                .map(ApprovalRequestMapper::toDto)
                .toList();
    }
}
