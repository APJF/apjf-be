package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;

import java.time.LocalDateTime;

/**
 * DTO đại diện cho phản hồi thông tin lộ trình học tập
 */
public record LearningPathResponseDto(
        Long id,
        String title,
        String description,
        EnumClass.Level targetLevel,
        String primaryGoal,
        String focusSkill,
        EnumClass.PathStatus pathStatus,
        Integer duration,
        Long userId,
        String username,
        LocalDateTime createdAt,
        LocalDateTime lastUpdatedAt
) {
    public static LearningPathResponseDto of(Long id, String title, String description, EnumClass.Level targetLevel,
                                     String primaryGoal, String focusSkill, EnumClass.PathStatus pathStatus,
                                     Integer duration, Long userId, String username,
                                     LocalDateTime createdAt, LocalDateTime lastUpdatedAt) {
        return new LearningPathResponseDto(id, title, description, targetLevel, primaryGoal, focusSkill,
                                        pathStatus, duration, userId, username, createdAt, lastUpdatedAt);
    }
}
