package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;

import java.time.Instant;
import java.util.List;

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
        EnumClass.PathStatus status,
        Float duration,
        Long userId,
        String username,
        Instant createdAt,
        Instant lastUpdatedAt,
        List<CourseOrderDto> courses
) {
    public static LearningPathResponseDto of(Long id, String title, String description, EnumClass.Level targetLevel,
                                             String primaryGoal, String focusSkill, EnumClass.PathStatus pathStatus,
                                             Float duration, Long userId, String username,
                                             Instant createdAt, Instant lastUpdatedAt, List<CourseOrderDto> courses) {
        return new LearningPathResponseDto(id, title, description, targetLevel, primaryGoal, focusSkill,
                pathStatus, duration, userId, username, createdAt, lastUpdatedAt, courses);
    }
}