package fu.sep.apjf.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

public record LearningPathProgressDetailResponseDto(
        Long learningPathId,
        String learningPathTitle,
        boolean completed,
        LocalDateTime completedAt,
        Set<CourseProgressResponseDto> courses
) {}