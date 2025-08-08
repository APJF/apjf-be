package fu.sep.apjf.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

public record CourseProgressResponseDto(
        String courseId,
        String courseTitle,
        boolean completed,
        LocalDateTime completedAt,
        Set<ChapterProgressResponseDto> chapterProgresses
) {
}