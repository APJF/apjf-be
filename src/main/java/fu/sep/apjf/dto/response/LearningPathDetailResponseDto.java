package fu.sep.apjf.dto.response;

import fu.sep.apjf.entity.EnumClass;

import java.time.Instant;
import java.util.List;

public record LearningPathDetailResponseDto(
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
        boolean isCompleted,
        float percent,
        List<CourseDetailResponseDto> courses
){
}
