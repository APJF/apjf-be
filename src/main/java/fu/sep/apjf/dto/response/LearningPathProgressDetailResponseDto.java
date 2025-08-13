package fu.sep.apjf.dto.response;

import java.time.Instant;
import java.util.Set;

public record LearningPathProgressDetailResponseDto(
        Long learningPathId,
        String learningPathTitle,
        boolean completed,
        Instant completedAt,
        Set<CourseProgressResponseDto> courses
) {
}