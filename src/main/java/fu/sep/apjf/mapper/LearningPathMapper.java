package fu.sep.apjf.mapper;

import fu.sep.apjf.dto.request.LearningPathRequestDto;
import fu.sep.apjf.dto.response.LearningPathResponseDto;
import fu.sep.apjf.entity.EnumClass;
import fu.sep.apjf.entity.LearningPath;
import fu.sep.apjf.entity.User;

import java.time.LocalDateTime;

/**
 * Mapper class để chuyển đổi giữa LearningPath entity và DTO
 */
public final class LearningPathMapper {

    private LearningPathMapper() {
        // Private constructor to prevent instantiation
    }

    public static LearningPathResponseDto toResponseDto(LearningPath learningPath) {
        if (learningPath == null) {
            return null;
        }

        // Add null safety checks for user
        Long userId = learningPath.getUser() != null ? learningPath.getUser().getId() : null;
        String username = learningPath.getUser() != null ? learningPath.getUser().getUsername() : null;

        // Handle type conversions
        EnumClass.Level targetLevel = learningPath.getTargetLevel() != null ?
                EnumClass.Level.valueOf(learningPath.getTargetLevel()) : null;

        // Convert PathStatus to Status (if needed) or pass null if status is null
        EnumClass.Status status = learningPath.getStatus() != null ?
                EnumClass.Status.valueOf(learningPath.getStatus().name()) : null;

        // Convert BigDecimal to Integer
        Integer duration = learningPath.getDuration() != null ?
                learningPath.getDuration().intValue() : null;

        return new LearningPathResponseDto(
                learningPath.getId(),
                learningPath.getTitle(),
                learningPath.getDescription(),
                targetLevel,
                learningPath.getPrimaryGoal(),
                learningPath.getFocusSkill(),
                status,
                duration,
                userId,
                username,
                learningPath.getCreatedAt(),
                learningPath.getLastUpdatedAt()
        );
    }

    public static LearningPath toEntity(LearningPathRequestDto dto, User user) {
        if (dto == null) {
            return null;
        }

        return LearningPath.builder()
                .title(dto.title())
                .description(dto.description())
                .targetLevel(dto.targetLevel() != null ? dto.targetLevel().name() : null)
                .primaryGoal(dto.primaryGoal())
                .focusSkill(dto.focusSkill())
                .status(dto.status() != null ? EnumClass.PathStatus.valueOf(dto.status().name()) : null)
                .duration(dto.duration() != null ? new java.math.BigDecimal(dto.duration()) : null)
                .user(user)
                .createdAt(LocalDateTime.now())
                .lastUpdatedAt(LocalDateTime.now())
                .build();
    }
}
