package fu.sep.apjf.dto.response;

import java.time.Instant;

public record CourseDetailProgressResponseDto(
        String courseId,
        String courseTitle,
        boolean completed,
        Instant completedAt
) {}