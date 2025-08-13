package fu.sep.apjf.dto.response;

import java.time.Instant;
import java.util.Set;

public record CourseProgressResponseDto(
        String courseId,
        String courseTitle,
        boolean completed,
        Instant completedAt,
        Set<ChapterProgressResponseDto> chapterProgresses
) {
}